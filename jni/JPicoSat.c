/**
 * Copyright (c) 2006 - 2014, Armin Biere, Johannes Kepler University.
 *               2016, Miklos Maroti, University of Szeged
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
#include <stdio.h>

#include <picosat/picosat.h>
#include "jpicosat_JPicoSat.h"

JNIEXPORT jstring JNICALL Java_jpicosat_JPicoSat_picosat_1version(JNIEnv *env, jclass cls) {
	const char *msg = picosat_version();
	return (*env)->NewStringUTF(env, msg);
}

JNIEXPORT jint JNICALL Java_jpicosat_JPicoSat_picosat_1api_1version(JNIEnv *env, jclass cls) {
	return PICOSAT_API_VERSION;
}

JNIEXPORT jlong JNICALL Java_jpicosat_JPicoSat_picosat_1init(JNIEnv *env, jclass cls) {
	return (jlong) picosat_init();
}

JNIEXPORT void JNICALL Java_jpicosat_JPicoSat_picosat_1reset(JNIEnv *env, jclass cls, jlong handle) {
	picosat_reset((PicoSAT*) handle);
}

JNIEXPORT jint JNICALL Java_jpicosat_JPicoSat_picosat_1inc_1max_1var(JNIEnv *env, jclass cls, jlong handle) {
	return picosat_inc_max_var((PicoSAT*) handle);
}

JNIEXPORT jint JNICALL Java_jpicosat_JPicoSat_picosat_1add(JNIEnv *env, jclass cls, jlong handle, jint lit) {
	return picosat_add((PicoSAT*) handle, lit);
}

JNIEXPORT jint JNICALL Java_jpicosat_JPicoSat_picosat_1sat(JNIEnv *env, jclass cls, jlong handle, jint decision_limit) {
	return picosat_sat((PicoSAT*) handle, decision_limit);
}

JNIEXPORT jint JNICALL Java_jpicosat_JPicoSat_picosat_1deref(JNIEnv *env, jclass cls, jlong handle, jint lit) {
	return picosat_deref((PicoSAT*) handle, lit);
}
