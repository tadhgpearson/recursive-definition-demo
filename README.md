Open API Schema validation test
===============================

Demonstrates [issue OpenAPI4J #106](https://github.com/openapi4j/openapi4j/issues/106)
where a recursive data structure cannot be parsed.

If the bug is fixed,

    > mvn clean verify

will work correctly.

You can also test using a built in Jetty server. Do

    > mvn jetty:run
    
to start the server and then call http://localhost:7895/rest in your browser
to see the response

Requirements
------------
* Java 11+
* Maven 3.6+
