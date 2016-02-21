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
 * This is the base class of all SAT solvers. All variables are numbered
 * starting from 1, and their negated literals are negative values.
 */
public abstract class Solver {
	/**
	 * Creates one of the solvers.
	 * 
	 * @param what
	 *            which solver to prefer
	 * @return a new solver
	 */
	public static Solver create(String what) {
		if (what == null || what.equals("minisat"))
			return new JMiniSat();
		else if (what.equals("picosat"))
			return new JPicoSat();
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Resets all memory associated with this instance.
	 */
	public abstract void reset();

	/**
	 * If the variable because a decision variable, then try the true value
	 * first.
	 */
	public static final int FLAG_TRY_TRUE = 0x01;

	/**
	 * If the variable becomes a decision variable, then try the false value
	 * first.
	 */
	public static final int FLAG_TRY_FALSE = 0x02;

	/**
	 * The variable can be eliminated by the solver (will not be mentioned in
	 * new clauses after the first solve).
	 */
	public static final int FLAG_ELIMINATE = 0x04;

	/**
	 * Try to avoid making decisions on this variable.
	 */
	public static final int FLAG_NODECISION = 0x08;

	/**
	 * Adds a new variable to the solver with the given special flags.
	 * 
	 * @param policy
	 *            one of the policy constants
	 * @return the positive literal of the new variable
	 */
	public abstract int addVariable(int flags);

	/**
	 * Adds a new variable to the solver.
	 * 
	 * @return the positive literal of the new variable
	 */
	public int addVariable() {
		return addVariable(0);
	}

	/**
	 * Adds a single literal clause to the solver.
	 * 
	 * @param lit
	 *            the literal to be added
	 */
	public abstract void addClause(int lit);

	/**
	 * Adds a two literal clause to the solver.
	 * 
	 * @param lit1
	 *            the first literal of the clause
	 * @param lit2
	 *            the second literal of the clause
	 */

	public abstract void addClause(int lit1, int lit2);

	/**
	 * Adds a three literal clause to the solver.
	 * 
	 * @param lit1
	 *            the first literal of the clause
	 * @param lit2
	 *            the second literal of the clause
	 * @param lit3
	 *            the third literal of the clause
	 */
	public abstract void addClause(int lit1, int lit2, int lit3);

	/**
	 * Adds a new clause to the solver.
	 * 
	 * @param literals
	 *            the list of literals (positive or negative variable indices)
	 *            of the new clause
	 */
	public abstract void addClause(int... literals);

	/**
	 * Solves the currently added variables and clauses.
	 * 
	 * @return <code>true</code> if the instance is solvable
	 */
	public abstract boolean solve();

	/**
	 * Queries the value of a literal in the solution.
	 * 
	 * @param literal
	 *            the index of the variable to be queried
	 * @return positive if the literal is true, negative if the literal is false
	 *         and zero if the value can be either true or false
	 */
	public abstract int getValue(int literal);
}
