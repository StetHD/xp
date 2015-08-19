package com.enonic.xp.core.impl.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.Input;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.util.GeoPoint;

import static com.enonic.xp.media.MediaInfo.GPS_INFO;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;

final class ImageContentProcessor
{
    private static final ImmutableMap<String, String> FIELD_CONFORMITY_MAP = ImmutableMap.<String, String>builder().
        put( "tiffImagelength", "imageHeight" ).
        put( "tiffImagewidth", "imageWidth" ).
        put( "exposureBiasValue", "exposureBias" ).
        put( "FNumber", "aperture" ).
        put( "exposureTime", "shutterTime" ).
        put( "subjectDistanceRange", "focusDistance" ).
        put( "gpsAltitude", "altitude" ).
        put( "gpsImgDirection", "direction" ).
        put( "whiteBalanceMode", "whiteBalance" ).
        put( "isoSpeedRatings", "iso" ).
        build();

    private final MixinService mixinService;

    private final ContentService contentService;

    private final MediaInfo mediaInfo;

    private final ContentType contentType;

    private ImageContentProcessor( final Builder builder )
    {
        this.mediaInfo = builder.mediaInfo;
        this.contentType = builder.contentType;
        this.mixinService = builder.mixinService;
        this.contentService = builder.contentService;
    }

    public CreateContentParams processCreate( final CreateContentParams params )
    {
        Preconditions.checkArgument( params.getType().isImageMedia(),
                                     "This processor only accepts [" + ContentTypeName.imageMedia() + "]: " + params.getType() );

        final CreateAttachments originalAttachments = params.getCreateAttachments();
        Preconditions.checkArgument( originalAttachments.getSize() == 1, "Expected only one attachment" );

        final CreateAttachment sourceAttachment = originalAttachments.first();

        final Mixins contentMixins = mixinService.getByContentType( contentType );
        ExtraDatas extraDatas = null;
        if ( mediaInfo != null )
        {
            extraDatas = extractMetadata( mediaInfo, contentMixins );
        }

        final CreateAttachments.Builder builder = CreateAttachments.create();
        builder.add( sourceAttachment );

        return CreateContentParams.create( params ).
            createAttachments( builder.build() ).extraDatas( extraDatas ).
            build();
    }

    public ProcessUpdateResult processUpdate( final UpdateContentParams params, final CreateAttachments createAttachments )
    {
        final CreateAttachments processedCreateAttachments;
        if ( createAttachments != null && createAttachments.getSize() == 1 )
        {
            final CreateAttachment sourceAttachment = createAttachments.first();
            final CreateAttachments.Builder builder = CreateAttachments.create();
            builder.add( sourceAttachment );

            processedCreateAttachments = builder.build();
        }
        else
        {
            processedCreateAttachments = createAttachments;
        }

        final ContentEditor editor;
        if ( mediaInfo != null )
        {
            editor = editable -> {

                Mixins contentMixins = mixinService.getByContentType( contentType );
                editable.extraDatas = extractMetadata( mediaInfo, contentMixins );

            };
        }
        else
        {
            editor = editable -> {

                if ( !contentType.getName().isDescendantOfMedia() )
                {
                    return;
                }
                editable.extraDatas = updateImageMetadata( editable );

            };
        }
        return new ProcessUpdateResult( processedCreateAttachments, editor );
    }

    private ExtraDatas updateImageMetadata( final EditableContent editable )
    {
        final Media media = (Media) editable.source;
        final Attachment mediaAttachment = media.getMediaAttachment();
        if ( mediaAttachment == null )
        {
            return editable.extraDatas;
        }

        final ByteSource binary = contentService.getBinary( editable.source.getId(), mediaAttachment.getBinaryReference() );
        if ( binary == null )
        {
            return editable.extraDatas;
        }

        final BufferedImage image = toBufferedImage( binary );
        final Cropping cropping = media.getCropping();

        final long byteSize;
        final long imageWidth;
        final long imageHeight;
        final long imageSize;
        if ( cropping == null || cropping.isUnmodified() )
        {
            byteSize = mediaAttachment.getSize();
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            imageSize = imageWidth * imageHeight;
        }
        else
        {
            final BufferedImage croppedImage = cropImage( image, cropping );
            byteSize = getImageByteSize( croppedImage, mediaAttachment.getExtension() );
            imageWidth = croppedImage.getWidth();
            imageHeight = croppedImage.getHeight();
            imageSize = imageWidth * imageHeight;
        }

        ExtraData extraData = editable.extraDatas.getMetadata( MediaInfo.IMAGE_INFO_METADATA_NAME );
        if ( extraData != null )
        {
            final PropertyTree xData = extraData.getData();
            xData.setLong( IMAGE_INFO_PIXEL_SIZE, imageSize );
            xData.setLong( IMAGE_INFO_IMAGE_HEIGHT, imageHeight );
            xData.setLong( IMAGE_INFO_IMAGE_WIDTH, imageWidth );
            xData.setLong( MEDIA_INFO_BYTE_SIZE, byteSize );
        }

        return editable.extraDatas;
    }

    private BufferedImage toBufferedImage( final ByteSource source )
    {
        try
        {
            return ImageIO.read( source.openStream() );
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Failed to read BufferedImage from InputStream" ).withCause( e );
        }
    }

    private BufferedImage cropImage( final BufferedImage image, final Cropping cropping )
    {
        final double width = image.getWidth();
        final double height = image.getHeight();
        return image.getSubimage( (int) ( width * cropping.left() ), (int) ( height * cropping.top() ), (int) ( width * cropping.width() ),
                                  (int) ( height * cropping.height() ) );
    }

    private long getImageByteSize( final BufferedImage image, final String format )
    {
        final SizeCounterOutputStream output = new SizeCounterOutputStream();
        try
        {
            ImageIO.write( image, format, output );
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Failed to write BufferedImage to InputStream" ).withCause( e );
        }
        return output.size();
    }

    private ExtraDatas extractMetadata( MediaInfo mediaInfo, Mixins mixins )
    {
        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        final Map<MixinName, ExtraData> metadataMap = new HashMap<>();

        for ( Map.Entry<String, Collection<String>> entry : mediaInfo.getMetadata().asMap().entrySet() )
        {
            for ( Mixin mixin : mixins )
            {
                final String formItemName = getConformityName( entry.getKey() );
                final FormItem formItem = mixin.getForm().getFormItems().getItemByName( formItemName );
                if ( formItem == null )
                {
                    continue;
                }

                ExtraData extraData = metadataMap.get( mixin.getName() );
                if ( extraData == null )
                {
                    extraData = new ExtraData( mixin.getName(), new PropertyTree() );
                    metadataMap.put( mixin.getName(), extraData );
                    extradatasBuilder.add( extraData );
                }
                if ( FormItemType.INPUT.equals( formItem.getType() ) )
                {
                    Input input = (Input) formItem;
                    if ( InputTypeName.DATE_TIME.equals( input.getInputType() ) )
                    {
                        extraData.getData().addLocalDateTime( formItemName,
                                                              ValueTypes.LOCAL_DATE_TIME.convert( entry.getValue().toArray()[0] ) );
                    }
                    else if ( InputTypeName.LONG.equals( input.getInputType() ) )
                    {
                        final Long[] longValues = entry.getValue().stream().map( Long::parseLong ).toArray( Long[]::new );
                        extraData.getData().addLongs( formItemName, longValues );
                    }
                    else
                    {
                        extraData.getData().addStrings( formItemName, entry.getValue() );
                    }
                }

            }
        }
        fillComputedFormItems( metadataMap.values(), mediaInfo );

        return extradatasBuilder.build();
    }

    public String getConformityName( String tikaFieldValue )
    {
        if ( FIELD_CONFORMITY_MAP.containsValue( tikaFieldValue ) )
        {
            return null;
        }
        return FIELD_CONFORMITY_MAP.containsKey( tikaFieldValue ) ? FIELD_CONFORMITY_MAP.get( tikaFieldValue ) : tikaFieldValue;
    }

    public void fillComputedFormItems( Collection<ExtraData> extraDataList, MediaInfo mediaInfo )
    {
        for ( ExtraData extraData : extraDataList )
        {
            final PropertyTree xData = extraData.getData();
            if ( IMAGE_INFO.equals( extraData.getName().getLocalName() ) )
            {
                final Collection<String> tiffImageLengths = mediaInfo.getMetadata().get( "tiffImagelength" );
                final Collection<String> tiffImageWidths = mediaInfo.getMetadata().get( "tiffImagewidth" );
                if ( tiffImageLengths.size() > 0 && tiffImageWidths.size() > 0 )
                {
                    final long tiffImageLength = Long.valueOf( tiffImageLengths.toArray()[0].toString() );
                    final long tiffImageWidth = Long.valueOf( tiffImageWidths.toArray()[0].toString() );
                    xData.setLong( IMAGE_INFO_PIXEL_SIZE, tiffImageLength * tiffImageWidth );
                    xData.setLong( IMAGE_INFO_IMAGE_HEIGHT, tiffImageLength );
                    xData.setLong( IMAGE_INFO_IMAGE_WIDTH, tiffImageWidth );
                }
            }
            if ( GPS_INFO.equals( extraData.getName().getLocalName() ) )
            {
                if ( mediaInfo.getMetadata().get( "geoLat" ).size() > 0 && mediaInfo.getMetadata().get( "geoLong" ).size() > 0 )
                {
                    xData.addGeoPoint( "geoPoint",
                                       new GeoPoint( Double.valueOf( mediaInfo.getMetadata().get( "geoLat" ).toArray()[0].toString() ),
                                                     Double.valueOf( mediaInfo.getMetadata().get( "geoLong" ).toArray()[0].toString() ) ) );
                }
            }
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private MediaInfo mediaInfo;

        private ContentType contentType;

        private MixinService mixinService;

        private ContentService contentService;

        public Builder mediaInfo( final MediaInfo mediaInfo )
        {
            this.mediaInfo = mediaInfo;
            return this;
        }

        public Builder contentType( final ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder mixinService( final MixinService mixinService )
        {
            this.mixinService = mixinService;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.mixinService );
            Preconditions.checkNotNull( this.contentType );
        }

        public ImageContentProcessor build()
        {
            this.validate();
            return new ImageContentProcessor( this );
        }
    }

    /**
     * Keeps track of the number of bytes written to the output stream, but discards the data.
     */
    private static final class SizeCounterOutputStream
        extends OutputStream
    {
        private long size = 0;

        public long size()
        {
            return size;
        }

        @Override
        public void write( final byte[] b )
            throws IOException
        {
            if ( b != null )
            {
                size += b.length;
            }
        }

        @Override
        public void write( final byte[] b, final int off, final int len )
            throws IOException
        {
            size += len;
        }

        @Override
        public void write( final int b )
            throws IOException
        {
            size++;
        }
    }
}
