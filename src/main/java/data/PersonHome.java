package data;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.ejb.Remove;
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

import model.Country;
import model.Person;

/**
 * Implements Gateway, an Adam Bien pattern whose purpose is to expose
 * an Entity (and its relations) to the Client/Web tier, rather like a Seam2 "Home Object"
 * @author Ian Darwin
 */
@Stateful@Named@ConversationScoped
public class PersonHome implements Serializable {

	private static final long serialVersionUID = -2284578724132631798L;

	@Inject Conversation conv;

	// Must be Long (not long) so we can check for null
	private Long id;
	private Person instance = new Person();

	@PersistenceContext(type=PersistenceContextType.EXTENDED) EntityManager em;

	public PersonHome() {
		System.out.println("MemberHome.MemberHome()");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void wire() {
		conv.begin();
		System.out.println("Wire(): " + id);
		if (id == null) {
			throw new IllegalStateException("Wire: No ID");
		}
		instance = em.find(Person.class, id);
		if (instance == null) {
			System.err.println("Person not found by id! " + id);
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
		return "PersonList.web";
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String save() {
		System.out.println("MemberHome.save()");
		em.persist(instance);
		return "PersonList.web";
	}

	public void newInstance() {
		System.out.println("MemberHome.newInstance()");
		instance = new Person();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		System.out.println("MemberHome.setId(" + id + ")");
		this.id = id;
	}
	public Person getInstance() {
		System.out.println("MemberHome.getInstance(): " + instance);
		return instance;
	}
	public void setInstance(Person instance) {
		this.instance = instance;
	}

	@Remove
	public void remove() {
		System.out.println("MemberHome.remove()");
		conv.end();
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
		String postCode = (String) userInputValue, message = "??";
		final Country country = instance.getCountry();
		switch(country) {
		case CANADA:
			valid = postCode.matches("\\w\\d\\w ?\\d\\w\\d");
			message = "PostCode must match ANA NAN pattern";
			break;
		case USA:
			valid = postCode.matches("\\d{5}(-\\d{4})?");
			message = "Zip Code must be 5 digits, optional -4 digits";
		case OTHER:
			break;
		default:
			throw new IllegalStateException("Unknown country");
		}
		if (!valid) {
			jsfContext.addMessage(
					toValidate.getClientId(jsfContext), 
					new FacesMessage(message));
	        ((UIInput) toValidate).setValid(false);
	        return;
		}
	}
}
