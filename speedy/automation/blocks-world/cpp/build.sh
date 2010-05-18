#/bin/bash

g++ -L$SOAR_HOME/lib -I $SOAR_HOME/include -o poc poc.cpp -lClientSML -lConnectionSML -lElementXML
