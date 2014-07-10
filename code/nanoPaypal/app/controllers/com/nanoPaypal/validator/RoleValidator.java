package controllers.com.nanoPaypal.validator;

import java.util.Map;
import java.util.Set;

import com.nanoPaypal.user.impl.ROLE;
import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.specs.IUser;

public class RoleValidator implements IValidator {
	@Override
	public ValidationResponse validate(Map<String, String> parameters) {

		String uid = parameters.get("uid");
		String page = parameters.get("page");

		ValidationResponse response = null;

		IUser iUser = null;
		try {
			iUser = UserFactory.getUser(uid);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ValidationResponse(false,
					INVALID_MESSAGE.ERROR_general, null);
			return response;
		}
		if (iUser != null && iUser.getUID() != null && iUser.getRoles() != null
				&& !iUser.getRoles().isEmpty()) {

			Set<ROLE> set = controllers.Constants.roles.get(page);
			if (set != null) {
				for (ROLE r : set) {
					if (iUser.getRoles().contains(r)) {
						response = new ValidationResponse(true,
								INVALID_MESSAGE.SUCCESS, iUser);
						return response;
					}
				}
			}

		}
		response = new ValidationResponse(false, INVALID_MESSAGE.ERROR_general,
				null);
		return response;
	}
}
