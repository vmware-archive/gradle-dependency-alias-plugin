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

class BuildFile {
    private File buildFile

    BuildFile(File settingsFile) {
        this.buildFile = settingsFile
    }

    static BuildFile create(TemporaryFolder dir) {
        return new BuildFile(dir.newFile('build.gradle'))
    }

    BuildFile withDependencies(String... dependencies) {
        return withDependencies(Arrays.asList(dependencies))
    }


    BuildFile withDependencies(List<String> dependencies) {
        buildFile << """
                         dependencies {
                            ${dependencies.join('\n')}
                         }
                     """
        return this
    }

    BuildFile withRepositories(String... repos) {
        return withRepositories(Arrays.asList(repos))
    }


    BuildFile withRepositories(List<String> repos) {
        buildFile << """
                         repositories {
                            ${repos.join('\n')}
                         }
                     """
        return this
    }

    BuildFile apply(String plugin) {
        buildFile << "apply plugin: '${plugin}'"
        return this
    }

}
