/* File:   TheNativeMethodImpl.c
 * Desc:   'C' implementation of the native method
 * Author: rudolf.scheurer@hefr.ch
 */

#include <stdio.h>
#include "AClassWithNativeMethods.h"

JNIEXPORT void JNICALL Java_AClassWithNativeMethods_theNativeMethod
	(JNIEnv* env, jobject thisObj, jstring prompt) 	{
	    char buf[128];
	    const char *str = (*env)->GetStringUTFChars(env, prompt, NULL);
	    printf("Hello %s\n", str);
	    (*env)->ReleaseStringUTFChars(env, prompt, str);
}

