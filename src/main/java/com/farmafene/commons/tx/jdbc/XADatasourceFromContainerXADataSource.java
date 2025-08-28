/*
 * Copyright (c) 2009-2022 farmafene.com
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
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XADatasourceFromContainerXADataSource implements XADataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(XADatasourceFromContainerXADataSource.class);

	private XADataSource datasource;

	/**
	 * Constructor de la clase
	 */
	public XADatasourceFromContainerXADataSource() {
	}

	/**
	 * Constructor de la clase
	 */
	public XADatasourceFromContainerXADataSource(XADataSource datasource) {
		setDatasource(datasource);
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
		sb.append("datasource=").append(this.datasource);
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
		return datasource.getLogWriter();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#setLogWriter(PrintWriter)
	 */
	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
		datasource.setLogWriter(out);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getXAConnection()
	 */
	@Override
	public XAConnection getXAConnection() throws SQLException {
		XAConnection xaCon = datasource.getXAConnection();
		LOGGER.trace("XADatasource.getConnection()");
		return new XAConnectionWrapper(xaCon);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getXAConnection(String, String)
	 */
	@Override
	public XAConnection getXAConnection(final String user, final String password) throws SQLException {
		// Pide lo que quieras, pero ya está configurado en el pool externo
		LOGGER.trace("XADatasource.getConnection({},*****)", user);
		return new XAConnectionWrapper(datasource.getXAConnection());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return datasource.getParentLogger();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#getLoginTimeout()
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return this.datasource.getLoginTimeout();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see XADataSource#setLoginTimeout(int)
	 */
	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		this.datasource.setLoginTimeout(seconds);
	}

	/**
	 * @return the datasource
	 */
	public XADataSource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(XADataSource datasource) {
		this.datasource = datasource;
	}
}
