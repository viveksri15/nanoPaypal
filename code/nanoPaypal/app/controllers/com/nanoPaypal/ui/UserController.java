package controllers.com.nanoPaypal.ui;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.main;
import views.html.register;

import com.nanoPaypal.configurationManager.Config;
import com.nanoPaypal.transaction.impl.AccountFactory;
import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.impl.ROLE;
import com.nanoPaypal.user.specs.IUser;

import controllers.com.nanoPaypal.validator.LoginValidator;
import controllers.com.nanoPaypal.validator.RegistrationValidator;
import controllers.com.nanoPaypal.validator.ValidationResponse;

public class UserController extends Controller {

	public static Result logout() {
		session().clear();
		return redirect("/");
	}

	public static Result login() {
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String userName = values.get("userName")[0];
		String password = values.get("password")[0];
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("userName", userName);
		parameters.put("password", password);
		ValidationResponse response = new LoginValidator().validate(parameters);
		if (response.isSuccess()) {
			IUser user = (IUser) response.getResultObj();
			session().put("user", user.getUID());
			session().put("userName", user.getUserName());
			if (user.getRoles().contains(ROLE.ADMINISTRATOR))
				session().put("admin", "true");
			return redirect("/user");
		}
		return ok(main.render(Config.getValue(response.getInvalid_MESSAGE()
				.toString())));
	}

	public static Result register() {
		return ok(register.render(""));
	}

	public static Result registerDone() {

		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String userName = values.get("userName")[0];
		String password = values.get("password")[0];
		String password1 = values.get("password1")[0];
		String email = values.get("email")[0];

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("userName", userName);
		parameters.put("password", password);
		parameters.put("password1", password1);
		parameters.put("email", email);
		ValidationResponse response = new RegistrationValidator()
				.validate(parameters);

		IUser user = (IUser) response.getResultObj();
		if (user != null && user.getUID() != null) {
			session().put("user", user.getUID());
			session().put("userName", user.getUserName());
			if (user.getRoles().contains(ROLE.ADMINISTRATOR))
				session().put("admin", "true");
			IAccount account = AccountFactory.getAccount(user);
			try {
				account.initNewAccount();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return redirect("/user");
		}

		return ok(register.render(Config.getValue(response.getInvalid_MESSAGE()
				.toString())));
	}
}