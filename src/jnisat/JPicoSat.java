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

public class JPicoSat extends Solver {
	static {
		LibDetect.loadLibrary("picosat");

		int pv = getVersion();
		int av = getApiVersion();
		if (pv < av) {
			throw new UnsatisfiedLinkError("Your picosat version " + pv
					+ " is too old, need at least " + av);
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
	private boolean solvable;

	/**
	 * Constructs a new PicoSAT instance and reserves some memory.
	 */
	public JPicoSat() {
		handle = picosat_init();
		solvable = false;
		if (handle == 0)
			throw new OutOfMemoryError();
	}

	@Override
	public void reset() {
		if (handle != 0)
			picosat_reset(handle);
		handle = picosat_init();
	}

	@Override
	protected void finalize() {
		if (handle != 0)
			picosat_reset(handle);
		handle = 0;
	}

	@Override
	public int addVariable() {
		return picosat_inc_max_var(handle);
	}

	@Override
	public void addClause(int lit) {
		picosat_add(handle, lit);
		picosat_add(handle, 0);
	}

	@Override
	public void addClause(int lit1, int lit2) {
		picosat_add(handle, lit1);
		picosat_add(handle, lit2);
		picosat_add(handle, 0);
	}

	@Override
	public void addClause(int lit1, int lit2, int lit3) {
		picosat_add(handle, lit1);
		picosat_add(handle, lit2);
		picosat_add(handle, lit3);
		picosat_add(handle, 0);
	}

	@Override
	public void addClause(int... literals) {
		for (int lit : literals)
			picosat_add(handle, lit);
		picosat_add(handle, 0);
	}

	private static final int PICOSAT_UNKNOWN = 0;
	private static final int PICOSAT_SATISFIABLE = 10;
	private static final int PICOSAT_UNSATISFIABLE = 20;

	@Override
	public boolean solve() {
		int a = picosat_sat(handle, -1);
		if (a != PICOSAT_SATISFIABLE && a != PICOSAT_UNSATISFIABLE)
			throw new IllegalStateException();

		solvable = a == PICOSAT_SATISFIABLE;
		return solvable;
	}

	@Override
	public int getValue(int literal) {
		assert solvable;
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
}
