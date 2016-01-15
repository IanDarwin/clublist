package model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MemberTest {

	@Test
	public void test() {
		EqualsVerifier.forClass(Member.class).verify();
	}

}
