package com.enonic.wem.api.schema.mixin;

import org.junit.Test;

import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        Mixin ageMixin = newMixin().name( "age" ).addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = newFormItemSet().name( "person" ).addFormItem(
            newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newMixinReference( ageMixin ).name( "age" ).build() ).build();
        Mixin personMixin = newMixin().name( "person" ).addFormItem( personFormItemSet ).build();

        Mixin addressMixin = newMixin().name( "address" ).addFormItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personFormItemSet.add( newMixinReference( addressMixin ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A Mixin cannot reference other Mixins unless it is of type InputMixin: FormItemSetMixin", e.getMessage() );
        }
    }
}
