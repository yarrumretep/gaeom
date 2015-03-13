# Latest Release #
  * 1.0.0-b1 - July 19, 2011
  * 1.0.0-b2-SNAPSHOT may be more recent

# Summary #
This project aims to deliver a simple to use, yet powerful and performant object-datastore mapping engine.

First, you define your persistent objects
```
class MappingFramework
{
    String name;
    String description;
    boolean rocks;
}
```

Then, you can store instances in an [ObjectStoreSession](http://wiki.gaeom.googlecode.com/hg/apidocs/index.html?com/google/code/gaeom/ObjectStoreSession.html)
```
ObjectStore os = ObjectStore.Factory.create();  // usually done once per JVM
ObjectStoreSession oss = os.beginSession();     // usually done once per request and used throughout

MappingFramework gaeom = new MappingFramework();
gaeom.name = "gaeom";
gaeom.description = "Supah object-datastore mapping";
gaeom.rocks = true;

oss.store(gaeom).id(1).now();                   // id() is optional
```

You can retrieve objects like this
```
MappingFramework mf1 = oss.load(MappingFramework.class).id(1).now();
```
or query for the first instance
```
MappingFramework mf2 = oss.find(MappingFramework.class).single().now();
```
or the first instance that rocks
```
MappingFramework mf3 = oss.find(MappingFramework.class).filter("rocks", true).single().now();
```
of you can query for all instances
```
List<MappingFramework> all = oss.find(MappingFramework.class).now();
```

That's just the beginning...

While we are working on fleshing out the documentation, please feel free to browse the test code for examples of various features: [Test Code](http://code.google.com/p/gaeom/source/browse/#hg%2Fsrc%2Ftest%2Fjava%2Fcom%2Fgoogle%2Fcode%2Fgaeom%2Ftest)

# Feature Overview #
> gaeom has the following list of features to help meet your GAE persistence needs.
    * Automatic mapping of POJOs to `DatastoreService` Entities
    * Uniqueness guarantee scoped by `ObjectStoreSession`
    * Activation controls at query-time to ensure you pull only data that you want - by level or by filter-pattern
    * Seamless embedding of objects for performance and queriability - `@Embedded`
    * Transparent polymorphic relationships and embedded objects
    * Declarative key parenting - `@Parent` and `@Child`
    * Customizable encoding/decoding - `@EncodeWith`
    * Convenient filter shortcuts
      * `filterBetween()`
      * `filterBeginsWith()`
    * High performance is a specific feature
      * Built-in instance caching for objects that change infrequently - `@Cached`
      * Automatic batching of generations of relationship instance hydration
      * Robust retries to allow running with EVENTUAL read consistency setting (the default)
      * Cached metadata to speed instance hydration

# Usage #

## Direct ##
Include the following jars in your classpath:
  * `gaeom-1.0.0-b1.jar`  - http://gaeom.googlecode.com/files/gaeom-1.0.0-b1.jar
  * `guava-r09.jar` - http://guava-libraries.googlecode.com/files/guava-r09.zip
  * `commons-beanutils-1.8.3.jar` - http://apache.mirrors.tds.net//commons/beanutils/binaries/commons-beanutils-1.8.3-bin.zip


## Maven ##
We are actively working on improving this software in preparation for its first release.  For now, if you wish to try it out, you can include the following dependency:

```
  	<dependency>
  		<groupId>com.google.code.gaeom</groupId>
  		<artifactId>gaeom</artifactId>
  		<version>1.0.0-b1</version>
  	</dependency>
```

To track the latest snapshot releases (may be quite frequent):
```
  	<dependency>
  		<groupId>com.google.code.gaeom</groupId>
  		<artifactId>gaeom</artifactId>
  		<version>1.0.0-b2-SNAPSHOT</version>
  	</dependency>
```
and the following repository entry
```
    <repository>
      <id>Sonatype</id>
      <url>https://oss.sonatype.org/content/groups/public</url>
    </repository>
```