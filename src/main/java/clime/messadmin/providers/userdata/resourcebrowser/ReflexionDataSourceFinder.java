/**
 *
 */
package clime.messadmin.providers.userdata.resourcebrowser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

/**
 * Helper class, help find a {@link DataSource}-like class
 * (e.g. org.apache.commons.dbcp.BasicDataSource / org.apache.tomcat.dbcp.dbcp.BasicDataSource)
 * @author C&eacute;drik LIME
 */
class ReflexionDataSourceFinder {
	public static class DataSourceConfiguration {
		public String driverClassName;
		public String url;
		public String userName;
		public boolean closed;
		public int numActive, maxActive;
		public int numIdle, minIdle, maxIdle;
		/** {@inheritDoc} */
		@Override
		public String toString() {
			StringBuilder desc = new StringBuilder(256);
			desc.append("DataSource[");
			if (closed) {
				desc.append("closed, ");
			}
			desc.append("active=").append(numActive).append('/').append(maxActive).append(", ");
			desc.append("idle=").append(numIdle).append("(min=").append(minIdle).append(",max=").append(maxIdle).append("), ");
			desc.append("jdbcUrl=").append(url).append(", ");
			desc.append("userName=").append(userName).append(", ");
			desc.append("jdbcDriverClass=").append(driverClassName);
			desc.append(']');
			return desc.toString();
		}
	}

	private static final String getDriverClassName = "getDriverClassName";//$NON-NLS-1$
	private static final String getUrl       = "getUrl";//$NON-NLS-1$
	private static final String getUsername  = "getUsername";//$NON-NLS-1$
	private static final String isClosed     = "isClosed";//$NON-NLS-1$
	private static final String getNumActive = "getNumActive";//$NON-NLS-1$
	private static final String getMaxActive = "getMaxActive";//$NON-NLS-1$
	private static final String getMinIdle   = "getMinIdle";//$NON-NLS-1$
	private static final String getNumIdle   = "getNumIdle";//$NON-NLS-1$
	private static final String getMaxIdle   = "getMaxIdle";//$NON-NLS-1$

	private ReflexionDataSourceFinder() {
		throw new AssertionError();
	}

	public static DataSourceConfiguration getDataSourceConfiguration(Object obj) {
		if (obj == null) {
			return null;
		}
		Class clazz = obj.getClass();
		DataSourceConfiguration result = null;
		try {
			Method driverClassName = clazz.getMethod(getDriverClassName);
			Method url       = clazz.getMethod(getUrl);
			Method username  = clazz.getMethod(getUsername);
			Method closed    = clazz.getMethod(isClosed);
			Method numActive = clazz.getMethod(getNumActive);
			Method maxActive = clazz.getMethod(getMaxActive);
			Method minIdle   = clazz.getMethod(getMinIdle);
			Method numIdle   = clazz.getMethod(getNumIdle);
			Method maxIdle   = clazz.getMethod(getMaxIdle);
			DataSourceConfiguration config = new DataSourceConfiguration();
			config.driverClassName = (String) driverClassName.invoke(obj);
			config.url = (String) url.invoke(obj);
			config.userName = (String) username.invoke(obj);
			config.closed = ((Boolean) closed.invoke(obj)).booleanValue();
			config.numActive = ((Integer) numActive.invoke(obj)).intValue();
			config.maxActive = ((Integer) maxActive.invoke(obj)).intValue();
			config.minIdle = ((Integer) minIdle.invoke(obj)).intValue();
			config.numIdle = ((Integer) numIdle.invoke(obj)).intValue();
			config.maxIdle = ((Integer) maxIdle.invoke(obj)).intValue();
			result = config;
		} catch (SecurityException ignore) {
		} catch (NoSuchMethodException ignore) {
		} catch (IllegalAccessException ignore) {
		} catch (InvocationTargetException ignore) {
		} catch (RuntimeException ignore) {
		}
		return result;
	}
}
