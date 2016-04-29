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

/**
 * This is the JNI adapter for the COMiniSatPS solver as participated in the SAT
 * 2015 race.
 */
public class COMiniSatPS extends Solver {
	static {
		LibDetect.loadLibrary("cominisatps");
	}

	protected long handle;
	protected boolean solvable;
	protected int simplify;

	protected static final int SIMPLIFY_NEVER = 0;
	protected static final int SIMPLIFY_ONCE = 1;
	protected static final int SIMPLIFY_ALWAYS = 1;

	/**
	 * Constructs a new solver instance with the given simplification method;
	 */
	public COMiniSatPS(int simplify) {
		handle = cominisatps_ctor();
		if (handle == 0)
			throw new OutOfMemoryError();

		solvable = true;
		this.simplify = simplify;
	}

	/**
	 * Constructs a new solver instance
	 */
	public COMiniSatPS() {
		this(SIMPLIFY_NEVER);
	}

	@Override
	public void reset() {
		if (handle != 0)
			cominisatps_dtor(handle);
		handle = cominisatps_ctor();
		solvable = true;
	}

	@Override
	protected void finalize() {
		if (handle != 0)
			cominisatps_dtor(handle);
		handle = 0;
	}

	@Override
	public int addVariable() {
		int lit = cominisatps_new_var(handle, true, true);
		cominisatps_set_frozen(handle, lit, true);
		return lit;
	}

	@Override
	public int addVariable(int flags) {
		boolean polarity = (flags & FLAG_TRY_FALSE) == 0;
		boolean decision = (flags & FLAG_NODECISION) == 0;
		int lit = cominisatps_new_var(handle, polarity, decision);
		if ((flags & FLAG_ELIMINATE) == 0)
			cominisatps_set_frozen(handle, lit, true);
		return lit;
	}

	@Override
	public void addClause(int lit) {
		solvable &= cominisatps_add_clause(handle, lit);
	}

	@Override
	public void addClause(int lit1, int lit2) {
		solvable &= cominisatps_add_clause(handle, lit1, lit2);
	}

	@Override
	public void addClause(int lit1, int lit2, int lit3) {
		solvable &= cominisatps_add_clause(handle, lit1, lit2, lit3);
	}

	@Override
	public void addClause(int... literals) {
		solvable &= cominisatps_add_clause(handle, literals);
	}

	@Override
	public boolean solve() {
		if (!solvable)
			return false;

		if (simplify == SIMPLIFY_ONCE) {
			solvable = cominisatps_solve(handle, true, true);
			simplify = SIMPLIFY_NEVER;
		} else if (simplify == SIMPLIFY_ALWAYS)
			solvable = cominisatps_solve(handle, true, false);
		else
			solvable = cominisatps_solve(handle, false, true);

		return solvable;
	}

	@Override
	public int getValue(int literal) {
		assert solvable;
		byte a = cominisatps_model_value(handle, literal);
		assert a == LBOOL_FALSE || a == LBOOL_TRUE;
		return a == LBOOL_TRUE ? 1 : -1;
	}

	protected static native long cominisatps_ctor();

	protected static native void cominisatps_dtor(long handle);

	protected static native int cominisatps_new_var(long handle, boolean polarity, boolean decision);

	protected static native void cominisatps_set_decision_var(long handle, int lit,
			boolean value);

	protected static native void cominisatps_set_frozen(long handle, int lit,
			boolean value);

	protected static native boolean cominisatps_add_clause(long handle, int lit);

	protected static native boolean cominisatps_add_clause(long handle, int lit1,
			int lit2);

	protected static native boolean cominisatps_add_clause(long handle, int lit1,
			int lit2, int lit3);

	protected static native boolean cominisatps_add_clause(long handle, int[] lits);

	protected static native boolean cominisatps_solve(long handle,
			boolean simplify, boolean turnoff);

	protected static native boolean cominisatps_simplify(long handle);

	protected static native boolean cominisatps_eliminate(long handle,
			boolean turnoff);

	protected static native boolean cominisatps_is_eliminated(long handle, int lit);

	protected static native boolean cominisatps_okay(long handle);

	protected static final byte LBOOL_TRUE = 0;
	protected static final byte LBOOL_FALSE = 1;
	protected static final byte LBOOL_UNDEF = 2;

	protected static native byte cominisatps_model_value(long handle, int lit);
}
