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

#include <jni.h>
#include <stdint.h>

#include "simp/SimpSolver.h"
#include "jnisat_COMiniSatPS.h"

static inline jlong encode(Minisat::SimpSolver* p) {
	return (jlong) (intptr_t) p;
}

static inline Minisat::SimpSolver* decode(jlong h) {
	return (Minisat::SimpSolver*) (intptr_t) h;
}

JNIEXPORT jlong JNICALL Java_jnisat_COMiniSatPS_cominisatps_1ctor(JNIEnv *env,
		jclass cls) {
	return encode(new Minisat::SimpSolver());
}

JNIEXPORT void JNICALL Java_jnisat_COMiniSatPS_cominisatps_1dtor(JNIEnv *env,
		jclass cls, jlong handle) {
	delete decode(handle);
}

JNIEXPORT jint JNICALL Java_jnisat_COMiniSatPS_cominisatps_1new_1var(JNIEnv *env,
		jclass cls, jlong handle, jboolean polarity, jboolean decision) {
	int v = decode(handle)->newVar((bool) polarity, (bool) decision);
	return (jint)(1 + v);
}

JNIEXPORT void JNICALL Java_jnisat_COMiniSatPS_cominisatps_1set_1decision_1var(
		JNIEnv *env, jclass cls, jlong handle, jint lit, jboolean value) {
	int v = lit > 0 ? lit - 1 : -lit - 1;
	decode(handle)->setDecisionVar(v, (bool) value);
}

JNIEXPORT void JNICALL Java_jnisat_COMiniSatPS_cominisatps_1set_1frozen(JNIEnv *env,
		jclass cls, jlong handle, jint lit, jboolean value) {
	int v = lit > 0 ? lit - 1 : -lit - 1;
	decode(handle)->setFrozen(v, (bool) value);
}

static inline Minisat::Lit convert(int lit) {
	return Minisat::toLit(lit > 0 ? (lit << 1) - 2 : ((-lit) << 1) - 1);
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1add_1clause__JI(
		JNIEnv *env, jclass cls, jlong handle, jint lit) {
	return decode(handle)->addClause(convert(lit));
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1add_1clause__JII(
		JNIEnv *env, jclass cls, jlong handle, jint lit1, jint lit2) {
	return decode(handle)->addClause(convert(lit1), convert(lit2));
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1add_1clause__JIII(
		JNIEnv *env, jclass cls, jlong handle, jint lit1, jint lit2,
		jint lit3) {
	return decode(handle)->addClause(convert(lit1), convert(lit2),
			convert(lit3));
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1add_1clause__J_3I(
		JNIEnv *env, jclass cls, jlong handle, jintArray lits) {
	jint len = env->GetArrayLength(lits);
	Minisat::vec < Minisat::Lit > vec(len);

	jint *p = (jint*) env->GetPrimitiveArrayCritical(lits, 0);
	for (jint i = 0; i < len; i++)
		vec[i] = convert(p[i]);
	env->ReleasePrimitiveArrayCritical(lits, p, 0);

	return decode(handle)->addClause_(vec);
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1solve(JNIEnv *env,
		jclass cls, jlong handle, jboolean simplify, jboolean turnoff) {
	return decode(handle)->solve((bool) simplify, (bool) turnoff);
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1simplify(JNIEnv *env,
		jclass cls, jlong handle) {
	return decode(handle)->simplify();
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1eliminate(JNIEnv *env,
		jclass cls, jlong handle, jboolean turnoff) {
	return decode(handle)->eliminate((bool) turnoff);
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1is_1eliminated(
		JNIEnv *env, jclass cls, jlong handle, jint lit) {
	int v = lit > 0 ? lit - 1 : -lit - 1;
	return decode(handle)->isEliminated(v);
}

JNIEXPORT jboolean JNICALL Java_jnisat_COMiniSatPS_cominisatps_1okay(JNIEnv *env,
		jclass cls, jlong handle) {
	return decode(handle)->okay();
}

JNIEXPORT jbyte JNICALL Java_jnisat_COMiniSatPS_cominisatps_1model_1value(JNIEnv *env,
		jclass cls, jlong handle, jint lit) {
	return (jbyte) Minisat::toInt(decode(handle)->modelValue(convert(lit)));
}
