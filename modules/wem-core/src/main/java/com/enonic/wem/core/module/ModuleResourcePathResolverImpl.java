package com.enonic.wem.core.module;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.config.SystemConfig;

@Singleton
final class ModuleResourcePathResolverImpl
    implements ModuleResourcePathResolver
{
    private final SystemConfig systemConfig;

    @Inject
    public ModuleResourcePathResolverImpl( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Override
    public Path resolveModulePath( final ModuleKey moduleKey )
    {
        final Path basePath = systemConfig.getModulesDir();
        return basePath.resolve( moduleKey.toString() );
    }

    @Override
    public Path resolveResourcePath( final ModuleResourceKey key )
    {
        final Path moduleDir = resolveModulePath( key.getModule() );
        return moduleDir.resolve( key.getPath().substring( 1 ) );
    }
}
