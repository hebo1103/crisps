package net.crisps.cloud.framework.cache.aspectj.expression;

import org.springframework.expression.EvaluationException;
@SuppressWarnings("serial")
class VariableNotAvailableException extends EvaluationException {

	private final String name;

	public VariableNotAvailableException(String name) {
		super("Variable '" + name + "' is not available");
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
