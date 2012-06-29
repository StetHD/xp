package com.enonic.wem.core.content.config.field;


import com.google.common.base.Preconditions;

public class SubType
    extends ConfigItem
{
    private String label;

    private ConfigItems configItems = new ConfigItems();

    private boolean required;

    private boolean immutable;

    private Multiple multiple;

    private String customText;

    private String helpText;


    @Override
    void setPath( final FieldPath fieldPath )
    {
        super.setPath( fieldPath );
        configItems.setPath( fieldPath );
    }

    public void addField( final Field field )
    {
        Preconditions.checkState( getPath() != null, "Cannot add field before this SubType is added" );

        field.setPath( new FieldPath( getPath(), field.getName() ) );
        this.configItems.addField( field );
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    boolean isMultiple()
    {
        return multiple != null;
    }


    public Multiple getMultiple()
    {
        return multiple;
    }

    public String getCustomText()
    {
        return customText;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public ConfigItems getConfigItems()
    {
        return configItems;
    }

    @Override
    public String toString()
    {
        FieldPath fieldPath = getPath();
        if ( fieldPath != null )
        {
            return fieldPath.toString();
        }
        else
        {
            return getName() + "?";
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public ConfigItem getConfig( final FieldPath fieldPath )
    {
        return configItems.getConfig( fieldPath );
    }

    @Override
    ConfigItemJsonGenerator getJsonGenerator()
    {
        return SubTypeJsonGenerator.DEFAULT;
    }

    public static class Builder
    {
        private SubType subType;

        private Builder()
        {
            subType = new SubType();
        }

        public Builder name( String value )
        {
            subType.setName( value );
            return this;
        }

        public Builder label( String value )
        {
            Preconditions.checkNotNull( value, "label cannot be null" );

            subType.label = value;
            return this;
        }

        public Builder required( boolean value )
        {
            subType.required = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            subType.immutable = value;
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                subType.multiple = new Multiple( 0, 0 );
            }
            else
            {
                subType.multiple = null;
            }
            return this;
        }

        public Builder multiple( int minEntries, int maxEntries )
        {
            Preconditions.checkArgument( minEntries >= 0 );
            Preconditions.checkArgument( maxEntries >= 0 );

            subType.multiple = new Multiple( minEntries, maxEntries );
            return this;
        }

        public Builder customText( String value )
        {
            subType.customText = value;
            return this;
        }

        public Builder helpText( String value )
        {
            subType.helpText = value;
            return this;
        }

        public SubType build()
        {
            Preconditions.checkNotNull( subType.getName(), "name cannot be null" );

            return subType;
        }
    }
}
