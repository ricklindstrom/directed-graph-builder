
Directed Graph Builder
======================

Utilities for building useful directed graphs images from Java code, Java artifacts, or call stacks of running threads.
----------------------

This project contains utilities for 

BUILDING
--------
  * Directed graphs - Primarily graphviz files (but also Gephi (gefx) or TGF files)

FROM
----
  * Java code,
  * Java artifacts like runtime Spring configuration,
  * Call Stacks of Running Threads,
  * (Maybe other sources soon.)


WHY
---
Because the ability to quickly navigate and understand a large software system is essential.

Sample Output Fragment
----------------------
(Shrunken to protect the guilty.)

http://directedgraphbuilder.googlecode.com/svn/trunk/spring-context-example.png

(This link to this image likes to break. But trust me, the generated diagram for a project with a dozen spring context files hundreds of configured beans looks pretty good and is really useful for understanding the architecture of a system.)


Sample Usage
------------

*Can be used to aid generating Graphviz files* (that are reasonably-well laid out and colored) from within Java code:
```java
(new Graph()).addEdge("hello", "world").save("helloworld.dot");
```
See HowToUseGraphJava for more detail usage and examples.


*Can be used to generate Graphviz files from runtime spring configuration:*
```xml
<bean id="graphvizPostProcessor" class="com.directedgraphbuilder.GraphvizPostProcessor"/>
```
Each node and edge is clustered and colored based on what spring configuration file defines it.

*Can be used to generate Graphviz files from executing threads.*

```java
    CallGraphBuilder b = new CallGraphBuilder();
    b.add(Thread.currentThread().getStackTrace());     
    Graph g = b.buildGraph();
```

Alternatively, CallGraphBuilder can also manage the Graph (in a ThreadLocal variable)
```java
    CallGraphBuilder.start();
    CallGraphBuilder.capture();
    Graph g = CallGraphBuilder.stop();
    g.save();
```


==To Build==
```bash
    mvn clean install
```
