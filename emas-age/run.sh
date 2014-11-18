#!/bin/sh
mvn install
cd applications
mvn -q exec:java -Dage.config.properties=classpath:emas.properties

