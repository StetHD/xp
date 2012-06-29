package com.enonic.wem.core.content;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public abstract class FieldEntryJsonGenerator
{
    public abstract void generate( FieldEntry fieldEntry, JsonGenerator g )
        throws IOException;
}
