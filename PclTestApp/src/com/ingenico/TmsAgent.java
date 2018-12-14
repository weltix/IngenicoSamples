package com.ingenico;

public class TmsAgent {
	/** Native method */
	public native int tms(String param);

	/** Load JNI .so on initialization */
	static { System.loadLibrary("tms_android"); }
}
