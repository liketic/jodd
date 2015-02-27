// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.csselly;

/**
 * Inner selector of {@link CssSelector}.
 */
public abstract class Selector {

	/**
	 * Selector types.
	 */
	public enum Type {
		ATTRIBUTE, PSEUDO_CLASS, PSEUDO_FUNCTION
	}

	protected final Type type;

	protected Selector(Type type) {
		this.type = type;
	}

	/**
	 * Returns selector type.
	 */
	public Type getType() {
		return type;
	}
}
