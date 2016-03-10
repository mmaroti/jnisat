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

public class Validate {
	private final Solver sat;
	private final int size;
	private final int[] table;

	public Validate(Solver solver, int size) {
		this.sat = solver;
		this.size = size;
		this.table = new int[size * size];
	}

	private void generate() {
		for (int i = 0; i < table.length; i++)
			table[i] = sat.addVariable();

		// reflexive
		for (int i = 0; i < size; i++)
			sat.addClause(table[i * (size + 1)]);

		// symmetric
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				sat.addClause(table[i * size + j], -table[j * size + i]);

		// transitive
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				for (int k = 0; k < size; k++)
					sat.addClause(-table[i * size + j], -table[j * size + k],
							table[i * size + k]);
	}

	public int findall() {
		int[] clause = new int[table.length];

		int a = 0;
		while (sat.solve()) {
			a += 1;

			for (int i = 0; i < table.length; i++) {
				int b = sat.getValue(table[i]);
				clause[i] = b > 0 ? -table[i] : table[i];
			}

			sat.addClause(clause);
		}

		return a;
	}

	public static void run(String name) {
		System.out.print(name + ": ");

		Solver sat;
		try {
			if (name.equals("minisat"))
				sat = new JMiniSat();
			else if (name.equals("picosat"))
				sat = new JPicoSat();
			// else if (name.equals("sat4j"))
			// sat = new Sat4J();
			else
				throw new IllegalArgumentException();
		} catch (LinkageError e) {
			System.out.println("not available");
			System.out.println("\t" + e.getMessage());
			return;
		}

		long time = System.currentTimeMillis();
		Validate validate = new Validate(sat, 8);
		validate.generate();
		int count = validate.findall();
		time = System.currentTimeMillis() - time;

		if (count != 4140)
			System.out.println("incorrect answer " + count);
		else
			System.out.println(time + " milliseconds");
	}

	public static void main(String[] args) {
		System.out.println("Calculating the 8th Bell number (4140 solutions)");
		run("minisat");
		run("picosat");
		// run("sat4j");
	}
}
