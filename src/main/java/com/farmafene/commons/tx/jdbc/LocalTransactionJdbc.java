package com.farmafene.commons.tx.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.LocalTransaction;

public class LocalTransactionJdbc implements LocalTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalTransactionJdbc.class);
	private Connection connection;

	public LocalTransactionJdbc(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 
	 * @see jakarta.resource.spi.LocalTransaction#begin()
	 */
	@Override
	public void begin() throws ResourceException {
		LOGGER.trace("begin()");
	}

	/**
	 * 
	 * @see jakarta.resource.spi.LocalTransaction#commit()
	 */
	@Override
	public void commit() throws ResourceException {
		LOGGER.trace("commit()");
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * 
	 * @see jakarta.resource.spi.LocalTransaction#rollback()
	 */
	@Override
	public void rollback() throws ResourceException {
		LOGGER.trace("rollback()");
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
