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

class DefaultFileResolver implements FileResolver {

    def Map<Object, Object> resolve(String file) {
        Closure<Map<Object, Object>> loadProps = { stream ->
            Properties props = new Properties()
            props.load(stream)
            return props
        }

        try {
            URL url = new URL(file)
            return url.withInputStream(loadProps)
        } catch (MalformedURLException e) {
            File f = new File(file)
            if (f.exists()) {
                return f.withInputStream(loadProps)
            } else {
                throw new InvalidUserDataException("Could not resolve ${file} using the default resolver. Please check that the file exists or consider creating a custom resolver")
            }
        }
    }

}
