package com.enonic.wem.core.content;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.GetContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.MockContentTypeFetcher;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.component.inputtype.InputTypes.TEXT_LINE;

@Component
public final class GetContentTypesHandler
    extends CommandHandler<GetContentTypes>
{
    private ContentTypeFetcher contentTypeFetcher;

    public GetContentTypesHandler()
    {
        super( GetContentTypes.class );
        this.contentTypeFetcher = createMockContentTypeFetcher();
    }

    @Override
    public void handle( final CommandContext context, final GetContentTypes command )
        throws Exception
    {
        final List<QualifiedContentTypeName> contentTypeNames = command.getNames();
        final List<ContentType> contentTypeList = Lists.newArrayList();
        for ( QualifiedContentTypeName name : contentTypeNames )
        {
            final ContentType contentType = contentTypeFetcher.getContentType( name );
            if ( contentType != null )
            {
                contentTypeList.add( contentType );
            }
        }
        final ContentTypes contentTypes = ContentTypes.from( contentTypeList );
        command.setResult( contentTypes );
    }

    private ContentTypeFetcher createMockContentTypeFetcher()
    {
        MockContentTypeFetcher mockContentTypeFetcher = new MockContentTypeFetcher();
        final ContentType articleContentType = new ContentType();
        articleContentType.setModule( Module.newModule().name( "News" ).build() );
        articleContentType.setName( "Article" );
        final Input title =
            newInput().name( "title" ).type( TEXT_LINE ).label( "Title" ).required( true ).helpText( "Article title" ).build();
        final Input category =
            newInput().name( "preface" ).type( TEXT_LINE ).label( "Preface" ).helpText( "Preface of the article" ).build();
        final Input body =
            newInput().name( "body" ).type( TEXT_AREA ).label( "Body" ).required( true ).helpText( "Body of the article" ).build();
        articleContentType.addComponent( title );
        articleContentType.addComponent( category );
        articleContentType.addComponent( body );
        mockContentTypeFetcher.add( articleContentType );

        final ContentType article2ContentType = new ContentType();
        article2ContentType.setModule( Module.newModule().name( "News" ).build() );
        article2ContentType.setName( "Article2" );
        article2ContentType.addComponent( title.copy() );
        article2ContentType.addComponent( category.copy() );
        article2ContentType.addComponent( body.copy() );
        ComponentSet componentSet = newComponentSet().name( "related" ).build();
        componentSet.add( newInput().name( "author" ).label( "Author" ).type( TEXT_LINE ).build() );
        componentSet.add( newInput().name( "category" ).label( "Category" ).type( TEXT_LINE ).build() );
        article2ContentType.addComponent( componentSet );
        mockContentTypeFetcher.add( article2ContentType );

        return mockContentTypeFetcher;
    }
}
