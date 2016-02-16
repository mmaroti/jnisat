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
	private final JNISat sat;
	private final int size;
	private final int[] table;

	public Validate(JNISat solver, int size) {
		this.sat = solver;
		this.size = size;
		this.table = new int[size * size];
	}

	private void generate() {
		for (int i = 0; i < table.length; i++)
			table[i] = sat.addVariable();
	}

	private void reflexive() {
		for (int i = 0; i < size; i++)
			sat.addClause(table[i * (size + 1)]);
	}

	private void antisymm() {
		for (int i = 1; i < size; i++)
			for (int j = 0; j < i; j++)
				sat.addClause(-table[i * size + j], -table[j * size + i]);
	}

	private void transitive() {
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

	public void run() {
		long time = System.currentTimeMillis();

		generate();
		reflexive();
		antisymm();
		transitive();
		int a = findall();

		time = System.currentTimeMillis() - time;

		System.out.println("Number of posets of size " + size + " is: " + a);
		System.out.println("Elapsed time: " + time + " milliseconds");
	}

	public static void main(String[] args) {
		JNISat pico = new JPicoSat();
		Validate verify = new Validate(pico, 5);
		verify.run();
	}
}
