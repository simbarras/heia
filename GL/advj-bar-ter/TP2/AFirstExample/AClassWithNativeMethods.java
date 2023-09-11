/* File:	  AClassWithNativeMethods.java
 * Desc:	  Java program to demonstrate
 *		      a simple native method invocation
 * Author:	rudolf.scheurer@hefr.ch
 */

/**
 * AClassWithNativeMethods
 */
public class AClassWithNativeMethods {
    
	public native void theNativeMethod(String person);
	
 	public void aJavaMethod() {
 	    theNativeMethod("Tout le monde"); // the native method
	}
}

