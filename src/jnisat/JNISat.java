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

package jnisat;

import java.io.*;

public abstract class JNISat {
	public abstract void reset();

	public abstract int addVariable();

	public abstract void addClause(int... literals);

	public abstract boolean solve();

	public abstract int getValue(int literal);

	private static String getLibrarySuffix() {
		String os;
		String arch;
		String ext;

		String osname = System.getProperty("os.name");
		if (osname.startsWith("Linux")) {
			os = "linux";
			ext = "so";
		} else if (osname.startsWith("Windows")) {
			os = "win";
			ext = "dll";
		} else if (osname.startsWith("Mac OS") || osname.startsWith("Darwin")) {
			os = "osx";
			ext = "dylib";
		} else
			throw new RuntimeException("unknown operating system");

		String osarch = System.getProperty("os.arch");
		if (osarch.equals("x86") || osarch.equals("i386"))
			arch = "32";
		else if (osarch.equals("x86_64") || osarch.equals("amd64"))
			arch = "64";
		else
			throw new RuntimeException("unknown architecture");

		return "-" + os + arch + "." + ext;
	}

	public final static String LIBRARY_SUFFIX = getLibrarySuffix();

	protected static void loadLibrary(String name) {
		name = name + LIBRARY_SUFFIX;
		InputStream is = JNISat.class.getResourceAsStream("/" + name);
		if (is == null)
			throw new UnsatisfiedLinkError("Could not find " + name
					+ " inside the JAR");

		File temp;
		OutputStream os;

		try {
			int i = name.lastIndexOf('.');
			temp = File.createTempFile(name.substring(0, i),
					name.substring(i + 1));
			temp.deleteOnExit();
			os = new FileOutputStream(temp);
		} catch (IOException e) {
			throw new UnsatisfiedLinkError(e.getMessage());
		}

		try {
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
		} catch (IOException e) {
			throw new UnsatisfiedLinkError(e.getMessage());
		}

		System.load(temp.getAbsolutePath());
	}
}
