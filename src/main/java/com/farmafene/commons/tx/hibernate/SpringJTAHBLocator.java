package com.farmafene.commons.tx.hibernate;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

@SuppressWarnings("serial")
public class SpringJTAHBLocator//
		extends org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform //
		implements InitializingBean, Serializable {

	private static TransactionManager TRANSACTION_MANAGER;
	private static UserTransaction USER_TRANSACTION;
	private TransactionManager transactionManager;
	private UserTransaction userTransaction;

	public SpringJTAHBLocator() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform#locateTransactionManager()
	 */
	@Override
	protected TransactionManager locateTransactionManager() {
		return TRANSACTION_MANAGER;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform#locateUserTransaction()
	 */
	@Override
	protected UserTransaction locateUserTransaction() {
		return USER_TRANSACTION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		USER_TRANSACTION = this.userTransaction;
		TRANSACTION_MANAGER = this.transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @param userTransaction the userTransaction to set
	 */
	public void setUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}
}
