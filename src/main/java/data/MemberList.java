package data;

import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Named;

import model.Member;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

/**
 * This is a basic DAO-like interface for use by JSF or EJB.
 * Methods are implemented for us by Apache DeltaSpike Data.
 * The methods in the inherited interface suffice for many apps!
 * @author Ian Darwin
 */
@Named("memberList") @Default
@SessionScoped
@Repository 
public interface MemberList extends EntityRepository<Member, Long> {

	@Query(value="select p from Member p order by p.lastName asc")
	List<Member> findAll();

}
