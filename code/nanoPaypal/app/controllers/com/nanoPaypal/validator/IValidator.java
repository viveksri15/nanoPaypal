package controllers.com.nanoPaypal.validator;

import java.util.Map;

public interface IValidator {
	public ValidationResponse validate(Map<String, String> parameters);
}
