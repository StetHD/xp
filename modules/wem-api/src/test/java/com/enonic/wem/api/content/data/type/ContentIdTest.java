package com.enonic.wem.api.content.data.type;

import org.junit.Test;


public class ContentIdTest
{
    @Test
    public void newValue()
        throws Exception
    {
        Object value = ValueTypes.CONTENT_ID.convert( "123" );
        System.out.println( value.getClass() );
    }
}
