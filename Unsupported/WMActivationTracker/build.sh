#/bin/bash

g++ -o gen_wma \
	gen_wma.cpp \
	-I$SOAR_HOME/include \
	-L$SOAR_HOME/lib \
	-lClientSML \
	-lConnectionSML \
	-lElementXML
