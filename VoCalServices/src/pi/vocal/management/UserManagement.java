package pi.vocal.management;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Role;

public class UserManagement {

	public static boolean createUser(PublicUser user, String password) {
		boolean success = true;
		
		Session session = null;
		try {
			SessionFactory sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			
			// TODO hash generation/salt
			User userDto = new User();
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setEmail(user.getEmail());
			userDto.setRole(Role.USER);
			
			session.getTransaction().commit();
		} catch (HibernateException he) {
			success = false;
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
		
		return success;
	}
	
}
