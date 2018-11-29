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

	@PersistenceContext(type=PersistenceContextType.EXTENDED) EntityManager em;

	private static final String LIST_PAGE = "MemberList";
	private static final String FORCE_REDIRECT = "?faces-redirect=true";

	@Inject Conversation conv;

	// Must be Long (not long) so we can check for null
	private Long id;
	private Member instance = newInstance();

	public Member newInstance() {
		System.out.println("MemberHome.newInstance()");
		return new Member();
	}

	public MemberHome() {
		System.out.println("MemberHome.MemberHome()");
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		System.out.println("MemberHome.setId(" + id + ")");
		this.id = id;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void wire() {
		System.out.println("Wire(): " + id);
		if (conv.isTransient()) {
			conv.begin();
		}
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

	/** The C of CRUD - create a new T in the database */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String save() {
		System.out.println("MemberHome.save()");
		em.persist(instance);
		conv.end();
		return LIST_PAGE + FORCE_REDIRECT;
	}
	
	/** The R of CRUD - Download a T by primary key
	 * @param id The primary key of the entity to find
	 * @return The found entity
	 */
	public Member find(long id) {		
		return em.find(Member.class, id);
	}

	/** The U of CRUD - update an Entity
	 * @param entity The entity to update
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String update() {
		System.out.println("MemberHome.update()");
		em.merge(instance);
		return LIST_PAGE + FORCE_REDIRECT;
	}

	/** The D of CRUD - delete an Entity. Use with care! */
	public String remove() {
		System.out.println("MemberHome.remove()");
		em.remove(instance);
		conv.end();
		return LIST_PAGE + FORCE_REDIRECT;
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

	@PreDestroy
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void bfn() {
		conv.end();
		System.out.println("MemberHome.bfn()");
	}

	/** Validate the email in a Member object; JSF-specific */
	public void validateEmail(FacesContext jsfContext,
            UIComponent toValidate, 
            Object userInputValue) {
		
		boolean valid = true;
		String email = (String) userInputValue;
		StringHolder messageHolder = new StringHolder();
		valid = MemberValidator.validateEmail(email, messageHolder);
		if (!valid) {
			jsfContext.addMessage(
					toValidate.getClientId(jsfContext), 
					new FacesMessage(messageHolder.value));
	        ((UIInput) toValidate).setValid(false);
	        return;
		}
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
		valid = MemberValidator.validatePostCode(country, postCode, messageHolder);
		if (!valid) {
			jsfContext.addMessage(
					toValidate.getClientId(jsfContext), 
					new FacesMessage(messageHolder.value));
	        ((UIInput) toValidate).setValid(false);
	        return;
		}
	}
}
