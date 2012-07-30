package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class ConfigItemsTest
{
    @Test
    public void getConfig()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet.Builder subTypeBuilder = FieldSet.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        FieldSet fieldSet = subTypeBuilder.build();
        configItems.addConfig( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        // exercise & verify
        ConfigItem personaliaConfig = configItems.getConfig( new FieldPath( "personalia" ).getLastElement() );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfig( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        // exercise & verify
        ConfigItem personaliaEyeColourConfig = fieldSet.getConfigItems().getConfig( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }
}
