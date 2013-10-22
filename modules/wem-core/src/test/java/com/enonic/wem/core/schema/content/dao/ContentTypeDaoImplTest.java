package com.enonic.wem.core.schema.content.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class ContentTypeDaoImplTest
    extends AbstractJcrTest
{
    private static final byte[] IMAGE_DATA = "imagedata".getBytes();

    private static final Icon DUMMY_ICON = Icon.from( IMAGE_DATA, "image/gif" );

    private ContentTypeDao contentTypeDao;

    public void setupDao()
        throws Exception
    {
        contentTypeDao = new ContentTypeDaoImpl();
    }

    @Test
    public void createContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            name( "mycontenttype" ).
            setAbstract( false ).
            displayName( "My content type" ).
            icon( DUMMY_ICON );

        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );

        // exercise
        contentTypeDao.create( contentType, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentTypeDao.CONTENT_TYPES_PATH + "mycontenttype" );
        assertNotNull( contentNode );
        assertArrayEquals( IMAGE_DATA, JcrHelper.getPropertyBinary( contentNode, IconJcrMapper.ICON_PROPERTY ) );
    }

    @Test
    public void retrieveContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            name( "mycontenttype" ).
            setAbstract( true ).
            displayName( "My content type" ).
            icon( DUMMY_ICON );
        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.create( contentType, session );

        // exercise
        final ContentTypes contentTypes = contentTypeDao.select( QualifiedContentTypeNames.from( "mycontenttype" ), session );
        commit();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );
        final ContentType contentType1 = contentTypes.first();
        assertEquals( "mycontenttype", contentType1.getName() );
        assertEquals( true, contentType1.isAbstract() );
        assertEquals( "My content type", contentType1.getDisplayName() );
        assertEquals( DUMMY_ICON, contentType1.getIcon() );
    }

    @Test
    public void retrieveAllContentTypes()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            name( "mycontenttype" ).
            setAbstract( true ).
            displayName( "My content type" );
        final ContentType contentTypeCreated1 = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.create( contentTypeCreated1, session );

        final ContentType.Builder contentTypeBuilder2 = newContentType().
            name( "somecontenttype" ).
            setAbstract( false ).
            displayName( "Another content type" );
        final ContentType contentTypeCreated2 = addContentTypeFormItems( contentTypeBuilder2 );

        contentTypeDao.create( contentTypeCreated2, session );

        // exercise
        final ContentTypes contentTypes = contentTypeDao.selectAll( session );
        commit();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 2, contentTypes.getSize() );
        final ContentType contentType1 = contentTypes.getContentType( QualifiedContentTypeName.from( "mycontenttype" ) );
        final ContentType contentType2 = contentTypes.getContentType( QualifiedContentTypeName.from( "somecontenttype" ) );

        assertEquals( "mycontenttype", contentType1.getName() );
        assertEquals( true, contentType1.isAbstract() );
        assertEquals( "My content type", contentType1.getDisplayName() );

        assertEquals( "somecontenttype", contentType2.getName() );
        assertEquals( false, contentType2.isAbstract() );
        assertEquals( "Another content type", contentType2.getDisplayName() );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            name( "my_content_type" ).
            setAbstract( true ).
            displayName( "My content type" );
        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.create( contentType, session );

        // exercise
        final ContentTypes contentTypesAfterCreate = contentTypeDao.select( QualifiedContentTypeNames.from( "my_content_type" ), session );
        assertNotNull( contentTypesAfterCreate );
        assertEquals( 1, contentTypesAfterCreate.getSize() );

        final ContentType contentTypeUpdate = newContentType( contentType ).
            setAbstract( false ).
            displayName( "My content type-UPDATED" ).
            icon( DUMMY_ICON ).
            build();
        contentTypeDao.update( contentTypeUpdate, session );
        commit();

        // verify
        final ContentTypes contentTypesAfterUpdate = contentTypeDao.select( QualifiedContentTypeNames.from( "my_content_type" ), session );
        assertNotNull( contentTypesAfterUpdate );
        assertEquals( 1, contentTypesAfterUpdate.getSize() );
        final ContentType contentType1 = contentTypesAfterUpdate.first();
        assertEquals( "my_content_type", contentType1.getName() );
        assertEquals( false, contentType1.isAbstract() );
        assertEquals( "My content type-UPDATED", contentType1.getDisplayName() );
        assertEquals( DUMMY_ICON, contentType1.getIcon() );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            name( "my_content_type" ).
            setAbstract( true ).
            displayName( "My content type" );
        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.create( contentType, session );

        // exercise
        final ContentTypes contentTypesAfterCreate = contentTypeDao.select( QualifiedContentTypeNames.from( "my_content_type" ), session );
        assertNotNull( contentTypesAfterCreate );
        assertEquals( 1, contentTypesAfterCreate.getSize() );

        contentTypeDao.delete( contentType.getQualifiedName(), session );
        commit();

        // verify
        final ContentTypes contentTypesAfterDelete = contentTypeDao.select( QualifiedContentTypeNames.from( "my_content_type" ), session );
        assertNotNull( contentTypesAfterDelete );
        assertTrue( contentTypesAfterDelete.isEmpty() );
    }

    private ContentType addContentTypeFormItems( final ContentType.Builder contentTypeBuilder )
    {
        final FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( formItemSet );
        return contentTypeBuilder.build();
    }
}
