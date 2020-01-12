# VMware has ended active development of this project, this repository will no longer be updated.
# Grade Dependency Alias Plugin
Dependency Alias Plugin is an Apache 2.0 licensed Gradle plugin that provides a simple mean to alias Gradle-style dependency notation with simplified methods that appears to be built-into Gradle itself.
Providing this ability, one can simplify otherwise obscure dependencies by providing them with a clear name or even consolidate related dependencies into a single alias - take the Log4J example:
```gradle
compile 'org.apache.logging.log4j:log4j-api:2.4.1'
compile 'org.apache.logging.log4j:log4j-core:2.4.1'
```
These 2 artifacts are needed for incorporating Log4J into a project, throw SLF4J into the mix and you now have 3 artifacts.
With the Dependency Alias Plugin you could replace these dependencies, which alone have no meaning, with
```gradle
compile log4j()
```

## <a name="started"></a>Getting started with the Plugin
Currently the plugin is not yet available in standard means (see [Upcoming changes](#changes)) so for now it needs to be built manually.

To build and install the plugin into your local maven repository execute the following command
```bash
./gradlew clean build install
```

The Dependency Alias Plugin is unique in that it's not a Project plugin but rather a Settings plugin which means that it needs to be applied in your project's ```settings.gradle``` file like so:
```gradle
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath 'com.vmware.gradle:gradle-dependency-alias-plugin:0.1.0-SNAPSHOT'
    }
}

apply plugin: 'com.vmware.dependency-alias'
```
Make sure to include ```mavenLocal()``` in the repositories closure of the buildscript configuration in ```settings.gradle```(see [Examples](#examples) for more examples)
No changes are required in the ```build.gradle``` file

## <a name="examples"></a>Examples
This code base provides 3 working examples of injecting the ```junit()``` alias into the build.
See samples under ```/src/samples/```

the ```alias``` configuration closure present in the 2 custom samples provides a way to change the plugin's behavior either by defining a different properties file to look for or by defining a whole different way of obtaining alias to dependency mapping.
Note that alias definition in the standard format is done by adding a line to the alias file (defaults to ```alias.properties``` in the same location as ```settings.gradle```) in the form ```alias=dependency```

## Compatibility
This plugin was tested with the following Gradle versions:
- 2.6
- 2.7
- 2.8
- 2.9

It does not mean that the plugin won't work on earlier versions of Gradle.
It does mean that the integration tests (using Gradle's TestKit) won't work on pre 2.6 versions.

## <a name="changes"></a>Upcoming changes
- [ ] Continuous Integration
- [ ] Coverage Report (Currently unit tests cover ~80%, 100% with integration tests) 
- [ ] Make plugin accessible via standard manners (e.g. Bintray, maven central, Gradle plugin portal)
- [X] Support aliasing collection of dependencies
- [ ] Support overriding version with alias parameters (for aliases with singleton dependency only)
