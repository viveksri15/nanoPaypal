package controllers.com.nanoPaypal.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.nanoPaypal.configurationManager.Constants;

public class RequestValidator {

	private static Pattern emailPattern;
	static {
		emailPattern = Pattern.compile(Constants.EMAIL_PATTERN);
	}

	public static ValidationResponse isUserNamePasswordEmpty(String userName,
			String password) {
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
			ValidationResponse response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_blankUserNameOrPassword, null);
			return response;
		}
		return null;
	}
	public static ValidationResponse isRegistrationFormInvalid(String userName,
			String password, String password1, String email) {
		ValidationResponse response;
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)
				|| StringUtils.isEmpty(email) || StringUtils.isEmpty(password1)) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_incompleteForm, null);
			return response;
		}

		if (!password.equals(password1)) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_notMatchingPassword, null);
			return response;
		}

		Matcher matcher = emailPattern.matcher(email);
		if (!matcher.matches()) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_invalidEmail, null);
			return response;
		}

		return null;
	}
}
