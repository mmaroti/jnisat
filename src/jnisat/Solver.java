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

public abstract class Solver {
	/**
	 * Resets all memory associated with this instance.
	 */
	public abstract void reset();

	/**
	 * The variable cannot be eliminated, and no polarity set.
	 */
	public static final byte POLICY_DEFAULT = 0;

	/**
	 * The variable cannot be eliminated, and polarity is set to true (that
	 * value will be tried first under decision).
	 */
	public static final byte POLICY_TRUE = 1;

	/**
	 * The variable cannot be eliminated, and polarity is set to false (that
	 * value will be tried first under decision).
	 */
	public static final byte POLICY_FALSE = 2;

	/**
	 * The variable can be eliminated (should not be used after the first solve
	 * command), and but no polarity is set.
	 */
	public static final byte POLICY_ELIMINATE = 3;

	/**
	 * Adds a new variable to the solver with the given policy.
	 *
	 * @param policy
	 *            one of the policy constants
	 * @return the positive literal of the new variable
	 */
	public abstract int addVariable(byte policy);

	/**
	 * Adds a new variable to the solver.
	 *
	 * @return the positive literal of the new variable
	 */
	public int addVariable() {
		return addVariable(POLICY_DEFAULT);
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
