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

import org.sat4j.core.*;
import org.sat4j.minisat.*;
import org.sat4j.specs.*;

public class Sat4J extends Solver {
	protected final ISolver solver;
	protected boolean solvable;

	public Sat4J() {
		solver = SolverFactory.newDefault();
		solvable = true;
	}

	@Override
	public void reset() {
		solver.reset();
	}

	@Override
	public int addVariable(int flags) {
		return solver.nextFreeVarId(true);
	}

	@Override
	public void addClause(int lit) {
		addClause(new int[] { lit });
	}

	@Override
	public void addClause(int lit1, int lit2) {
		addClause(new int[] { lit1, lit2 });
	}

	@Override
	public void addClause(int lit1, int lit2, int lit3) {
		addClause(new int[] { lit1, lit2, lit3 });
	}

	@Override
	public void addClause(int... literals) {
		try {
			solver.addClause(new VecInt(literals));
		} catch (ContradictionException e) {
			solvable = false;
		}
	}

	@Override
	public boolean solve() {
		if (solvable)
			try {
				solvable = solver.isSatisfiable();
			} catch (TimeoutException e) {
				throw new RuntimeException(e.getMessage());
			}

		return solvable;
	}

	@Override
	public int getValue(int literal) {
		assert solvable;
		if (literal > 0)
			return solver.model(literal) ? 1 : -1;
		else
			return solver.model(-literal) ? -1 : 1;
	}
}
