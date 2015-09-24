package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.filter.ValueFilter;

public interface QueryFieldNameResolver
{
    String resolve( final CompareExpr compareExpr );

    String resolve( final ValueFilter valueQueryFilter );

    String resolve( final String queryFieldName );

    String resolve( final String queryFieldName, final IndexValueType indexValueType );

    String resolve( final String queryFieldName, final Value value );

    String resolveOrderByFieldName( final String queryFieldName );

}