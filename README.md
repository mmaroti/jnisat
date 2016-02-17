JNISat
======

This is a Java JNI wrapper around the PicoSat and MiniSat solver libraries.
The adapter JNI libraries (jpicosat, jminisat) are compiled for various
operating systems (Linux, Windows, Mac OS) and architectures (x86 and amd64)
and packaged into a single JAR file. At runtime the appropriate adapter
library is loaded from the JAR file by the Java virtual machine, and then
the operating system links and loads the native library (picosat, minisat) 
needed for actually solving SAT problems.

## Installation

First you need to install the native libraries on your operating system.
For example, on Ubuntu you need to run `sudo apt-get install minisat` 
to install the MiniSat solver, or you can get the latest source and compile
and install it yourself.    

Then clone this JNISat repository from GitHub, and run the `ant jar` command
to prepare the jnisat.jar file. Try running the `ant validate` command 
to check whether the solvers are working on your system. Then simply add the
jnisat.jar file to your java application, and you can start using the
JNISat java library. If you run into problems with the adapter libraries, then
run the `ant detect` command to see what is going on.

If no adapter library is compiled for your architecture (or you do not
trust the ones uploaded in the lib directory), then run `ant minisat`
or `ant picosat` to create the appropriate adapter libraries for your
operating system.

## License

The JNISat java library is released under the MIT license. It dynamically 
links against the PicoSat and MiniSat solvers, which are also released
under the MIT license, but does not contain any source code from those.
