import com.nanoPaypal.transaction.impl.DBAccount;
import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.impl.DBUser;

public class Test {
	public static void main(String[] args) throws Exception {
		DBUser dbUser1 = new DBUser("vivek", "vivek");
		DBUser dbUser2 = new DBUser("vivek1", "vivek");
		IAccount account = new DBAccount(dbUser1);
		IAccount account2 = new DBAccount(dbUser2);
		account.transferFund(account2.getAccountId(), 10);
	}
}
