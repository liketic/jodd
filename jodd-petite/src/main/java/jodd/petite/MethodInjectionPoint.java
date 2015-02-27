// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Method;

/**
 * Method injection points.
 */
public class MethodInjectionPoint {

	public static final MethodInjectionPoint[] EMPTY = new MethodInjectionPoint[0]; 

	public final Method method;
	public final String[][] references;

	MethodInjectionPoint(Method method, String[][] references) {
		this.method = method;
		this.references = references;
	}

}
