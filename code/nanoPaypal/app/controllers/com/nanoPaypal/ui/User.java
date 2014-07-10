package controllers.com.nanoPaypal.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.history;
import views.html.user;

import com.nanoPaypal.UIApi.helpers.UserDetails;
import com.nanoPaypal.configurationManager.Config;
import com.nanoPaypal.transaction.impl.AccountFactory;
import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.specs.IUser;

import controllers.com.nanoPaypal.validator.PasswordChangeValidator;
import controllers.com.nanoPaypal.validator.ValidationResponse;

public class User extends Controller {
	public static Result index() {
		String uid = session().get("user");
		try {
			IUser iUser = UserFactory.getUser(uid);
			IAccount account = AccountFactory.getAccount(iUser);
			DecimalFormat df = new DecimalFormat("0.00##");
			String result = df.format(account.getFunds());
			return ok(user.render(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(user.render("0"));
	}

	public static Result getHistory() {
		String uid = session().get("user");
		System.out.println("uid=" + uid);
		List<List<Object>> details = UserDetails.getDetails(uid);
		if (details == null) {
			return ok(history.render(new ArrayList<List<Object>>(),
					new ArrayList<String>(), "Your History"));
		}
		List<Object> header1 = details.remove(0);
		List<String> header = new ArrayList<String>();
		for (Object o : header1) {
			header.add(o + "");
		}
		return ok(history.render(details, header, "Your History"));
	}

	public static Result changePassword() {
		return ok(views.html.changePassword.render(""));
	}

	public static Result changePassword_exec() {

		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String currentPassword = values.get("currentPassword")[0];
		String newPassword1 = values.get("newPassword1")[0];
		String newPassword2 = values.get("newPassword2")[0];

		PasswordChangeValidator changeValidator = new PasswordChangeValidator();
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("userName", session().get("userName"));
		parameters.put("currentPassword", currentPassword);
		parameters.put("password", newPassword1);
		parameters.put("password1", newPassword2);

		System.out.println(parameters);

		ValidationResponse validationResponse = changeValidator
				.validate(parameters);
		if (validationResponse != null)
			return ok(views.html.changePassword.render(Config
					.getValue(validationResponse.getInvalid_MESSAGE()
							.toString())));
		else
			return ok(views.html.changePassword.render(Config
					.getValue(controllers.Constants.PASSWORD_FAILED)));
	}

	public static Result forgotPassword() {
		return ok(views.html.forgotPassword.render());
	}

	public static Result forgotPassword_exec() {
		return ok(new Html(
				"Please click on the link you get on your registered mail Id.<br/><a href=\"/\">Go Back</a>"));
	}
}