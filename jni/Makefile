JAVA_HOME ?= $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
CLASS_PATH = ../src

vpath %.java $(CLASS_PATH)/jpicosat
vpath %.class $(CLASS_PATH)/jpicosat

libjpicosat.so : JPicoSat.c jpicosat_JPicoSat.h JPicoSat.java
	gcc -O3 -fPIC -shared -I$(JAVA_HOME)/include/ \
		-I$(JAVA_HOME)/include/linux/ JPicoSat.c -lpicosat -o libjpicosat.so

jpicosat_JPicoSat.h : JPicoSat.class JPicoSat.java
	javah -cp $(CLASS_PATH) jpicosat.JPicoSat

JPicoSat.class : JPicoSat.java
	javac -cp $(CLASS_PATH) $<

clean:
	rm $(CLASS_PATH)/jpicosat/JPicoSat.class
	rm jpicosat_JPicoSat.h
	rm libjpicosat.so
