/*
 * Copyright © 2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * Some files may be comprised of various open source software components, each of which
 * has its own license that is located in the source code of the respective component.
 */

package com.vmware.oss.gradle.alias

import com.vmware.oss.gradle.alias.utils.AliasesFile
import com.vmware.oss.gradle.alias.utils.BuildFile
import com.vmware.oss.gradle.alias.utils.SettingsFile
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DependencyAliasPluginIntegrationTest extends BaseSettingsPluginIntegrationTest {


    @Before
    public void setup() throws IOException {
        createDirectoryTree('src/main/java/')
    }

    @Test
    void testDependencyAlias() {
        AliasesFile aliases = AliasesFile.create(testDir).add('junit', 'junit:junit:4.12')
        SettingsFile.create(testDir).withPluginClasspath(getPluginClasspath())
                                    .apply('com.vmware.dependency-alias')
                                    .withAliasExtension(aliases.absolutePath)
        BuildFile.create(testDir).apply('java')
                                 .withRepositories('jcenter()')
                                 .withDependencies('compile junit()')


        BuildResult result = GradleRunner.create()
                .withProjectDir(testDir.getRoot())
                .withArguments('dependencies', '--configuration', 'testCompile')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies").getOutcome())
        Assert.assertTrue(getOutput(result).contains('junit:junit:4.12'))
    }

    @Test
    void testDependencyAliasWithMultipleDeps() {
        AliasesFile aliases = AliasesFile.create(testDir).add('log4j', 'org.apache.logging.log4j:log4j-api:2.4.1,org.apache.logging.log4j:log4j-core:2.4.1')
        SettingsFile.create(testDir).withPluginClasspath(getPluginClasspath())
                .apply('com.vmware.dependency-alias')
                .withAliasExtension(aliases.absolutePath)
        BuildFile.create(testDir).apply('java')
                .withRepositories('jcenter()')
                .withDependencies('compile log4j()')


        BuildResult result = GradleRunner.create()
                .withProjectDir(testDir.getRoot())
                .withArguments('dependencies', '--configuration', 'testCompile')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies").getOutcome())
        Assert.assertTrue(getOutput(result).contains('org.apache.logging.log4j:log4j-api:2.4.1'))
        Assert.assertTrue(getOutput(result).contains('org.apache.logging.log4j:log4j-core:2.4.1'))
    }

    @Test
    void testDependencyAliasMixedWithStandardDependency() {
        AliasesFile aliases = AliasesFile.create(testDir).add('junit', 'junit:junit:4.12')
        SettingsFile.create(testDir).withPluginClasspath(getPluginClasspath())
                                    .apply('com.vmware.dependency-alias')
                                    .withAliasExtension(aliases.absolutePath)
        BuildFile.create(testDir).apply('java')
                                 .withRepositories('jcenter()')
                                 .withDependencies('compile junit()', 'compile \'org.mockito:mockito-all:1.10.19\'')

        BuildResult result = GradleRunner.create()
                .withProjectDir(testDir.getRoot())
                .withArguments('dependencies', '--configuration', 'testCompile')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies").getOutcome())
        Assert.assertTrue(getOutput(result).contains('junit:junit:4.12'))
        Assert.assertTrue(getOutput(result).contains('org.mockito:mockito-all:1.10.19'))
    }

    @Test
    void testDependencyAliasOnMultiproject() {
        AliasesFile aliases = AliasesFile.create(testDir).add('junit', 'junit:junit:4.12')
        SettingsFile.create(testDir).withPluginClasspath(getPluginClasspath()).apply('com.vmware.dependency-alias').withAliasExtension(aliases.absolutePath).include('child')
        createBuildIn('''
                         | allprojects {
                         |  apply plugin: 'java'
                         |  repositories {
                         |      jcenter()
                         |  }
                         | }
                      '''.stripMargin())

        createDirectoryTree('child/src/main/java/')
        createBuildIn('child/', 'dependencies { compile junit() }')

        BuildResult result = GradleRunner.create()
                .withProjectDir(testDir.getRoot())
                .withArguments(':child:dependencies', '--configuration', 'testCompile')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":child:dependencies").getOutcome())
        Assert.assertTrue(getOutput(result).contains('junit:junit:4.12'))
    }


    private File createDirectoryTree(String path) {
        File dir = new File(testDir.getRoot(), path)
        dir.mkdirs()
        return dir
    }

    private File createBuildIn(String content) {
        return createBuildIn('', content)
    }

    private File createBuildIn(String path, String content) {
        File buildFile = testDir.newFile("${path}/build.gradle");
        buildFile << content
        return buildFile
    }

    /***
     * Gradle does not have BC on incubating feature such as testkit
     * version 2.9 changed getStandardOutput (2.6 - 2.8) to getOutput (2.9 and onwards)
     * this method will fetch the right value via metaclass inspection
     * @param result GradleRunner's BuildResult
     * @return GradleRunner's execution output
     */
    private String getOutput(BuildResult result) {
        if (result.respondsTo('getOutput')){
            //2.9
            return result.getOutput();
        }
        //pre 2.9
        return result.getStandardOutput();
    }
}