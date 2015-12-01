/*
 * Copyright © 2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * Some files may be comprised of various open source software components, each of which
 * has its own license that is located in the source code of the respective component.
 */

package com.vmware.oss.gradle.alias

import org.gradle.api.InvalidUserDataException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DefaultFileResolverTest {
    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder()

    @Test
    void testResolveByUrl() {
        FileResolver tested = new DefaultFileResolver()
        File file = testDir.newFile()
        file << 'val=testByUrl'
        Assert.assertEquals([('val'): 'testByUrl'], tested.resolve("file://${file.absolutePath}"))
    }

    @Test
    void testResolveByLocalPath() {
        FileResolver tested = new DefaultFileResolver()
        File file = testDir.newFile()
        file << 'val=testByLocalPath'
        Assert.assertEquals([('val'): 'testByLocalPath'], tested.resolve(file.absolutePath))
    }

    @Test(expected = InvalidUserDataException.class)
    void testResolveOther() {
        FileResolver tested = new DefaultFileResolver()
        tested.resolve('fakenonexistingfile')
    }
}
