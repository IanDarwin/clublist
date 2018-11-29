package data;

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.darwinsys.lang.StringHolder;

@RunWith(Parameterized.class)
public class MemberValidatorEmailTest {
	
	String email;
	boolean expected;
	StringHolder messageHolder = new StringHolder();
	
	public MemberValidatorEmailTest(boolean expected, String email) {
		this.email = email;
		this.expected = expected;
	}
	
	@Parameters
	public static List<Object[]> getParams() {
		return Arrays.asList(new Object[][]{
				// Null or Empty are valid for us, might not be for you.
				{true, null },
				{true, "" },
				// From short to long, roughly
				{true, "a@b.co"},
				{false, "a at b.co" },
				{true, "a.b.c@d.e.f.com"},
				{true, "ketchup@mcdonalds.infomaterial"},
				{false, "a-b@c.com"},			
		});
	}	
	
	@Test
	public void test() {
		System.out.println("Testing: " + email);
		assertSame(expected, MemberValidator.validateEmail(email, messageHolder));
	}

}
