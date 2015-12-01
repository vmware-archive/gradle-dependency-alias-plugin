/*
 * Copyright © 2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * Some files may be comprised of various open source software components, each of which
 * has its own license that is located in the source code of the respective component.
 */

package com.vmware.oss.gradle.alias

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.util.ConfigureUtil

class DependencyAliasPlugin implements Plugin<Settings> {
    static final def String DEFAULT_ALIAS_FILE = 'alias.properties'

    def Map<String, String> dependencyAliasesDef = [:]
    def DependencyAliasExtension ext

    @Override
    void apply(Settings settings) {
        ext = new DependencyAliasExtension()
        createExtensionOnSettings(settings, 'alias', ext)
        settings.getGradle().settingsEvaluated { Settings s -> loadAliases() }
        settings.getGradle().projectsLoaded { Gradle gradle -> applyAliases(gradle.getRootProject()) }
    }

    void applyAliases(Project root) {
        root.allprojects { Project project ->
            DependencyHandler handler = project.dependencies
            dependencyAliasesDef.each { alias, value ->
                handler.metaClass."${alias}" = { Object[] args -> aliasInvocationHandler(value, args) }
            }
        }
    }

    String aliasInvocationHandler(String value, Object[] args) {
        //TODO: collections
        return value
    }

    void loadAliases() {
        FileResolver resolver = new DefaultFileResolver()
        String file = DEFAULT_ALIAS_FILE
        if (ext.aliasFileResolver != null && ext.aliasFileResolver instanceof FileResolver) {
            resolver = ext.aliasFileResolver
        }

        if (ext.aliasFile != null) {
            file = ext.aliasFile
        }

        dependencyAliasesDef.putAll(resolver.resolve(file))
    }

    void createExtensionOnSettings(Settings settings, String name, Object ext) {
        settings.metaClass."${name}" = { Closure c -> ConfigureUtil.configure(c, ext) }
    }

}