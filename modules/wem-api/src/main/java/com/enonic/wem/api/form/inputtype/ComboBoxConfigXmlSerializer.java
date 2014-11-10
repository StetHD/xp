package com.enonic.wem.api.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.wem.api.xml.DomBuilder;
import com.enonic.wem.api.xml.DomHelper;

import static com.enonic.wem.api.form.inputtype.ComboBoxConfig.newComboBoxConfig;

final class ComboBoxConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<ComboBoxConfig>
{
    public static final ComboBoxConfigXmlSerializer DEFAULT = new ComboBoxConfigXmlSerializer();

    @Override
    protected void serializeConfig( final ComboBoxConfig config, final DomBuilder builder )
    {
        builder.start( "options" );

        for ( final Option option : config.getOptions() )
        {
            builder.start( "option" );
            builder.start( "label" ).text( option.getLabel() ).end();
            builder.start( "value" ).text( option.getValue() ).end();
            builder.end();
        }

        builder.end();
    }

    @Override
    public ComboBoxConfig parseConfig( final Element elem )
    {
        final ComboBoxConfig.Builder builder = newComboBoxConfig();
        final Element optionsEl = DomHelper.getChildElementByTagName( elem, "options" );

        for ( final Element optionEl : DomHelper.getChildElementsByTagName( optionsEl, "option" ) )
        {
            final String label = DomHelper.getChildElementValueByTagName( optionEl, "label" );
            final String value = DomHelper.getChildElementValueByTagName( optionEl, "value" );
            builder.addOption( label, value );
        }

        return builder.build();
    }
}
