package pi.vocal.bootstrap;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pi.vocal.persistence.HibernateUtil;

/**
 * Servlet class used to initialize Hibernate connection at server startup.
 * 
 * @author s3ppl
 * 
 */
public class BootStrapServlet implements Servlet {
	private ServletConfig config;

	@Override
	public void destroy() {
		this.config = null;
	}

	@Override
	public ServletConfig getServletConfig() {
		return config;
	}

	@Override
	public String getServletInfo() {
		return "This servlet only starts up the internal database and has no further usage.";
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		HibernateUtil.getSessionFactory();
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		// do nothing
	}

}
