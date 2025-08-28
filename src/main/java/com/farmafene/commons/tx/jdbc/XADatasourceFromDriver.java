/*
 * Copyright (c) 2009-2015 farmafene.com
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.farmafene.commons.tx.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

/**
 * Implementación de un XADataSource para no-XA JDBC Driver.
 */
public class XADatasourceFromDriver implements XADataSource {

	private int loginTimeout = 20;
	private String driverClassName;
	private String url;
	private String user;
	private String password;

	/**
	 * Constructor de la clase
	 */
	public XADatasourceFromDriver() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("={");
		sb.append("url=").append(this.url);
		sb.append(", driverClassName=").append(this.driverClassName);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getLogWriter()
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#setLogWriter(PrintWriter)
	 */
	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getXAConnection()
	 */
	@Override
	public XAConnection getXAConnection() throws SQLException {
		return getXAConnection(this.user, this.password);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getXAConnection(String, String)
	 */
	@Override
	public XAConnection getXAConnection(final String user, final String password) throws SQLException {
		try {
			final Properties props = new Properties();
			props.setProperty("user", user);
			props.setProperty("password", password);
			Connection connection = null;
			try {
				connection = DriverManager.getConnection(this.url, props);
			} catch (final RuntimeException th) {
				throw th;
			} catch (final SQLException th) {
				throw th;
			} catch (final Throwable th) {
				throw new SQLException(th);
			} finally {
			}
			final XAConnection xaCon = new Connection2XAConnection(connection);
			return xaCon;
		} catch (final Exception ex) {
			throw (SQLException) new SQLException("Imposible conectar al recurso no-XA " + this.driverClassName)
					.initCause(ex);
		}
	}

	private Class<?> loadClass(final String className) throws ClassNotFoundException {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null) {
			try {
				return cl.loadClass(className);
			} catch (final ClassNotFoundException ex) {
			}
		}

		return Class.forName(className);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("Not supported!");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getLoginTimeout()
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return this.loginTimeout;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#setLoginTimeout(int)
	 */
	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		this.loginTimeout = seconds;
	}

	/**
	 * Obtiene la clase configurada como driver
	 *
	 * @return clase del driver
	 */
	public String getDriverClassName() {
		return this.driverClassName;
	}

	/**
	 * Establece el driver y lo registra
	 *
	 * @param driverClassName clase del driver
	 * @throws ClassNotFoundException no se encuenta la clase
	 * @throws IllegalAccessException no se puede acceder al método constructor
	 * @throws InstantiationException no se puede instanciar la clase
	 * @throws SQLException           error al registrar el driver
	 */
	@SuppressWarnings("deprecation")
	public void setDriverClassName(final String driverClassName)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
		this.driverClassName = driverClassName;
		final Class<?> driverClazz = loadClass(driverClassName);
		DriverManager.registerDriver((Driver) driverClazz.newInstance());
	}

	/**
	 * Obtiene la url de conexión para el driver
	 *
	 * @return la url de conexión
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Establece la URL de conexión para el driver.
	 *
	 * @param url la url de conexión
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * Obtiene el usuario del driver
	 *
	 * @return el usuario
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Establece el usuario para el driver
	 *
	 * @param user el usuario
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 * Obtiene la password para el driver
	 *
	 * @return la password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Establece la password al driver
	 *
	 * @param password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}
}
