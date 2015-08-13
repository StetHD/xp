package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.Reference;
import com.enonic.xp.xml.DomHelper;

final class ImageSelectorType
    extends InputType
{
    public ImageSelectorType()
    {
        super( InputTypeName.IMAGE_SELECTOR );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.REFERENCE );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }

    @Override
    public InputTypeConfig parseConfig( final ApplicationKey app, final Element elem )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( app );
        final InputTypeConfig.Builder builder = InputTypeConfig.create();

        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );
        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( StringUtils.isNotBlank( text ) )
        {
            builder.property( "relationshipType", resolver.toRelationshipTypeName( text ).toString() );
        }

        return builder.build();
    }

    @Override
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();
        jsonConfig.put( "relationshipType", config.getValue( "relationshipType" ) );
        return jsonConfig;
    }
}