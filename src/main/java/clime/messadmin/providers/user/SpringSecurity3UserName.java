/**
 */
package clime.messadmin.providers.user;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clime.messadmin.providers.spi.RequestLifeCycleProvider;

/**
 * Fetch the current user name from Spring Security 3, if available.
 *
 * Implementation note: Spring Security stores its data in a ThreadLocal object, so we need to sniff it at request time.
 * Copy (cache) the information as RemoteUser at request time. It will be picked by the HttpRequestRemoteUser plugin.
 *
 * @author C&eacute;drik LIME
 * @since 4.2
 */
public class SpringSecurity3UserName extends AcegiUserName implements RequestLifeCycleProvider {

	/**
	 */
	public SpringSecurity3UserName() {
		super();
	}

	@Override
	protected String getSecurityContextHolderClassName() {
		return "org.springframework.security.core.context.SecurityContextHolder";//$NON-NLS-1$
	}
	@Override
	protected String getSecurityContextClassName() {
		return "org.springframework.security.core.context.SecurityContext";//$NON-NLS-1$
	}
	@Override
	protected String getAuthenticationClassName() {
		return "org.springframework.security.core.Authentication";//$NON-NLS-1$
	}
	@Override
	protected String getUserDetailsClassName() {
		return "org.springframework.security.core.userdetails.UserDetails";//$NON-NLS-1$
	}

	/**
	 * @see clime.messadmin.providers.spi.BaseProvider#getPriority()
	 */
	@Override
	public int getPriority() {
		return 48;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestInitialized(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
		/*
		Object obj = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (obj instanceof org.springframework.security.core.userdetails.UserDetails) {
			String username = ((org.springframework.security.core.userdetails.UserDetails) obj).getUsername();
		} else {
			String username = obj.toString();
		}
		 */
		super.requestInitialized(request, response, servletContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestDestroyed(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
		super.requestDestroyed(request, response, servletContext);
	}
}
