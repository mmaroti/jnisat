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
		LibDetect.loadLibrary("minisat");
	}

	private long handle;
	private boolean solvable;

	/**
	 * Constructs a new MiniSAT instance and reserves some memory.
	 */
	public JMiniSat() {
		handle = minisat_ctor();
		solvable = false;
		if (handle == 0)
			throw new OutOfMemoryError();
	}

	@Override
	public void reset() {
		if (handle != 0)
			minisat_dtor(handle);
		handle = minisat_ctor();
	}

	@Override
	protected void finalize() {
		if (handle != 0)
			minisat_dtor(handle);
		handle = 0;
	}

	@Override
	public int addVariable() {
		return minisat_newvar(handle, LBOOL_UNDEF);
	}

	@Override
	public void addClause(int lit) {
		solvable = minisat_addclause(handle, lit);
	}

	@Override
	public void addClause(int lit1, int lit2) {
		solvable = minisat_addclause(handle, lit1, lit2);
	}

	@Override
	public void addClause(int lit1, int lit2, int lit3) {
		solvable = minisat_addclause(handle, lit1, lit2, lit3);
	}

	@Override
	public void addClause(int... literals) {
		solvable = minisat_addclause(handle, literals);
	}

	@Override
	public boolean solve() {
		solvable = minisat_solve(handle, false);
		return solvable;
	}

	@Override
	public int getValue(int literal) {
		assert solvable;
		byte a = minisat_value(handle, literal);
		return a == LBOOL_TRUE ? 1 : a == LBOOL_FALSE ? -1 : 0;
	}

	private static native long minisat_ctor();

	private static native void minisat_dtor(long handle);

	private static final byte LBOOL_TRUE = 0;
	private static final byte LBOOL_FALSE = 1;
	private static final byte LBOOL_UNDEF = 2;

	private static native int minisat_newvar(long handle, byte policy);

	private static native boolean minisat_addclause(long handle, int lit);

	private static native boolean minisat_addclause(long handle, int lit1,
			int lit2);

	private static native boolean minisat_addclause(long handle, int lit1,
			int lit2, int lit3);

	private static native boolean minisat_addclause(long handle, int[] lits);

	private static native boolean minisat_solve(long handle, boolean simplify);

	private static native boolean minisat_eliminate(long handle);

	private static native boolean minisat_okay(long handle);

	private static native byte minisat_value(long handle, int lit);

	public static void main(String[] args) {
		Solver sat = new JMiniSat();
		System.out.println(sat.addVariable());
		System.out.println(sat.addVariable());
		sat.addClause(1, 2);
		System.out.println(sat.solve());
		sat.addClause(1, -2);
		System.out.println(sat.solve());
		sat.addClause(-1, 2);
		System.out.println(sat.solve());
		sat.addClause(-1, -2);
		System.out.println(sat.solve());
	}
}
