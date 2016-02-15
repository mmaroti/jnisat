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

public class JPicoSat {
	static {
		try {
			System.loadLibrary("jpicosat");
		} catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError(
					"JPicoSat: the jpicosat JNI library is not found");
		}

		int pv = getVersion();
		int av = getApiVersion();
		if (pv < av) {
			throw new UnsatisfiedLinkError("JPicoSat: your picosat version "
					+ pv + " is too old, need at least " + av);
		}
	}

	/**
	 * @return the actual version of the PicoSAT solver library installed on
	 *         your system
	 */
	public static int getVersion() {
		return Integer.parseInt(picosat_version());
	}

	/**
	 * @return the expected version of the PicoSAT solver library that this
	 *         JPicoSat JNI library needs
	 */
	public static int getApiVersion() {
		return picosat_api_version();
	}

	private long handle;

	/**
	 * Constructs a new PicoSAT instance and reserves some memory.
	 */
	public JPicoSat() {
		handle = picosat_init();
		assert handle != 0;
	}

	/**
	 * Resets all PicoSAT memory associated with this instance.
	 */
	public void reset() {
		assert handle != 0;
		picosat_reset(handle);
		handle = picosat_init();
		assert handle != 0;
	}

	@Override
	protected void finalize() {
		assert handle != 0;
		picosat_reset(handle);
		handle = 0;
	}

	public int addVariable() {
		return picosat_inc_max_var(handle);
	}

	public void addClause(int... literals) {
		for (int lit : literals)
			picosat_add(handle, lit);
		picosat_add(handle, 0);
	}

	public int solve() {
		return picosat_sat(handle, -1);
	}

	public int getValue(int literal) {
		return picosat_deref(handle, literal);
	}

	private static native String picosat_version();

	private static native int picosat_api_version();

	private static native long picosat_init();

	private static native void picosat_reset(long handle);

	private static native int picosat_inc_max_var(long handle);

	private static native int picosat_add(long handle, int lit);

	private static native int picosat_sat(long handle, int decision_limit);

	private static native int picosat_deref(long handle, int lit);

	public static void main(String[] args) {
		JPicoSat sat = new JPicoSat();
		System.out.println(sat.addVariable());
		System.out.println(sat.addVariable());
		sat.addClause(-1, -2);
		sat.addClause(1, -2);
		sat.addClause(-1, 2);
		System.out.println(sat.solve());
		System.out.println(sat.getValue(1));
		System.out.println(sat.getValue(2));
	}
}
