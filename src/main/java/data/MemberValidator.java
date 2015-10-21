package data;

import java.io.Serializable;

import model.Country;

import org.omg.CORBA.StringHolder;

/**
 * Raw validators, not tied to JSF or anything.
 * @author Ian Darwin
 */
public class MemberValidator implements Serializable {

	private static final long serialVersionUID = -12845872132631791L;

	
	/** Validate that an email address is well-formed (it may or may not be deliverable).
	 * @param email The email address to be validated
	 * @param messageHolder The response holder
	 * @return True if the message is not malformed.
	 */
	public static boolean validateEmail(String email, StringHolder messageHolder) {
		boolean valid = true;
		// We allow validation to pass for empty emails; you may not want this
		if (email == null || email.length() == 0) 
			return valid;
		messageHolder.value = "Invalid email syntax, I think";
		return email.matches("[\\w.]+@[\\w.]+\\.\\w+");
	}

	/** 
	 * Validate a Post Code
	 * @param country The country
	 * @param postCode The post code
	 * @param invalidMessage A holder for a message if invalid
	 * @return The syntactic validity of the given code
	 */
	public static boolean validatePostCode(Country country, String postCode, StringHolder invalidMessage) {
		boolean valid = true;
		switch(country) {
		case CA:
			valid = postCode.matches("\\w\\d\\w ?\\d\\w\\d");
			invalidMessage.value = "PostCode must match ANA NAN pattern";
			break;
		case US:
			valid = postCode.matches("\\d{5}(-\\d{4})?");
			invalidMessage.value = "Zip Code must be 5 digits, optional -4 digits";
			break;
		case ZZ:
			break;
		default:
			throw new IllegalArgumentException("Unknown country");
		}
		return valid;
	}
}
