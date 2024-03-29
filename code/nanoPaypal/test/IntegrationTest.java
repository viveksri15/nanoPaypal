import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.BeforeClass;

import play.libs.F.Callback;
import play.test.TestBrowser;

public class IntegrationTest {

	@BeforeClass
	public static void testSetup() {
		System.out.println("Ready");
	}

	@org.junit.Test
	public void test() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						browser.goTo("http://localhost:3333");
						assertThat(browser.pageSource()).contains("Welcome");
					}
				});
	}
}
