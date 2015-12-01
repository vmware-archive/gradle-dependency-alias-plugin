/*
 * Copyright © 2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * Some files may be comprised of various open source software components, each of which
 * has its own license that is located in the source code of the respective component.
 */

package com.vmware.oss.gradle.alias.utils

import org.junit.rules.TemporaryFolder

class SettingsFile {
    private File settingsFile

    SettingsFile(File settingsFile) {
        this.settingsFile = settingsFile
    }

    static SettingsFile create(TemporaryFolder dir) {
        return new SettingsFile(dir.newFile('settings.gradle'))
    }

    SettingsFile withPluginClasspath(String... pluginClasspath) {
        return withPluginClasspath(Arrays.asList(pluginClasspath))
    }

    SettingsFile withPluginClasspath(List<String> pluginClasspath) {
        settingsFile << """
                        buildscript {
                            dependencies {
                                classpath files($pluginClasspath)
                            }
                        }
                        """
        return this
    }

    SettingsFile apply(String plugin) {
        settingsFile << "apply plugin: '${plugin}'"
        return this
    }

    SettingsFile include(String project) {
        settingsFile << "include '${project}'"
        return this
    }


    SettingsFile withAliasExtension(String aliasFile) {
        return withAliasExtension(aliasFile, null)
    }

    SettingsFile withAliasExtension(String aliasFile, String aliasFileResolver) {
        if (aliasFile == null && aliasFileResolver == null) {
            return this
        }

        if (aliasFileResolver == null) {
            settingsFile << """
                                 alias {
                                     aliasFile = '${aliasFile}'
                                 }
                            """
        } else {
            settingsFile << """
                                 alias {
                                     aliasFile = '${aliasFile}'
                                     aliasFileResolver = ${aliasFileResolver}
                                 }
                            """
        }
        return this
    }

}
