package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

public final class SyncUserStoreJson
{
    private List<String> keys;

    public List<String> getKeys()
    {
        return keys;
    }

    public void setKeys( final List<String> keys )
    {
        this.keys = keys;
    }
}
