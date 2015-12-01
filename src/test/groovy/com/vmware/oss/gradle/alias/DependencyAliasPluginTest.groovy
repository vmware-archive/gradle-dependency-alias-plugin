/*
 * Copyright © 2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * Some files may be comprised of various open source software components, each of which
 * has its own license that is located in the source code of the respective component.
 */

package com.vmware.oss.gradle.alias

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class DependencyAliasPluginTest {

    @Test
    void testApply() {

    }

    @Test
    void testApplyAliases() {
        DependencyAliasPlugin tested = new DependencyAliasPlugin()
        tested.dependencyAliasesDef.put('testAlias', 'testValue')
        Project root = new ProjectBuilder().withName('root').build()
        tested.applyAliases(root)
        Assert.assertFalse(root.dependencies.respondsTo('testAlias').isEmpty())
        Assert.assertEquals('testValue', root.dependencies.testAlias())
    }

    @Test
    void testApplyAliasesMultiProject() {
        DependencyAliasPlugin tested = new DependencyAliasPlugin()
        tested.dependencyAliasesDef.put('testAlias', 'testValue')
        Project root = new ProjectBuilder().withName('root').build()
        Project child = new ProjectBuilder().withParent(root).withName('child').build()
        tested.applyAliases(root)
        Assert.assertFalse(root.dependencies.respondsTo('testAlias').isEmpty())
        Assert.assertEquals('testValue', root.dependencies.testAlias())
        Assert.assertFalse(child.dependencies.respondsTo('testAlias').isEmpty())
        Assert.assertEquals('testValue', child.dependencies.testAlias())
    }

    @Test
    void testAliasInvocationHandler() {

    }

    @Test
    void testLoadAliases() {
        DependencyAliasPlugin tested = new DependencyAliasPlugin()
        DependencyAliasExtension ext = new DependencyAliasExtension()
        tested.ext = ext
        ext.aliasFile = 'file'
        ext.aliasFileResolver = { String file -> [('test'): 'resolver'] }
        tested.loadAliases()
        Assert.assertEquals('resolver', tested.dependencyAliasesDef['test'])
    }

    @Test
    void testCreateExtensionOnSettings() {
        DependencyAliasPlugin tested = new DependencyAliasPlugin()
        DependencyAliasExtension ext = new DependencyAliasExtension()
        tested.ext = ext
        Settings s = [:] as Settings
        tested.createExtensionOnSettings(s, 'alias', ext)
        s.alias {
            aliasFile = 'file'
            aliasFileResolver = { [ ('val') :'resolver'] }
        }
        Assert.assertEquals('file', ext.aliasFile)
        Assert.assertEquals([ ('val') :'resolver'], ext.aliasFileResolver.resolve(ext.aliasFile))
    }
}
