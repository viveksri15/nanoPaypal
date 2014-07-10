import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.nanoPaypal.logging.ErrorLoggerFactory;
import com.nanoPaypal.logging.FileLogger;

import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import views.html.error;
import views.html.nopermission;
import controllers.com.nanoPaypal.validator.RoleValidator;
import controllers.com.nanoPaypal.validator.ValidationResponse;

public class Global extends GlobalSettings {

	@Override
	public Promise<Result> onError(RequestHeader requestHeader,
			Throwable throwable) {

		StringBuffer errorString = new StringBuffer();

		for (StackTraceElement element : throwable.getStackTrace()) {
			errorString.append(element.getFileName()).append(" ")
					.append(element.getLineNumber()).append("\t");
		}

		FileLogger logger = ErrorLoggerFactory.getInstance();

		logger.addLog("ERROR").addLog(errorString)
				.addLog(requestHeader.queryString())
				.addLog(requestHeader.path()).writeInfile();;

		return Promise
				.<Result> pure(Results.internalServerError(error.render()));
	}

	@Override
	public Promise<Result> onBadRequest(RequestHeader requestHeader, String path) {

		FileLogger logger = ErrorLoggerFactory.getInstance();

		logger.addLog("BAD_REQUEST").addLog(requestHeader.queryString())
				.addLog(requestHeader.path()).writeInfile();;

		return Promise
				.<Result> pure(Results.internalServerError(error.render()));
	}

	@Override
	public Promise<Result> onHandlerNotFound(RequestHeader requestHeader) {

		FileLogger logger = ErrorLoggerFactory.getInstance();

		logger.addLog("404_REQUEST").addLog(requestHeader.queryString())
				.addLog(requestHeader.path()).writeInfile();;

		return Promise
				.<Result> pure(Results.internalServerError(error.render()));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Action onRequest(Request request, Method method) {
		String path = request.path();
		if (path != null) {
			String[] split = path.split("/");
			Set<String> pagesToCheck = new HashSet<String>();
			pagesToCheck.add("user");
			pagesToCheck.add("admin");
			pagesToCheck.add("transact");
			if (split.length > 0 && pagesToCheck.contains(split[1])) {

				final String page = split[1];
				return new Action.Simple() {
					@Override
					public Promise<Result> call(Context ctx) throws Throwable {
						String uid = ctx.session().get("user");
						boolean invalidUser = false;
						if (uid != null) {
							RoleValidator roleValidator = new RoleValidator();
							Map<String, String> parameters = new HashMap<String, String>();
							parameters.put("uid", uid);
							parameters.put("page", page);
							ValidationResponse validate = roleValidator
									.validate(parameters);
							if (validate == null || !validate.isSuccess()) {
								invalidUser = true;
							}
						} else
							invalidUser = true;

						if (invalidUser) {
							return Promise.<Result> pure(Results
									.ok(nopermission.render()));
						}

						return delegate.call(ctx);
					}
				};
			} else if (split.length == 0
					|| (split.length > 0 && (split[1].equals("")
							|| split[1].equals("Login")
							|| split[1].equals("Register") || split[1]
								.equals("RegisterDone")))) {
				return new Action.Simple() {
					@Override
					public Promise<Result> call(Context ctx) throws Throwable {
						String uid = ctx.session().get("user");
						System.out.println(ctx.session());
						if (uid != null) {
							return Promise.<Result> pure(Results
									.redirect("/user"));
						}
						return delegate.call(ctx);
					}
				};
			}
		}
		return super.onRequest(request, method);
	}
}
