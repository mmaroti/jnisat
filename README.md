JNISat
======
[![Build Status](https://travis-ci.org/mmaroti/jnisat.svg?branch=master)](https://travis-ci.org/mmaroti/jnisat)

This is a Java JNI wrapper around the PicoSat and MiniSat solver libraries.
The adapter JNI libraries (jpicosat, jminisat) are compiled for various
operating systems (Linux, Windows, Mac OS) and architectures (x86 and amd64)
and packaged into a single JAR file. At runtime the appropriate adapter
library is loaded from the JAR file by the Java virtual machine, and then
the operating system links and loads the native solver library (picosat,
minisat) that is actually doing the work.

## Installation

1. First you need to install the native solver library on your operating system.
For example, on Ubuntu you need to run `sudo apt-get install minisat picosat`
to install the MiniSat and PicoSat solvers. On Windows you do not have to do
anything, the adapter library statically contains the native library.

2. Then clone this JNISat repository from GitHub, and run the `ant build` command
to prepare the jnisat.jar file. Try running the `ant validate` command
to check whether the solvers are working on your system. Then simply add the
jnisat.jar file to your java application, and you can start using the
JNISat java library. If you run into problems with the adapter libraries, then
run `ant detect` to see what is going on.

3. If no adapter library is compiled for your architecture (or you do not
trust the ones uploaded in the lib directory), then run `ant jminisat`
or `ant jpicosat` to create the appropriate adapter libraries for your
operating system. On Windows you need to install [cygwin](https://www.cygwin.com/) 
with the mingw compiler and [ant](http://ant.apache.org/) to build the native
libraries, but the compiled adapter library is statically linked and will not be
dependent on anything else.

## License

The JNISat java library is released under the MIT license. It dynamically
links against the PicoSat and MiniSat solvers, which are also released
under the MIT license, but does not contain any source code from those.
