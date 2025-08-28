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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase adaptadora de una conexión jdbc a una XAConnection.
 */
public class Connection2XAConnection implements XAConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(Connection2XAConnection.class);
	private final Connection connection;
	private final XAResourceJdbc xaResource;
	private final List<ConnectionEventListener> connectionEventListeners = new LinkedList<ConnectionEventListener>();
	private final List<StatementEventListener> statementEventListeners = new LinkedList<StatementEventListener>();

	/**
	 * Constructor
	 *
	 * @param connection conexión a realizar el Mock
	 */
	public Connection2XAConnection(final Connection connection) {
		this.connection = connection;
		this.xaResource = new XAResourceJdbc(connection);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.connection.hashCode();
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
		sb.append("connection=").append(this.connection);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean equals = false;
		if (obj instanceof Connection2XAConnection) {
			equals = this.connection.equals(((Connection2XAConnection) obj).connection);
		}
		return equals;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.XAConnection#getXAResource()
	 */
	@Override
	public XAResource getXAResource() throws SQLException {
		return this.xaResource;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#addConnectionEventListener(javax.sql.ConnectionEventListener)
	 */
	@Override
	public void addConnectionEventListener(final ConnectionEventListener listener) {
		LOGGER.trace("Añadido el lístener: {}", listener);
		this.connectionEventListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#close()
	 */
	@Override
	public void close() throws SQLException {
		this.connection.close();
		LOGGER.trace("close() of {}", this.connection);
		for (final ConnectionEventListener connectionEventListener : this.connectionEventListeners) {
			connectionEventListener.connectionClosed(new ConnectionEvent(this));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		LOGGER.trace("getConnection(): {}", this.connection);
		return this.connection;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#removeConnectionEventListener(javax.sql.ConnectionEventListener)
	 */
	@Override
	public void removeConnectionEventListener(final ConnectionEventListener listener) {
		this.connectionEventListeners.remove(listener);
	}

	@Override
	public void addStatementEventListener(final StatementEventListener statementeventlistener) {
		this.statementEventListeners.add(statementeventlistener);
	}

	@Override
	public void removeStatementEventListener(final StatementEventListener statementeventlistener) {
		this.statementEventListeners.remove(statementeventlistener);
	}
}
