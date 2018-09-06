/**
 * Copyright (c) 2016, Miklos Maroti, University of Szeged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package org.jnisat;

import java.io.*;

public class LibDetect {
	private static String getLibDir() {
		String name;
		String arch;

		String osname = System.getProperty("os.name");
		if (osname.startsWith("Linux"))
			name = "linux";
		else if (osname.startsWith("Windows"))
			name = "win";
		else if (osname.startsWith("Mac OS X") || osname.startsWith("Darwin"))
			name = "osx";
		else
			return "unknown";

		String osarch = System.getProperty("os.arch");
		if (osarch.equals("x86") || osarch.equals("i386"))
			arch = "32";
		else if (osarch.equals("x86_64") || osarch.equals("amd64"))
			arch = "64";
		else
			return "unknown";

		return name + arch;
	}

	private static final String LIBDIR = getLibDir();

	public static void loadLibrary(String name) {
		try {
			String libname = System.mapLibraryName(name);
			String fullname = "/lib/" + LIBDIR + "/" + libname;
			InputStream is = LibDetect.class.getResourceAsStream(fullname);
			if (is == null)
				throw new FileNotFoundException(fullname
						+ " not found inside JAR");

			File temp;
			OutputStream os;

			int i = libname.lastIndexOf('.');
			if (i < 0)
				i = libname.length();
			temp = File.createTempFile(libname.substring(0, i) + "-",
					libname.substring(i));
			temp.deleteOnExit();
			os = new FileOutputStream(temp);

			try {
				byte[] buffer = new byte[4096];
				int count;

				while ((count = is.read(buffer)) != -1) {
					os.write(buffer, 0, count);
				}
			} finally {
				os.close();
				is.close();
			}

			System.load(temp.getAbsolutePath());
		} catch (Exception e) {
			throw new UnsatisfiedLinkError(e.getMessage());
		}
	}

	private static String testLibrary(String name) {
		try {
			System.loadLibrary(name);
			return "installed";
		} catch (UnsatisfiedLinkError e) {
			Object url = LibDetect.class.getResource("/lib/" + LIBDIR + "/"
					+ System.mapLibraryName(name));
			return url != null ? "found in jar" : "not found";
		}
	}

	public static void main(String[] args) {
		if (args.length == 1 && args[0].equals("libdir"))
			System.out.println(LIBDIR);
		else if (args.length == 2 && args[0].equals("testlib"))
			System.out.println(testLibrary(args[1]));
		else if (args.length == 2 && args[0].equals("libname"))
			System.out.println(System.mapLibraryName(args[1]));
		else {
			System.out.println("libdir: " + LIBDIR);
			System.out.println("picosat: solver " + testLibrary("picosat")
					+ ", adapter " + testLibrary("jpicosat"));
			System.out.println("minisat: solver " + testLibrary("minisat")
					+ ", adapter " + testLibrary("jminisat"));
			System.out.println("cominisatps: solver with adapter "
					+ testLibrary("cominisatps"));
		}
	}
}
