# Swing Chart
Chart for java swing

## Installation
This project library do not available in maven central. so you can install with the jar library
- Copy jar library file to the root project. exp : `library/swing-chart-1.1.1.jar`
- Add this code to `pom.xml`
``` xml
<dependency>
    <groupId>raven.chart</groupId>
    <artifactId>swing-chart</artifactId>
    <version>1.1.1</version>
    <scope>system</scope>
    <systemPath>${basedir}/library/swing-chart-1.1.1.jar</systemPath>
</dependency>
```
- Other library are use with this library
``` xml
<dependency>
  <groupId>com.formdev</groupId>
  <artifactId>flatlaf</artifactId>
  <version>3.4</version>
</dependency>

<dependency>
  <groupId>com.formdev</groupId>
  <artifactId>flatlaf-extras</artifactId>
  <version>3.4</version>
</dependency>

<dependency>
    <groupId>com.miglayout</groupId>
    <artifactId>miglayout-swing</artifactId>
    <version>11.2</version>
</dependency>
```
