package com.farmafene.commons.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.LocalTransaction;

public class LocalTransactionWrapper implements LocalTransaction {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalTransactionWrapper.class);

	private LocalTransaction localTransaction;

	public LocalTransactionWrapper(LocalTransaction localTransaction) {
		this.localTransaction = localTransaction;
	}

	/**
	 * 
	 * @see jakarta.resource.spi.LocalTransaction#begin()
	 */
	@Override
	public void begin() throws ResourceException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("begin(), wrapped: {}", localTransaction.getClass().getCanonicalName());
		}
		this.localTransaction.begin();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return localTransaction.hashCode();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return localTransaction.equals(obj);
	}

	/**
	 * 
	 * @see jakarta.resource.spi.LocalTransaction#commit()
	 */
	@Override
	public void commit() throws ResourceException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("commit(), wrapped: {}", localTransaction.getClass().getCanonicalName());
		}
		this.localTransaction.commit();
	}

	/**
	 * 
	 * @see jakarta.resource.spi.LocalTransaction#rollback()
	 */
	@Override
	public void rollback() throws ResourceException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("rollback(), wrapped: {}", localTransaction.getClass().getCanonicalName());
		}
		this.localTransaction.commit();
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("[");
		sb.append("wrapped=").append(localTransaction.getClass().getCanonicalName());
		sb.append(", ").append(localTransaction);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @return the localTransaction
	 */
	public LocalTransaction getLocalTransaction() {
		return localTransaction;
	}

	/**
	 * @param localTransaction the localTransaction to set
	 */
	public void setLocalTransaction(LocalTransaction localTransaction) {
		this.localTransaction = localTransaction;
	}
}
