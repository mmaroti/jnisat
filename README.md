JNISat
======

This is a JNI wrapper around the PicoSat and MiniSat SAT solver libraries.
The adapter JNI libraries (jpicosat, jminisat) compiled for various
operating systems (Linux, Windows, Mac OS) and architectures (x86 and amd64)
are packaged into a single JAR file. At runtime the appropriate adapter
library is loaded from the JAR by the Java virtual machine, and then
the operating system links and loads the native library (picosat, minisat) 
needed for actually solving SAT problems.

## Installation

First you need to install the native libraries on your operating system.
For example, on Ubuntu you need to run `sudo apt-get install minisat` 
to install the minisat solver, or you can get the latest source and compile
and install it as usual.    

Then clone the `jnisat˙ repository and run the `ant jar` command
to prepare the `jnisat.jar` file. Try running the `ant validate` command 
to see if the solvers are working on your system. Then simply add the
`jnisat.jar` file to your java application, and you can start using the
JNISat java library.

If no adapter library is compiled for your architecture (or you do not
trust the one uploaded in the `lib` directory), then run `ant minisat`
or `ant picosat` to create the appropriate adapter libraries for your
operating system.

## License

The JNISat java library is released under the MIT license. It dynamically 
links against the PicoSat and MiniSat solvers, which are also released
under the MIT license, but does not contain any source from those.
