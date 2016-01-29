package action;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import data.MemberList;
import data.MemberValidator;
import mock.MockDataSourceProducer;
import model.Member;

@RunWith(Arquillian.class)
public class LoginActionTest {
	
	public static final String TEST_PASSWORD = "xecret";
	public static final String TEST_USERNAME = "test";
	public static final String TEST_FIRSTNAME = "Top";

	private static final String WEBAPP_SRC = /*"view";*/"src/main/webapp";
	private static final String RESOURCES_SRC = "src/main/resources";

	@Drone
	private WebDriver driver;

	// These injected WebElements are proxied so the elements don't have to exist until test accesses them
	@FindBy(id="loginForm:userName")
	private WebElement userNameTF;

	@FindBy(id="loginForm:password")
	private WebElement passwordTF;

	@FindBy(id="loginForm:login")
	private WebElement loginButton;
	
	@FindBy(id="menu.loggedInName")
	private WebElement loggedInName;

	@ArquillianResource
	private URL deploymentUrl;
	
//	/** We will test the Login Action but override its JPA-using verifyUser method */
//	@ManagedBean(name="identity") @SessionScoped
//	public static class MockLoginAction extends LoginAction {
//		private static final long serialVersionUID = 1L;
//		public MockLoginAction() {
//			// empty
//		}
//		@Override
//		Member verifyUser(String username, String password) {
//			System.out.println("LoginActionTest.MockLoginAction.verifyUser()");
//			if (TEST_USERNAME.equals(username) &&
//				TEST_PASSWORD.equals(password)) {
//				Member m = new Member(TEST_FIRSTNAME, "User", "nobody@mrmyxyzptylk456789.com");
//				m.setUserName(username);
//				m.setPassword(password);
//				return m;
//			} else {
//				throw new IllegalArgumentException("Invalid username/password");
//			}
//		}
//	}
	
	@Deployment()
    public static WebArchive createDeployment() {
		String DSD = "org.apache.deltaspike.modules:deltaspike-data-module-api:1.3.0";
		String DSAPI = "com.darwinsys:darwinsys-api:1.0.5";
		String MOCKITO = "org.powermock:powermock-api-mockito:1.6.2";
		File[] files = Maven.resolver().resolve(DSD, MOCKITO, DSAPI).withTransitivity().asFile();
		final WebArchive archive = ShrinkWrap.create(WebArchive.class, "clubtesting.war")
			.addPackage(LoginAction.class.getPackage())				// action
            .addPackage(Member.class.getPackage())					// model
            .addPackage(MockDataSourceProducer.class.getPackage())	// mock
            .addPackage(WebDriver.class.getPackage())
            .addClasses(
            		// NOT MemberHome.class, - exclude for now
            		MemberList.class,
            		MemberValidator.class
//            		MockLoginAction.class
            		)
            .addAsLibraries(files)
            .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
            		.importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                    	"/", Filters.include(".*\\.xhtml$"))
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            // Yes, this is src/main/resources, not src/test/resources
            .addAsResource(new File(RESOURCES_SRC + "/META-INF/persistence.xml"), "META-INF/persistence.xml")
            // Mistakes such as missing leading "/" may cause silent failures
            .addAsResource(new File(RESOURCES_SRC + "/config-sample.properties"), "config.properties")
            .addAsWebInfResource(new File("src/test/web.xml"), "WEB-INF/web.xml")
            .addAsWebInfResource(
                new StringAsset("<faces-config version=\"2.1\"/>"),
                "faces-config.xml")
            	;
		return archive;
    }

	@RunAsClient
	@Test
	public void testLoginSuccessfully() {
		final String target = deploymentUrl.toExternalForm() + "login.jsf";
		System.out.println("LoginActionTest.testLoginSuccessfully(): URL = " + target);
		driver.get(target);
		System.out.println(driver.getPageSource());
		userNameTF.sendKeys(TEST_USERNAME);
		passwordTF.sendKeys(TEST_PASSWORD);
		guardHttp(loginButton).click();
		System.out.println(driver.getPageSource());
		// assertEquals(TEST_FIRSTNAME, loggedInName.getText().trim());
		assertTrue(driver.getPageSource().contains("Welcome " + TEST_FIRSTNAME));
	}


	// @Test
	public void testLogout() {
		// XXX fail("Not yet implemented");
	}
}
