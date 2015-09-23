package outreach;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;

import model.Member;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import data.MemberList;

public class MailManTest {

	List<Member> fakeList = Arrays.asList(
			// XXX Grab the Sender email from the config.props and use that!
			new Member("Tom", "Jones", "devnull@ojwkjsflkjew.com")
	);
	private MailMan sender;
	
	@Before
	public void init() {
		MemberList lister = mock(MemberList.class);
		when(lister.findAll()).thenReturn(fakeList);
		sender = new MailMan();
		sender.lister = lister;
	}
	
	@Test @Ignore // Need to refactor to mock the Mail Session and Transport!
	public void test() throws Exception {
		sender.subject = "This is a test message";
		sender.messageBody = "This is a test message body";
		sender.sendMessageToAll();
	}

}
