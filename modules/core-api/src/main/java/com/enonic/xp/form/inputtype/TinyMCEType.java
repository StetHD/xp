package com.enonic.xp.form.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class TinyMCEType
    extends InputType
{
    public TinyMCEType()
    {
        super( InputTypeName.TINY_MCE );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.STRING );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }
}