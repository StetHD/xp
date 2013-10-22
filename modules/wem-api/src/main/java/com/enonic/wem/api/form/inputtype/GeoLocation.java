package com.enonic.wem.api.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueOfUnexpectedClassException;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;
import com.enonic.wem.api.form.InvalidValueException;

final class GeoLocation
    extends InputType
{
    GeoLocation()
    {
    }

    @Override
    public void checkValidity( final Property property )
        throws ValueOfUnexpectedClassException, InvalidValueException
    {
        ValueTypes.GEOGRAPHIC_COORDINATE.checkValidity( property );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.GeographicCoordinate( value );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
