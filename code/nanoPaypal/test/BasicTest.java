import java.util.ArrayList;
import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import play.twirl.api.Html;
import views.html.admin;
import views.html.adminHistory;
import views.html.changePassword;
import views.html.main;

import com.nanoPaypal.transaction.impl.AccountFactory;
import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.impl.ROLE;
import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.specs.IUser;

public class BasicTest {
	@BeforeClass
	public static void testSetup() {
		System.out.println("Ready");
	}

	@Test
	public void testAdminLogin() {

		//is there an admin
		try {
			//ensures user and role table is file
			IUser user = UserFactory.getUserFromUserName("admin");
			Assertions.assertThat((user != null));

			//ensures account table is fine
			IAccount account = AccountFactory.getAccount(user);
			Assertions.assertThat((account != null && account.getFunds() >= 0));

			//ensure admin has Administrator access
			Assertions.assertThat(user.getRoles().contains(ROLE.ADMINISTRATOR));
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionError("Admin Test Failure");
		}
	}

	@Test
	public void testPages() {
		try {
			{
				Html html = admin.render("");
				Assertions.assertThat(html.contentType())
						.isEqualTo("text/html");
			}
			{
				Html html = adminHistory.render(new ArrayList<List<Object>>(),
						new ArrayList<String>(), new ArrayList<String>(), "");
				Assertions.assertThat(html.contentType())
						.isEqualTo("text/html");
			}
			{
				Html html = changePassword.render("");
				Assertions.assertThat(html.contentType())
						.isEqualTo("text/html");
			}
			{
				Html html = main.render("");
				Assertions.assertThat(html.contentType())
						.isEqualTo("text/html");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionError("No admin defined");
		}
	}
}
