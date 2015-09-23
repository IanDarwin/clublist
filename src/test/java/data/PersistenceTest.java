package data;

import java.util.*;
import javax.persistence.*;
import org.junit.*;
import model.*;
import static org.junit.Assert.*;

public class PersistenceTest {

	private static EntityManagerFactory emf;
	private EntityManager em;

	final static String FIRST = "Loodlie";

	@BeforeClass
	public static void preClass() {
		emf = Persistence.createEntityManagerFactory("clublist");
	}

	@Before
	public void preTest() {
		em = emf.createEntityManager();
	}

	/** Live JPA (fake database): save a Member, then
	 * asser that we find it in the list of Members from JPA
	 */
	@Test
	public void testLoadStore() {
		Member tm = new Member();
		tm.setFirstName(FIRST);
		tm.setLastName("Lozz");

		em.getTransaction().begin();
		em.persist(tm);
		em.getTransaction().commit();

		List<Member> ms = em.createQuery("from Member", Member.class).getResultList();
		assertTrue("found any", ms.size() > 0);

		for (Member m : ms) {
			if (m.getFirstName().equals(FIRST)) {
				return;
			}
		}
		fail("Did not retrieve Member that we saved!");
	}
}
