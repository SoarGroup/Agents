#!/bin/bash

java -Xms1024m -Xmx1024m -classpath .:$JSOAR_HOME/lib/* Poc $SOAR_HOME
