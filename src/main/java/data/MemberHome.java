package data;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.omg.CORBA.StringHolder;

import model.Country;
import model.Member;

/**
 * Implements Gateway, an Adam Bien pattern whose purpose is to expose
 * an Entity (and its relations) to the Client/Web tier, rather like a Seam2 "Home Object"
 * @author Ian Darwin
 */
@Stateful @Named @ConversationScoped
public class MemberHome implements Serializable {

	private static final long serialVersionUID = -2284578724132631798L;

	private static final String LIST_PAGE = "MemberList";
	private static final String FORCE_REDIRECT = "?faces-redirect=true";

	@Inject Conversation conv;

	// Must be Long (not long) so we can check for null
	private Long id;
	private Member instance = new Member();

	@PersistenceContext(type=PersistenceContextType.EXTENDED) EntityManager em;

	public MemberHome() {
		System.out.println("MemberHome.MemberHome()");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void wire() {
		if (conv.isTransient()) {
			conv.begin();
		}
		System.out.println("Wire(): " + id);
		if (id == null) {
			instance = new Member();
			return;
		}
		instance = em.find(Member.class, id);
		if (instance == null) {
			throw new IllegalArgumentException("Member not found by id! " + id);
		}
	}
	public void wire(Long id) {
		System.out.println("MemberHome.wire(" + id + ")");
		setId(id);
		wire();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String update() {
		System.out.println("MemberHome.update()");
		em.merge(instance);
		return LIST_PAGE + FORCE_REDIRECT;
	}
	
	/** The C of CRUD - create a new T in the database */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String save() {
		System.out.println("MemberHome.save()");
		em.persist(instance);
		conv.end();
		return LIST_PAGE + FORCE_REDIRECT;
	}

	public void newInstance() {
		System.out.println("MemberHome.newInstance()");
		instance = new Member();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		System.out.println("MemberHome.setId(" + id + ")");
		this.id = id;
	}
	
	public Member getInstance() {
		System.out.println("MemberHome.getInstance(): " + instance);
		return instance;
	}
	public void setInstance(Member instance) {
		this.instance = instance;
		// this.id = instance.getId();
	}
	
	/** Close an editing operation: just end conversation, return List page.
	 * @return The List Page
	 */
	public String cancel() {
		conv.end();
		return LIST_PAGE + FORCE_REDIRECT;
	}
	
	/** Like Cancel but for e.g., View page, no conv end.
	 * @return The List Page
	 */
	public String done() {
		return LIST_PAGE + FORCE_REDIRECT;
	}

	/** The D of CRUD - delete an Entity. Use with care! */
	public String remove() {
		System.out.println("MemberHome.remove()");
		em.remove(instance);
		conv.end();
		return LIST_PAGE + FORCE_REDIRECT;
	}

	@PreDestroy
	public void bfn() {
		System.out.println("MemberHome.bfn()");
	}

	/**
	 * Simple validation of a method in a "home"-type object
	 * that can validate based on related state
	 */
	public void validatePostCode(FacesContext jsfContext,
            UIComponent toValidate, 
            Object userInputValue) {
		
		boolean valid = true;
		String postCode = (String) userInputValue;
		Country country = instance.getCountry();
		if (country == null) {
			country = Country.CA;
		}
		StringHolder messageHolder = new StringHolder();
		valid = validatePostCode(country, postCode, messageHolder);
		if (!valid) {
			jsfContext.addMessage(
					toValidate.getClientId(jsfContext), 
					new FacesMessage(messageHolder.value));
	        ((UIInput) toValidate).setValid(false);
	        return;
		}
	}

	/** 
	 * Method is NOT API and is only public for testing
	 * @param country The country
	 * @param postCode The post code
	 * @param invalidMessage A holder for a message if invalid
	 * @return The syntactic validity of the given code
	 */
	public boolean validatePostCode(Country country, String postCode, StringHolder invalidMessage) {
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
			throw new IllegalStateException("Unknown country");
		}
		return valid;
	}
}
