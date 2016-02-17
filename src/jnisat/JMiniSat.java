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

	protected long handle;
	protected boolean solvable;

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
	public int addVariable(byte policy) {
		return minisat_new_var(handle, policy);
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
		solvable = minisat_solve(handle, true);
		return solvable;
	}

	@Override
	public int getValue(int literal) {
		assert solvable;
		byte a = minisat_model_value(handle, literal);
		assert a == 0 || a == 1;
		return a == 0 ? 1 : -1;
	}

	protected static native long minisat_ctor();

	protected static native void minisat_dtor(long handle);

	protected static native int minisat_new_var(long handle, byte polarity,
			boolean eliminate);

	protected static native boolean minisat_add_clause(long handle, int lit);

	protected static native boolean minisat_add_clause(long handle, int lit1,
			int lit2);

	protected static native boolean minisat_add_clause(long handle, int lit1,
			int lit2, int lit3);

	protected static native boolean minisat_add_clause(long handle, int[] lits);

	protected static native boolean minisat_solve(long handle, boolean simplify);

	protected static native boolean minisat_eliminate(long handle);

	protected static native boolean minisat_is_eliminated(long handle, int lit);

	protected static native boolean minisat_okay(long handle);

	protected static native byte minisat_model_value(long handle, int lit);

	public static void main(String[] args) {
		JMiniSat sat = new JMiniSat();
		System.out.println(sat.addVariable());
		System.out.println(sat.addVariable());
		sat.addClause(1, 2);
		System.out.println(JMiniSat.minisat_is_eliminated(sat.handle, 1));
		System.out.println(JMiniSat.minisat_is_eliminated(sat.handle, 2));
		System.out.println(sat.solve());
		System.out.println(JMiniSat.minisat_is_eliminated(sat.handle, 1));
		System.out.println(JMiniSat.minisat_is_eliminated(sat.handle, 2));
		System.out.println();
		sat.addClause(1, -2);
		System.out.println(sat.solve());
		sat.addClause(-1, 2);
		System.out.println(sat.solve());
		sat.addClause(-1, -2);
		System.out.println(sat.solve());
	}
}
