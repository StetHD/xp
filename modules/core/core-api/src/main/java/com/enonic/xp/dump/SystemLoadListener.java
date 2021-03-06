package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface SystemLoadListener
{
    void loadingBranch( final RepositoryId repositoryId, final Branch branch );

    void nodeLoaded();

    void setTotal( final Long total );
}
