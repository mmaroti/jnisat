JNISat
======

This is a JNI wrapper around the PicoSat SAT solver library packaged into
a JAR file, however it does not contain the PicoSAT library itself, but 
dynamically links to the currently available one on your system. On debian
derived distributions you can install it with `sudo apt-get install picosat`.

You can build the JAR file with `ant`, and validate it with `ant validate`.

## License

The JNISat java library is released under the MIT license.
