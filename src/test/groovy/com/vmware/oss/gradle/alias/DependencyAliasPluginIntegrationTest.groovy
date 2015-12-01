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
        createHelloWorldIn('src/main/java/')
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
                .withArguments('--refresh-dependencies', 'compileJava', '--info')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome())
        Assert.assertTrue(result.getStandardOutput().contains('junit/junit/4.12/junit-4.12.jar'))
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
                .withArguments('--refresh-dependencies', 'compileJava', '--info')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome())
        Assert.assertTrue(result.getStandardOutput().contains('junit/junit/4.12/junit-4.12.jar'))
        Assert.assertTrue(result.getStandardOutput().contains('org/mockito/mockito-all/1.10.19/mockito-all-1.10.19.jar'))
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

        createHelloWorldIn('child/src/main/java/')
        createBuildIn('child/', 'dependencies { compile junit() }')

        BuildResult result = GradleRunner.create()
                .withProjectDir(testDir.getRoot())
                .withArguments('--refresh-dependencies', 'compileJava', '--info')
                .build();

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome())
        Assert.assertTrue(result.getStandardOutput().contains('junit/junit/4.12/junit-4.12.jar'))
    }


    private File createDirectoryTree(String path) {
        File dir = new File(testDir.getRoot(), path)
        dir.mkdirs()
        return dir
    }

    private File createHelloWorldIn(String path) {
        createDirectoryTree(path)
        File helloWorld = testDir.newFile("${path}/HelloWorld.java")
        helloWorld << '''  |public class HelloWorld {
                           |     public static void main(String[] args) {
                           |         // Prints "Hello, World" to the terminal window.
                           |         System.out.println("Hello, World");
                           |     }
                           |}'''.stripMargin()
        return helloWorld
    }

    private File createBuildIn(String content) {
        return createBuildIn('', content)
    }

    private File createBuildIn(String path, String content) {
        File buildFile = testDir.newFile("${path}/build.gradle");
        buildFile << content
        return buildFile
    }
}