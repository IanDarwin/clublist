package data;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Person;

/**
 * This is a JPA "gateway" data interface using JPA
 * @author Ian Darwin
 */
@Stateless @Named("personHome")
@SessionScoped
public class PersonHome {
	
	private static final String OUTCOME = "PersonList";

	@PersistenceContext
	private EntityManager em;

	private Person instance = new Person();

	public Person getInstance() {
		return instance;
	}
	
	public void wire(Long id) {
		if (id == null) {
			create();
			return;
		}
		instance = em.find(Person.class, id);
	}

	public void create() {
		instance = new Person();
	}

	/** Discard previously-entered values */
	public String cancel() {
		create();
		return OUTCOME;
	}
	/** In JPA an "update"-type method often doesn't have to
	 * do anything except look good, e.g., it sports a
	 * Transaction Attribute that will make the changes
	 * get committed. Unless, of course, the instance is
	 * brand-new
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String update() {
		if (instance.getId() == 0) {
			em.persist(instance);
		}
		return OUTCOME;
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
		switch(instance.getCountry()) {
		case CANADA:
			valid = postCode.matches("\\w\\d\\w \\d\\w\\d");
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
