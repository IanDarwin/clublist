package model;

import org.junit.Ignore;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MemberTest {

	@Ignore // Delete this line to actually test this
	@Test
	public void test() {
		EqualsVerifier.forClass(Member.class).verify();
	}

}
