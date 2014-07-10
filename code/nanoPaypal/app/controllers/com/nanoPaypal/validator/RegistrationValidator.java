package controllers.com.nanoPaypal.validator;

import java.util.Map;

import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.impl.exceptions.UserExistsException;
import com.nanoPaypal.user.specs.IUser;

public class RegistrationValidator implements IValidator {

	@Override
	public ValidationResponse validate(Map<String, String> parameters) {

		String userName = parameters.get("userName");
		String password = parameters.get("password");
		String password1 = parameters.get("password1");
		String email = parameters.get("email");

		ValidationResponse response = RequestValidator
				.isRegistrationFormInvalid(userName, password, password1, email);
		if (response != null)
			return response;

		try {
			IUser iUser = UserFactory.createUser(userName, password, email);
			if (iUser != null && iUser.getUID() != null)
				response = new ValidationResponse(true,
						INVALID_MESSAGE.SUCCESS, iUser);
		} catch (UserExistsException e) {
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_userOrEmailExists, null);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_general, null);
		}

		return response;
	}
}
