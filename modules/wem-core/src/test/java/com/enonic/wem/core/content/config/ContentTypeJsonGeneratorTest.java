package com.enonic.wem.core.content.config;

import org.junit.Test;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.SubType;
import com.enonic.wem.core.content.config.field.type.DropdownConfig;
import com.enonic.wem.core.content.config.field.type.FieldTypes;
import com.enonic.wem.core.content.config.field.type.RadioButtonsConfig;


public class ContentTypeJsonGeneratorTest
{
    @Test
    public void all_types()
    {
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "myOption 1", "o1" ).addOption( "myOption 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addField( Field.newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addField(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addField( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addField( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addField(
            Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addField( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addField( Field.newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeJsonGenerator generator = new ContentTypeJsonGenerator();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }

    @Test
    public void subtype()
    {
        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeJsonGenerator generator = new ContentTypeJsonGenerator();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }
}
