package model;

import org.junit.Test;
import org.junit.Ignore;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MemberTest {

	@Ignore // Delete this line to actually test this
	@Test
	public void test() {
		EqualsVerifier.forClass(Member.class).verify();
	}

}
