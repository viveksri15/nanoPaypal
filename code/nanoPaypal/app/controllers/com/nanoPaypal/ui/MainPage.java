package controllers.com.nanoPaypal.ui;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.main;

public class MainPage extends Controller {
	public static Result index() {
		return ok(main.render(""));
	}
}
