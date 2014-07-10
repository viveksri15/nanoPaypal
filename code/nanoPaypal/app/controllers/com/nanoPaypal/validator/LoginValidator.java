package controllers.com.nanoPaypal.validator;

import java.util.Map;

import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.impl.exceptions.BadUserNamePasswordException;
import com.nanoPaypal.user.specs.IUser;

public class LoginValidator implements IValidator {
	@Override
	public ValidationResponse validate(Map<String, String> parameters) {

		String userName = parameters.get("userName");
		String password = parameters.get("password");

		ValidationResponse response = null;

		response = RequestValidator.isUserNamePasswordEmpty(userName, password);
		if (response != null)
			return response;

		try {
			IUser iUser = UserFactory.getUser(userName, password);
			if (iUser != null && iUser.getUID() != null)
				response = new ValidationResponse(true,
						INVALID_MESSAGE.SUCCESS, iUser);
			else
				response = new ValidationResponse(false,
						INVALID_MESSAGE.ERROR_badUserNamePassword, null);
		} catch (BadUserNamePasswordException e) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_badUserNamePassword, null);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_general, null);
		}

		return response;
	}
}
