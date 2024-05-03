# A simple servlet application on Eclipse GlassFish Embedded 7

## Build

```
mvn clean package
```

## Run with Maven

```
mvn exec:exec
```

Then access the app at http://localhost:8080


You can specify a different HTTP port on the command line:

```
mvn exec:exec -Dhttp.port=9090
```

## Run with plain `java` command

```
java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens=java.naming/javax.naming.spi=ALL-UNNAMED -cp 'target/classes:target/dependencies/*' ee.omnifish.glassfish.vt.EmbeddedGlassfishApp
```

NOTE: On Java 17, it's necessary to add the
`--add-opens java.base/java.lang=ALL-UNNAMED --add-opens=java.naming/javax.naming.spi=ALL-UNNAMED` JVM arguments.

## Apache Benchmark

https://httpd.apache.org/

Example - 10000 concurrent requets, 50000 in total

```
ab -n 50000 -c 10000 http://localhost:8080/
```