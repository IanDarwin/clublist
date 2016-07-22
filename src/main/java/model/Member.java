package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Member - represents one person in our database.
 * @author Ian Darwin
 */
@Entity
public class Member {
	
	private long id;
	protected String userName, password;
	protected String firstName, lastName;
	protected String homePhone, cellPhone;
	protected String email;
	protected String address, address2;
	protected String city, province, postCode;
	protected Country country = Country.CA;
	/** If they have an executive position */
	protected String position;
	protected Date expiryDate;
	protected List<String> roles = new ArrayList<>();
	
	/** 
	 * "A very public Person"
	 */
	public Member() {
		
	}

	/**
	 * This constructor is likely only useful in testing
	 * @param firstName First Name
	 * @param lastName Last Name
	 * @param email Email address
	 */
	public Member(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/** Accessor for the password. May be hashed or not; the semantics
	 * are provided by the external control implementation, not coded here.
	 * @return The password string
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Transient
	public String getName() {
		return getFirstName() + ' ' + getLastName();
	}

	public String getHomePhone() {
		return homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getCellPhone() {
		return cellPhone;
	}
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	@Enumerated(EnumType.STRING)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	// For app-managed security

	@ElementCollection
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/**
	 * Canadian Post Codes can be entered in either case
	 * and with or without the middle space; this
	 * canonicalizes them to upper case and with the space.
	 */
	@PrePersist @PreUpdate
	public void fixupPostCode() {
		if (getPostCode() == null) {
			return;
		}
		System.out.println("Member.fixupPostCode()");
		if (country == Country.CA) {
			String ananan = getPostCode().toUpperCase();
			switch(ananan.length()) {
			case 7: // OK
				break;
			case 6: // Missing internal space
				postCode = 
					ananan.substring(0,3) + " " +
					ananan.substring(3);
				break;
			default:
				throw new IllegalStateException(
					postCode + " not valid Canadian postcode");
			}
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}

