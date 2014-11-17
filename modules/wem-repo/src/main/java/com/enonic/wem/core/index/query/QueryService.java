package com.enonic.wem.core.index.query;

import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.core.index.IndexContext;

public interface QueryService
{
    public static final int GET_ALL_SIZE_FLAG = -1;

    public NodeQueryResult find( final NodeQuery query, final IndexContext context );

    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext );

    public NodeVersionId get( final NodePath nodePath, final IndexContext indexContext );

    public NodeVersionIds find( final NodePaths nodePath, final OrderExpressions orderExprs, final IndexContext indexContext );

    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext );

    public boolean hasChildren( final NodePath nodeId, final IndexContext indexContext );

}
