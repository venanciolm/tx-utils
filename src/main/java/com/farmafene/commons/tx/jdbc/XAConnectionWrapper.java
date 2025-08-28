package com.farmafene.commons.tx.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import com.farmafene.commons.tx.XAResourceWrapper;

public class XAConnectionWrapper implements XAConnection {
	private XAConnection xaConnection;

	public XAConnectionWrapper(XAConnection xaConnection) {
		this.xaConnection = xaConnection;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.XAConnection#getXAResource()
	 */
	@Override
	public XAResource getXAResource() throws SQLException {
		return new XAResourceWrapper(xaConnection.getXAResource());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return xaConnection.getConnection();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#close()
	 */
	@Override
	public void close() throws SQLException {
		xaConnection.close();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#addConnectionEventListener(javax.sql.ConnectionEventListener)
	 */
	@Override
	public void addConnectionEventListener(ConnectionEventListener listener) {
		xaConnection.addConnectionEventListener(listener);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#removeConnectionEventListener(javax.sql.ConnectionEventListener)
	 */
	@Override
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		xaConnection.removeConnectionEventListener(listener);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#addStatementEventListener(javax.sql.StatementEventListener)
	 */
	@Override
	public void addStatementEventListener(StatementEventListener listener) {
		xaConnection.addStatementEventListener(listener);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.PooledConnection#removeStatementEventListener(javax.sql.StatementEventListener)
	 */
	@Override
	public void removeStatementEventListener(StatementEventListener listener) {
		xaConnection.removeStatementEventListener(listener);
	}
}
