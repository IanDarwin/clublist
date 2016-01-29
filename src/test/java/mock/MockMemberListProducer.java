package mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import com.darwinsys.security.DigestUtils;

import action.LoginActionTest;
import data.MemberList;
import model.Member;

@Default
public class MockMemberListProducer {
	
	static {
		System.out.println("MockMemberListProducer.<clinit>()");
	}
	
	@Produces
	public MemberList get() {
		final MemberList mock = mock(MemberList.class);
		when(mock.findByUserNameAndPassword(
				LoginActionTest.TEST_USERNAME, 
				DigestUtils.md5(LoginActionTest.TEST_PASSWORD))).thenReturn(fakeMember());
		return mock;
	}
	
	Member fakeMember() {
		Member m = new Member(LoginActionTest.TEST_FIRSTNAME, "User", "nobody@mrmyxyzptylk456789.com");
		m.setUserName(LoginActionTest.TEST_USERNAME);
		m.setPassword(LoginActionTest.TEST_PASSWORD);
		return m;
	}

}
