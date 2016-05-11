package com.enonic.xp.core.impl.media;

import java.io.IOException;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.util.Exceptions;

@Component
public final class MediaInfoServiceImpl
    implements MediaInfoService
{
    private BinaryExtractor binaryExtractor;

    @Override
    public MediaInfo parseMediaInfo( final ByteSource byteSource )
    {
        final MediaInfo.Builder builder = MediaInfo.create();

        final ExtractedData extractedData = binaryExtractor.extract( byteSource );

        addMetadata( byteSource, builder, extractedData );
        builder.setTextContent( extractedData.getText() );

        return builder.build();
    }

    private void addMetadata( final ByteSource byteSource, final MediaInfo.Builder builder, final ExtractedData extractedData )
    {
        builder.mediaType( extractedData.get( HttpHeaders.CONTENT_TYPE ) );

        // Append metadata to info object
        final Set<String> names = extractedData.names();
        for ( final String name : names )
        {
            final String value = extractedData.get( name );
            builder.addMetadata( name, value );
        }
        try
        {
            builder.addMetadata( MediaInfo.MEDIA_INFO_BYTE_SIZE, String.valueOf( byteSource.size() ) );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public ImageOrientation getImageOrientation( ByteSource byteSource )
    {
        final ExtractedData extractedData = binaryExtractor.extract( byteSource );
        final String orientation = extractedData.getImageOrientation();
        return ImageOrientation.from( orientation );
    }

    @Reference
    public void setBinaryExtractor( final BinaryExtractor binaryExtractor )
    {
        this.binaryExtractor = binaryExtractor;
    }
}
