package action;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import data.MemberList;
import data.MemberValidator;
import model.Member;
import mock.MockMemberListProducer;

@RunWith(Arquillian.class)
public class LoginActionTest {
	
	public static final String TEST_USERNAME = "test";
	public static final String TEST_PASSWORD = "xecret";
	public static final String TEST_FIRSTNAME = "Tapfoot";

	@Drone
	private WebDriver driver;

	// These injected WebElements are proxied, so elements don't have to exist until test accesses them
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
	
	@Deployment()
    public static WebArchive createDeployment() {
		String DSD = "org.apache.deltaspike.modules:deltaspike-data-module-api:1.3.0";
		String DSAPI = "com.darwinsys:darwinsys-api:1.0.5";
		String MOCKITO = "org.powermock:powermock-api-mockito:1.6.2";
		File[] files = Maven.resolver().resolve(DSD, MOCKITO, DSAPI).withTransitivity().asFile();
		final WebArchive archive = ShrinkWrap.create(WebArchive.class, "clubtesting.war")
			.addPackage(LoginAction.class.getPackage())				// action
            .addPackage(Member.class.getPackage())					// model
            .addPackage(MockMemberListProducer.class.getPackage())	// mock
            .addPackage(WebDriver.class.getPackage())
            .addClasses(
            		// MemberHome.class, // exclude
            		MemberList.class,
            		MemberValidator.class
            		)
            .addAsLibraries(files)
            .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
            		.importDirectory("src/main/webapp").as(GenericArchive.class),
                    	"/", Filters.include(".*\\.xhtml$"))
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            // Yes, this is src/main/resources, not src/test/resources
            .addAsResource(new File("src/main/resources" + "/META-INF/persistence.xml"), "META-INF/persistence.xml")
            // Mistakes such as missing leading "/" may cause silent failures
            .addAsResource(new File("src/main/resources" + "/config-sample.properties"), "config.properties")
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
		//System.out.println(driver.getPageSource());
		userNameTF.sendKeys(TEST_USERNAME);
		passwordTF.sendKeys(TEST_PASSWORD);
		guardHttp(loginButton).click();
		//System.out.println(driver.getPageSource());
		assertTrue(driver.getPageSource().contains("Welcome " + TEST_FIRSTNAME));
	}


	// @Test
	public void testLogout() {
		// XXX fail("Not yet implemented");
	}
}
