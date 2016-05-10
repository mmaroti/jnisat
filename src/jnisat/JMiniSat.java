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

public class JMiniSat extends Solver {
	static {
		LibDetect.loadLibrary("jminisat");
	}

	protected long handle;
	protected boolean solvable;
	protected boolean simplified;

	protected final int simplify;
	protected static final int SIMPLIFY_NEVER = 0;
	protected static final int SIMPLIFY_ONCE = 1;
	protected static final int SIMPLIFY_ALWAYS = 2;

	/**
	 * Constructs a new MiniSAT instance with the given simplification method;
	 */
	public JMiniSat(int simplify) {
		handle = minisat_ctor();
		if (handle == 0)
			throw new OutOfMemoryError();

		this.simplify = simplify;
		solvable = true;
		simplified = false;
	}

	/**
	 * Constructs a new MiniSAT instance
	 */
	public JMiniSat() {
		this(SIMPLIFY_ONCE);
	}

	@Override
	public void reset() {
		if (handle != 0)
			minisat_dtor(handle);

		handle = minisat_ctor();
		solvable = true;
		simplified = false;
	}

	@Override
	protected void finalize() {
		if (handle != 0)
			minisat_dtor(handle);
		handle = 0;
	}

	@Override
	public int addVariable() {
		int lit = minisat_new_var(handle, LBOOL_UNDEF);
		minisat_set_frozen(handle, lit, true);
		return lit;
	}

	@Override
	public int addVariable(int flags) {
		byte polarity = (flags & FLAG_TRY_TRUE) != 0 ? LBOOL_TRUE
				: (flags & FLAG_TRY_FALSE) != 0 ? LBOOL_FALSE : LBOOL_UNDEF;
		int lit = minisat_new_var(handle, polarity);
		if ((flags & FLAG_ELIMINATE) == 0)
			minisat_set_frozen(handle, lit, true);
		if ((flags & FLAG_NODECISION) != 0)
			minisat_set_decision_var(handle, lit, false);
		return lit;
	}

	@Override
	public void addClause(int lit) {
		solvable = minisat_add_clause(handle, lit);
	}

	@Override
	public void addClause(int lit1, int lit2) {
		solvable = minisat_add_clause(handle, lit1, lit2);
	}

	@Override
	public void addClause(int lit1, int lit2, int lit3) {
		solvable = minisat_add_clause(handle, lit1, lit2, lit3);
	}

	@Override
	public void addClause(int... literals) {
		solvable = minisat_add_clause(handle, literals);
	}

	@Override
	public boolean solve() {
		if (simplify == SIMPLIFY_ONCE) {
			solvable = minisat_solve(handle, true, !simplified);
			simplified = true;
		} else if (simplify == SIMPLIFY_ALWAYS)
			solvable = minisat_solve(handle, true, false);
		else
			solvable = minisat_solve(handle, false, true);
		return solvable;
	}

	@Override
	public int getValue(int literal) {
		assert solvable;
		byte a = minisat_model_value(handle, literal);
		assert a == LBOOL_FALSE || a == LBOOL_TRUE;
		return a == LBOOL_TRUE ? 1 : -1;
	}

	protected static native long minisat_ctor();

	protected static native void minisat_dtor(long handle);

	protected static native int minisat_new_var(long handle, byte polarity);

	protected static native void minisat_set_decision_var(long handle, int lit,
			boolean value);

	protected static native void minisat_set_frozen(long handle, int lit,
			boolean value);

	protected static native boolean minisat_add_clause(long handle, int lit);

	protected static native boolean minisat_add_clause(long handle, int lit1,
			int lit2);

	protected static native boolean minisat_add_clause(long handle, int lit1,
			int lit2, int lit3);

	protected static native boolean minisat_add_clause(long handle, int[] lits);

	protected static native boolean minisat_solve(long handle,
			boolean simplify, boolean turnoff);

	protected static native boolean minisat_simplify(long handle);

	protected static native boolean minisat_eliminate(long handle,
			boolean turnoff);

	protected static native boolean minisat_is_eliminated(long handle, int lit);

	protected static native boolean minisat_okay(long handle);

	protected static final byte LBOOL_TRUE = 0;
	protected static final byte LBOOL_FALSE = 1;
	protected static final byte LBOOL_UNDEF = 2;

	protected static native byte minisat_model_value(long handle, int lit);
}
