package controllers.com.nanoPaypal.ui;

import java.util.List;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.transact;
import views.html.transact_add;
import views.html.transact_delete;
import views.html.transact_sendMoney;

import com.nanoPaypal.UIApi.helpers.Search;
import com.nanoPaypal.configurationManager.Config;
import com.nanoPaypal.logging.ILogger;
import com.nanoPaypal.logging.TransactionLogger;
import com.nanoPaypal.transaction.impl.AccountFactory;
import com.nanoPaypal.transaction.impl.InsufficientBalanceException;
import com.nanoPaypal.transaction.impl.TransactionManager;
import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.specs.IUser;

import controllers.Constants;

public class Transact extends Controller {
	public static Result index() {
		return ok(transact.render(""));
	}

	public static Result addUser() {
		return ok(transact_add.render(""));
	}

	public static Result insertUser() {
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String email = values.get("email")[0];

		String user = Search.searchUser(email);
		if (user != null) {

			if (user.equals(session().get("user")))
				return ok(transact_add.render(Config
						.getValue(Constants.TRANSACTADD_ERROR_sameUser)));

			boolean addedUser = TransactionManager.addUser(session()
					.get("user"), user);
			if (addedUser)
				return ok(transact.render(Config
						.getValue(Constants.TRANSACTADD_SUCCESS_addedUser)));
			else
				return ok(transact_add.render(Config
						.getValue(Constants.TRANSACTADD_ERROR_couldNotAddUser)));
		}
		return ok(transact_add.render(Config
				.getValue(Constants.TRANSACTADD_ERROR_noUser)));
	}

	public static Result deleteUsers() {
		List<String> users = TransactionManager.getUsers(session().get("user"));
		return ok(transact_delete.render(users, ""));
	}

	public static Result deleteUsers_exec() {
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String email = values.get("select")[0];

		String user = Search.searchUser(email);
		if (user != null) {
			boolean deletedUsers = TransactionManager.deleteUsers(session()
					.get("user"), user);
			if (deletedUsers) {
				return ok(transact.render(Config
						.getValue(Constants.TRANSACTDELETE_SUCCESS)));
			} else
				return ok(transact_delete
						.render(TransactionManager.getUsers(session().get(
								"user")),
								Config.getValue(Constants.TRANSACTDELETE_ERROR_couldNotDelete)));
		}
		return ok(transact_delete.render(
				TransactionManager.getUsers(session().get("user")),
				Config.getValue(Constants.TRANSACTADD_ERROR_noUser)));
	}

	public static Result sendMoney() {
		List<String> users = TransactionManager.getUsers(session().get("user"));
		return ok(transact_sendMoney.render(users, ""));
	}

	public static Result sendMoney_exec() {
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		String email = values.get("select")[0];
		String amountS = values.get("amount")[0];

		float amount = 0;
		String uid = session().get("user");
		try {
			amount = Float.parseFloat(amountS);

		} catch (Exception e) {
			List<String> users = TransactionManager.getUsers(uid);
			return ok(transact_sendMoney.render(users,
					Config.getValue(Constants.TRANSACT_DO_invalidAmount)));
		}

		IUser user = null;
		try {
			user = UserFactory.getUser(uid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IUser ruser = null;
		try {
			ruser = UserFactory.getUserFromEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user != null && ruser != null) {

			IAccount userAccount = AccountFactory.getAccount(user);
			IAccount r_userAccount = AccountFactory.getAccount(ruser);
			try {
				String id = userAccount.transferFund(
						r_userAccount.getAccountId(), amount);
				if (id == null) {
					return ok(transact_sendMoney.render(
							TransactionManager.getUsers(uid),
							Config.getValue(Constants.TRANSACT_DO_failure)));
				}

				try {
					r_userAccount.initAccount();
					Object[] params = new Object[]{id, ruser.getUID(),
							user.getContacts().iterator().next().getValue(),
							"C", r_userAccount.getFunds(), amount};
					ILogger iLogger1 = new TransactionLogger(
							Constants.QUERY_logTransaction, params);
					iLogger1.log();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					userAccount.initAccount();
					Object[] params = new Object[]{id, uid, email, "D",
							userAccount.getFunds(), amount};
					ILogger iLogger1 = new TransactionLogger(
							Constants.QUERY_logTransaction, params);
					iLogger1.log();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (InsufficientBalanceException e) {
				return ok(transact_sendMoney
						.render(TransactionManager.getUsers(uid),
								Config.getValue(Constants.TRANSACT_DO_insufficientFund)));
			} catch (Exception e) {
				e.printStackTrace();
				return ok(transact_sendMoney.render(
						TransactionManager.getUsers(uid),
						Config.getValue(Constants.TRANSACT_DO_failure)));
			}

			return ok(transact.render(Config
					.getValue(Constants.TRANSACT_DO_success)));
		} else
			return ok(transact_sendMoney.render(
					TransactionManager.getUsers(uid),
					Config.getValue(Constants.TRANSACTADD_ERROR_noUser)));
	}
}