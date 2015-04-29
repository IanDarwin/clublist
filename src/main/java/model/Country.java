package model;

public enum Country {
	CANADA("CA"),
	USA("US"),
	OTHER("");
	final String isoName;
	private Country(final String isoName) {
		this.isoName = isoName;
	}
}
