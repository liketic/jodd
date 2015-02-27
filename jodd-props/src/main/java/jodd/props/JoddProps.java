// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.Jodd;

/**
 * Jodd PROPS module.
 */
public class JoddProps {

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddProps.class);
	}

}