package controllers.com.nanoPaypal.validator;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.specs.IUser;

public class PasswordChangeValidator implements IValidator {

	@Override
	public ValidationResponse validate(Map<String, String> parameters) {

		String userName = parameters.get("userName");
		String currentPassword = parameters.get("currentPassword");
		String password = parameters.get("password");
		String password1 = parameters.get("password1");

		ValidationResponse response = null;
		if (StringUtils.isEmpty(currentPassword)
				|| StringUtils.isEmpty(password)
				|| StringUtils.isEmpty(password1)) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_incompleteForm, null);
			return response;
		}

		if (!password.equals(password1)) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_notMatchingPassword, null);
			return response;
		}

		IUser iUser = null;
		try {
			iUser = UserFactory.getUser(userName, currentPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (iUser == null || iUser.getUID() == null) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_invalidPassword, null);
			return response;
		} else {
			boolean setPassword = iUser.setPassword(password);
			if (!setPassword) {
				response = new ValidationResponse(false,
						INVALID_MESSAGE.ERROR_general, null);
			} else {
				response = new ValidationResponse(false,
						INVALID_MESSAGE.SUCCESS, iUser);
			}
		}

		return response;
	}
}
