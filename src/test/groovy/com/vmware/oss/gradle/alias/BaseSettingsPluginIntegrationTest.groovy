/*
 * Copyright © 2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * Some files may be comprised of various open source software components, each of which
 * has its own license that is located in the source code of the respective component.
 */

package com.vmware.oss.gradle.alias

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class BaseSettingsPluginIntegrationTest {

    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder()

    protected List<String> getPluginClasspath() {
        URL pluginClasspathResource = getClass().classLoader.findResource('plugin-classpath.txt')
        if (pluginClasspathResource == null) {
            throw new IllegalStateException('Did not find plugin classpath resource, run `testClasses` build task.')
        }

        List<String> pluginClasspath = getClass().classLoader.findResource('plugin-classpath.txt')
                .readLines()
                .collect { it.replace('\\\\', '\\\\\\\\') }
                .collect { "\'${it}\'" }


        return pluginClasspath
    }

}
