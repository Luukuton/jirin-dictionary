# Jirin
A Java application for searching and parsing online monolingual Japanese dictionaries. Initially support only for [goo辞典](https://dictionary.goo.ne.jp/). The name comes from the lesser used Japanese word for dictionary, 辞林.

Current state of the UI:  
![current_ui](documentation/current_ui.gif)

## Releases
Latest: [Week 5](https://github.com/Luukuton/ot-harjoitustyo-hy2020/releases/tag/week5)

## Documentation
- [Software requirements specifications](documentation/software_requirements_specification.md)
- [Architecture](documentation/architecture.md)
- [Time accounting](documentation/time_accounting.md)

## Running

The application can be run with: 

```
mvn compile exec:java -Dexec.mainClass=fi.luukuton.jirin.Main
```

On Linux, if inputting Japanese directly to the search bar doesn't work, run this instead. In compiled builds, this is set by default.
```
mvn compile exec:java -Dexec.mainClass=fi.luukuton.jirin.Main -Djdk.gtk.version=2
```

## Testing

Tests are performed with: 

```
mvn test
```
Tests require an internet connection as they utlizie the online dictionary Goo.

Code coverage is created with: 

```
mvn jacoco:report
```

Code coverage can be viewed by opening _target/site/jacoco/index.html_ in a browser.

## Generating JAR file

Following command 

```
mvn package
```

generates a runnable JAR file, _jirin-1.0-SNAPSHOT.jar_, to the _target_ directory.

## JavaDoc

JavaDoc files are created with: 

```
mvn javadoc:javadoc
```

They can be viewed by opening _target/site/apidocs/index.html_ in a browser.

## CheckStyle

Checks defined in [checkstyle.xml](checkstyle.xml) can be executed with: 

```
mvn jxr:jxr checkstyle:checkstyle
```

Results can be viewed by opening _target/site/checkstyle.html_ in a browser.


## Dependencies
* Java 11
* Maven 

