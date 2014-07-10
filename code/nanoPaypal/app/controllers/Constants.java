package controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.nanoPaypal.user.impl.ROLE;

public class Constants {
	public static Map<String, Set<ROLE>> roles = null;
	static {
		roles = new HashMap<String, Set<ROLE>>();
		Set<ROLE> set = new HashSet<ROLE>();
		set.add(ROLE.LOGGEDIN_USER);
		roles.put("user", set);

		set = new HashSet<ROLE>();
		set.add(ROLE.ADMINISTRATOR);
		roles.put("admin", set);

		set = new HashSet<ROLE>();
		set.add(ROLE.TRANSACTION_USER);
		roles.put("transact", set);
	}

	public static final String TRANSACTADD_ERROR_couldNotAddUser = "TRANSACTADD_ERROR_couldNotAddUser";
	public static final String TRANSACTADD_SUCCESS_addedUser = "TRANSACTADD_SUCCESS_addedUser";
	public static final String TRANSACTADD_ERROR_noUser = "TRANSACTADD_ERROR_noUser";
	public static final String TRANSACTDELETE_SUCCESS = "TRANSACTDELETE_SUCCESS";
	public static final String TRANSACTDELETE_ERROR_couldNotDelete = "TRANSACTDELETE_ERROR_couldNotDelete";
	public static final String TRANSACT_DO_invalidAmount = "TRANSACT_DO_invalidAmount";
	public static final String TRANSACT_DO_failure = "TRANSACT_DO_failure";
	public static final String TRANSACT_DO_insufficientFund = "TRANSACT_DO_insufficientFund";
	public static final String TRANSACT_DO_success = "TRANSACT_DO_success";
	public static final String TRANSACTADD_ERROR_sameUser = "TRANSACTADD_ERROR_sameUser";
	public static final String QUERY_logTransaction = "insert into transactionRecord (transactionId, uid, otherUser, type, balance, amount) values (?,?,?,?,?, ?)";
	public static final String HISTORY_NORESULT = "HISTORY_NORESULT";
	public static final String PASSWORD_FAILED = "PASSWORD_FAILED";
}