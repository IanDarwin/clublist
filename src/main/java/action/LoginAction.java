package action;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

import com.darwinsys.security.DigestUtils;

import data.MemberList;
import model.Member;

/**
 * Simple DIY login.
 */
@Named("identity") @SessionScoped
public class LoginAction implements Serializable {

	private static final long serialVersionUID = -1899830327382597511L;

	@Inject
	MemberList userlist;

	String username, password;

	Member loggedInUser = null;
	
	public LoginAction() {
		// Just for debugging...
		System.out.println("Login.Login()");
	}
	
	public String login() {
		System.out.println("Login.login(" + username + ")");
		try {
			loggedInUser = verifyUser(username, password);
			if (loggedInUser == null) {
				throw new IllegalStateException("findByUserNameAndPassword should not return null!");
			}

			// XXX Maybe now tell the Container about it for e.g., logging and session times...
			// using request.login(username, password);
			// Except that they want a login and password to look up in a Realm, which we've already done.

			FacesContext.getCurrentInstance().addMessage( null,
                    new FacesMessage("Logged in as " + loggedInUser.getName()));
			return hasRole("admin") ? "MemberList" : "MemberView?memberId = " + loggedInUser.getId();

		} catch (NoResultException nre) {
			FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Login Failed for user " + username + "; user and/or password invalid."));
            return "Login";
		} catch (RuntimeException ex) {
			Throwable exc, r = ((exc = ex.getCause()) == null ? ex : exc);
			r.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage("Login Failed for user " + username + " due to " + ex));
			return "Login";
		}
	}

	Member verifyUser(String username, String password) {
		return userlist.findByUserNameAndPassword(username, DigestUtils.md5(password));
	}

	public void logout() {
		System.out.println("Logout " + loggedInUser);
		// XXX session.invalidate();
		loggedInUser = null;
	}

	/** Called by JSF */
	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	/** Used by JSF */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/** Important for security */
	public boolean isLoggedIn() {
		return loggedInUser != null;
	}

	public Member getLoggedInUser() {
		return loggedInUser;
	}

	public boolean hasRole(String role) {
		if (role == null) {
			throw new NullPointerException("isUserInRole(null) isn't sensible");
		}
		if (!isLoggedIn()) {
			return false;
		}
		for (String r : loggedInUser.getRoles()) {
			if (r.equals(role)) {
				return true;
			}
		}
		return false;
	}
}
