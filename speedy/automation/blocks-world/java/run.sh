#!/bin/bash

java -Xms1024m -Xmx1024m -classpath .:$SOAR_HOME/share/java/sml.jar Poc $SOAR_HOME $1
