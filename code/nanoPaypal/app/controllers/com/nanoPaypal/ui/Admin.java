package controllers.com.nanoPaypal.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nanoPaypal.UIApi.helpers.AdminHelper;
import com.nanoPaypal.UIApi.helpers.Search;
import com.nanoPaypal.UIApi.helpers.UserDetails;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin;
import views.html.adminHistory;
import views.html.history;

public class Admin extends Controller {
	public static Result index() {
		return ok(admin.render(""));
	}

	public static Result getHistory() {
		List<String> users = AdminHelper.getAllUsers();
		List<List<Object>> details = UserDetails.getDetails();
		if (details == null) {
			return ok(adminHistory.render(new ArrayList<List<Object>>(),
					new ArrayList<String>(), new ArrayList<String>(),
					"No History Found"));
		}
		List<Object> header1 = details.remove(0);
		List<String> header = new ArrayList<String>();
		for (Object o : header1) {
			header.add(o + "");
		}
		return ok(adminHistory.render(details, header, users, "User History"));
	}

	public static Result getHistoryOfUser() {
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String id = values.get("select")[0];
		String uid = Search.getUid(id);
		List<List<Object>> details = UserDetails.getDetails(uid);
		if (details == null) {
			return ok(history.render(new ArrayList<List<Object>>(),
					new ArrayList<String>(), "User History: " + id));
		}
		List<Object> header1 = details.remove(0);
		List<String> header = new ArrayList<String>();
		for (Object o : header1) {
			header.add(o + "");
		}
		return ok(history.render(details, header, "User History: " + id));
	}
}