package data;

import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Named;

import model.Person;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

/**
 * This is a basic DAO-like interface for use by JSF or EJB.
 * Methods are implemented for us by Apache DeltaSpike Data.
 * The methods in the inherited interface suffice for many apps!
 * @author Ian Darwin
 */
@Named("personList") @Default
@SessionScoped
@Repository 
public interface PersonList extends EntityRepository<Person, Long> {

	@Query(value="select p from Person p order by p.lastName asc")
	List<Person> findAll();

}
