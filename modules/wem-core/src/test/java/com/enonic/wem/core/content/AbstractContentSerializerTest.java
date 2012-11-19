package com.enonic.wem.core.content;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.blob.BlobKeyCreator;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.MockBlobKeyResolver;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.MockContentTypeFetcher;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public abstract class AbstractContentSerializerTest
{
    private Module myModule = Module.newModule().name( "myModule" ).build();

    protected MockContentTypeFetcher contentTypeFetcher = new MockContentTypeFetcher();

    private ContentSerializer serializer;

    abstract ContentSerializer getSerializer();

    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    @Test
    public void given_content_with_name_when_parsed_then_name_is_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( newInput().name( "myFormItem" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setName( "myContent" );
        content.setData( "myFormItem", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "myContent", parsedContent.getName() );
    }

    @Test
    public void given_content_with_name_and_a_formItem_when_parsed_then_path_and_value_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setData( "myInput", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "A value", parsedContent.getData( "myInput" ).getValue() );
    }

    @Test
    public void array()
    {
        Content content = new Content();
        content.setType( new QualifiedContentTypeName( "myModule:myType" ) );
        content.setData( "myArray[0]", "1" );
        content.setData( "myArray[1]", "2" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        assertEquals( "1", parsedContent.getData( "myArray[0]" ).getValue() );
        assertEquals( "2", parsedContent.getData( "myArray[1]" ).getValue() );
    }

    @Test
    public void array_within_set()
    {
        Content content = new Content();
        content.setType( new QualifiedContentTypeName( "myModule:myType" ) );
        content.setData( "mySet.myArray[0]", "1" );
        content.setData( "mySet.myArray[1]", "2" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        assertEquals( "1", parsedContent.getData( "mySet.myArray[0]" ).getValue() );
        assertEquals( "2", parsedContent.getData( "mySet.myArray[1]" ).getValue() );
    }

    @Test
    public void set_within_array()
    {
        Content content = new Content();
        content.setType( new QualifiedContentTypeName( "myModule:myType" ) );
        content.setData( "mySet[0].myInput", "1" );
        content.setData( "mySet[0].myOtherInput", "a" );
        content.setData( "mySet[1].myInput", "2" );
        content.setData( "mySet[1].myOtherInput", "b" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        assertEquals( "1", parsedContent.getData( "mySet[0].myInput" ).getValue() );
        assertEquals( "2", parsedContent.getData( "mySet[1].myInput" ).getValue() );
    }

    @Test
    public void array_within_array()
    {
        Content content = new Content();
        content.setType( new QualifiedContentTypeName( "myModule:myType" ) );
        content.setData( "mySet[0].myArray[0]", "1" );
        content.setData( "mySet[0].myArray[1]", "2" );
        content.setData( "mySet[1].myArray[0]", "3" );
        content.setData( "mySet[1].myArray[1]", "4" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        assertEquals( "1", parsedContent.getData( "mySet[0].myArray[0]" ).getValue() );
        assertEquals( "2", parsedContent.getData( "mySet[0].myArray[1]" ).getValue() );
        assertEquals( "3", parsedContent.getData( "mySet[1].myArray[0]" ).getValue() );
        assertEquals( "4", parsedContent.getData( "mySet[1].myArray[1]" ).getValue() );
    }

    @Test
    public void given_formItem_and_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.add( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myText", "A value" );
        content.setData( "formItemSet.myText", "A another value" );

        String serialized = toString( content );

        // exercise
        Content actualContent = toContent( serialized );

        // verify
        assertEquals( "myText", actualContent.getData( "myText" ).getPath().toString() );
        assertEquals( "formItemSet.myText", actualContent.getData( "formItemSet.myText" ).getPath().toString() );
        assertEquals( "A value", actualContent.getData( "myText" ).getValue() );
        assertEquals( "A another value", actualContent.getData( "formItemSet.myText" ).getValue() );
    }

    @Test
    public void given_array_of_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentTypeFetcher.add( contentType );

        FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).label( "FormItemSet" ).multiple( true ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.add( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "formItemSet[0].myText", "Value 1" );
        content.setData( "formItemSet[1].myText", "Value 2" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "formItemSet[0].myText", parsedContent.getData( "formItemSet[0].myText" ).getPath().toString() );
        assertEquals( "formItemSet[1].myText", parsedContent.getData( "formItemSet[1].myText" ).getPath().toString() );
        assertEquals( "Value 1", parsedContent.getData( "formItemSet[0].myText" ).getValue() );
        assertEquals( "Value 2", parsedContent.getData( "formItemSet[1].myText" ).getValue() );
    }

    @Test
    public void given_formItem_inside_layout_when_parse_then_formItem_path_is_affected_by_name_of_layout()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );
        FieldSet layout = newFieldSet().label( "Label" ).name( "fieldSet" ).add(
            newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( layout );

        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myText", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "A value", parsedContent.getValueAsString( "myText" ) );
        assertEquals( "myText", parsedContent.getData( "myText" ).getPath().toString() );
    }


    @Test
    public void unstructured_with_subTypes()
    {
        Content data = new Content();
        data.setData( "name", "Thomas" );
        data.setData( "child[0].name", "Joachim" );
        data.setData( "child[0].age", "9" );
        data.setData( "child[0].features.eyeColour", "Blue" );
        data.setData( "child[0].features.hairColour", "Blonde" );
        data.setData( "child[1].name", "Madeleine" );
        data.setData( "child[1].age", "7" );
        data.setData( "child[1].features.eyeColour", "Brown" );
        data.setData( "child[1].features.hairColour", "Black" );

        String serialized = toString( data );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "name" ).getValue() );
        assertEquals( "Joachim", parsedContent.getData( "child[0].name" ).getValue() );
        assertEquals( "9", parsedContent.getData( "child[0].age" ).getValue() );
        assertEquals( "Blue", parsedContent.getData( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", parsedContent.getData( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", parsedContent.getData( "child[1].name" ).getValue() );
        assertEquals( "7", parsedContent.getData( "child[1].age" ).getValue() );
        assertEquals( "Brown", parsedContent.getData( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", parsedContent.getData( "child[1].features.hairColour" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays()
    {
        Content content = new Content();
        content.setData( "names[0]", "Thomas" );
        content.setData( "names[1]", "Sten Roger" );
        content.setData( "names[2]", "Alex" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "names[0]" ).getValue() );
        assertEquals( DataTypes.TEXT, parsedContent.getData( "names[0]" ).getDataType() );
        assertEquals( "Sten Roger", parsedContent.getData( "names[1]" ).getValue() );
        assertEquals( "Alex", parsedContent.getData( "names[2]" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays_within_subType()
    {
        Content content = new Content();
        content.setData( "company.names[0]", "Thomas" );
        content.setData( "company.names[1]", "Sten Roger" );
        content.setData( "company.names[2]", "Alex" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "company.names[0]" ).getValue() );
        assertEquals( "Sten Roger", parsedContent.getData( "company.names[1]" ).getValue() );
        assertEquals( "Alex", parsedContent.getData( "company.names[2]" ).getValue() );
    }

    @Test
    public void xxx()
    {
        byte[] bytes = new byte[]{1, 2, 3};
        Content content = new Content();
        content.setName( "My content" );
        content.setData( "name", "Arn", DataTypes.TEXT );
        content.setData( "image.bytes", bytes, DataTypes.BLOB );
        content.setData( "image.caption", "Caption", DataTypes.TEXT );

        MockBlobKeyResolver blobToKeyReplacer = new MockBlobKeyResolver();
        content.replaceBlobsWithKeys( blobToKeyReplacer );

        String serialized = toString( content );
        Content parsedContent = toContent( serialized );

        assertEquals( "Arn", parsedContent.getData( "name" ).getValue() );
        assertEquals( "Caption", parsedContent.getData( "image.caption" ).getValue() );
        assertEquals( BlobKeyCreator.createKey( bytes ), parsedContent.getData( "image.bytes" ).getValue() );
    }

    @Test
    public void content_serialize_parse_serialize_roundTrip()
    {
        final DateTime time = DateTime.now();
        final Content content = new Content();
        content.setType( new QualifiedContentTypeName( "myModule:myType" ) );
        content.setCreatedTime( time );
        content.setModifiedTime( time );
        content.setOwner( AccountKey.superUser() );
        content.setModifier( AccountKey.superUser() );
        content.setDisplayName( "My content" );
        content.setPath( ContentPath.from( "site1/mycontent" ) );
        content.setData( "mySet[0].myArray[0]", "1" );
        content.setData( "mySet[0].myArray[1]", "2" );
        content.setData( "mySet[1].myArray[0]", "3" );
        content.setData( "mySet[1].myArray[1]", "4" );

        final String serialized = toString( content );

        // exercise
        final Content parsedContent = toContent( serialized );
        final String serializedAfterParsing = toString( parsedContent );

        // verify
        assertEquals( serialized, serializedAfterParsing );
    }

    private Content toContent( final String serialized )
    {
        return serializer.toContent( serialized );
    }

    private String toString( final Content content )
    {
        String serialized = getSerializer().toString( content );
        System.out.println( serialized );
        return serialized;
    }
}
