package clime.messadmin.admin;

import java.io.Serializable;
import java.util.Comparator;

import javax.servlet.http.HttpSession;

/**
 * Comparator which permits to compare on a session's content
 * @author C&eacute;drik LIME
 */
public abstract class BaseSessionComparator implements Comparator, Serializable {

	/**
	 * 
	 */
	public BaseSessionComparator() {
		super();
	}

	public abstract Comparable getComparableObject(HttpSession session);

	/** {@inheritDoc} */
	public final int compare(Object o1, Object o2) {
		HttpSession s1 = (HttpSession) o1;
		HttpSession s2 = (HttpSession) o2;
		Comparable c1 = getComparableObject(s1);
		Comparable c2 = getComparableObject(s2);
		return c1==null ? (c2==null ? 0 : -1) : (c2==null ? 1 : c1.compareTo(c2));
	}
}
