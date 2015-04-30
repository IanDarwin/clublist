package model;

public enum Country {
	CA("Canada"),
	US("USA"),
	ZZ("Other");
	
	final String longName;
	private Country(final String longName) {
		this.longName = longName;
	}
	public String getCountryName() {
		return longName;
	}
}
