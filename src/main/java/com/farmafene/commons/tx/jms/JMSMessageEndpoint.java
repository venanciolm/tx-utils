package com.farmafene.commons.tx.jms;

import java.lang.reflect.Method;

import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmafene.commons.tx.XAHelper;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.endpoint.MessageEndpoint;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;

public class JMSMessageEndpoint implements MessageEndpoint, MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(JMSMessageEndpoint.class);
	private MessageListener messageListener;
	private TransactionManager transactionManager;
	private XAResource xAResource;

	/**
	 * 
	 * @param xAResource         XAResource a tratar
	 * @param messageListener    Listener a tratar
	 * @param transactionManager (si hay gestor y se quiere gestionar la TX
	 *                           transacionalmente en el listener
	 */
	JMSMessageEndpoint( //
			XAResource xAResource, //
			MessageListener messageListener, //
			TransactionManager transactionManager //
	) {
		this.messageListener = messageListener;
		this.transactionManager = transactionManager;
		this.xAResource = xAResource;
	}

	/**
	 * 
	 * @see jakarta.jms.MessageListener#onMessage(jakarta.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		LOGGER.trace("onMessage(Message: {})", message);
		if (null != transactionManager) {
			try {
				messageListener.onMessage(message);
				LOGGER.trace("onMessage(OK, Message: {})", message);
			} catch (Throwable th) {
				LOGGER.debug("onMessage(KO, Message: {})", message, th);
				try {
					this.transactionManager.setRollbackOnly();
				} catch (IllegalStateException e) {
					LOGGER.error("onMessage(KO, Message: {})", message, th);
				} catch (SystemException e) {
					LOGGER.error("onMessage(KO, Message: {})", message, th);
				}
			}
		} else {
			messageListener.onMessage(message);
		}
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpoint#beforeDelivery(java.lang.reflect.Method)
	 */
	@Override
	public void beforeDelivery(Method method) throws NoSuchMethodException, ResourceException {
		LOGGER.trace("beforeDelivery(Method: {})", method);
		if (null != transactionManager) {
			try {
				if (this.transactionManager.getTransaction() == null) {
					LOGGER.trace("beforeDelivery(Method: {}).begin()", method);
					this.transactionManager.begin();
				}
			} catch (NotSupportedException e) {
				LOGGER.error("beforeDelivery(KO, Method: {}).begin()", method, e);
				throw new ResourceException(e);
			} catch (SystemException e) {
				LOGGER.error("beforeDelivery(KO, Method: {}).begin()", method, e);
				throw new ResourceException(e);
			} catch (IllegalStateException e) {
				LOGGER.error("beforeDelivery(KO, Method: {}).begin()", method, e);
				throw new ResourceException(e);
			}
			try {
				if (this.transactionManager.getTransaction() != null) {
					LOGGER.trace(".enlistResource({})", xAResource);
					this.transactionManager.getTransaction().enlistResource(xAResource);
				}
			} catch (RollbackException e) {
				LOGGER.error("beforeDelivery(KO, Method: {}).enlistResource(..)", method, e);
				throw new ResourceException(e);
			} catch (IllegalStateException e) {
				LOGGER.error("beforeDelivery(KO, Method: {}).enlistResource(..)", method, e);
				throw new ResourceException(e);
			} catch (SystemException e) {
				LOGGER.error("beforeDelivery(KO, Method: {}).enlistResource(..)", method, e);
				throw new ResourceException(e);
			}
			LOGGER.trace("beforeDelivery(OK, Method: {})", method);
		}
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpoint#afterDelivery()
	 */
	@Override
	public void afterDelivery() throws ResourceException {
		LOGGER.trace("afterDelivery()");
		if (null != transactionManager) {
			try {
				if (this.transactionManager.getTransaction() != null) {
					LOGGER.trace("afterDelivery({})",
							XAHelper.getStringFromStatus(this.transactionManager.getTransaction()));
					switch (this.transactionManager.getTransaction().getStatus()) {
					case Status.STATUS_ACTIVE:
						LOGGER.trace("afterDelivery(){}", ".commit()");
						this.transactionManager.commit();
						break;
					case Status.STATUS_MARKED_ROLLBACK:
						LOGGER.trace("afterDelivery(){}", ".rollback()");
						this.transactionManager.rollback();
						break;
					case Status.STATUS_PREPARED:
					case Status.STATUS_COMMITTED:
					case Status.STATUS_ROLLEDBACK:
					case Status.STATUS_NO_TRANSACTION:
					case Status.STATUS_PREPARING:
					case Status.STATUS_COMMITTING:
					case Status.STATUS_ROLLING_BACK:
					case Status.STATUS_UNKNOWN:
					default:
						// do nothing o mandar exepción
						break;
					}
				} else {
					LOGGER.trace("afterDelivery({})", "<???>");
				}
			} catch (SystemException e) {
				LOGGER.error("afterDelivery()", e);
				throw new ResourceException(e);
			} catch (IllegalStateException e) {
				LOGGER.error("afterDelivery()", e);
				throw new ResourceException(e);
			} catch (SecurityException e) {
				LOGGER.error("afterDelivery()", e);
				throw new ResourceException(e);
			} catch (RollbackException e) {
				LOGGER.error("afterDelivery()", e);
				throw new ResourceException(e);
			} catch (HeuristicMixedException e) {
				LOGGER.error("afterDelivery()", e);
				throw new ResourceException(e);
			} catch (HeuristicRollbackException e) {
				LOGGER.error("afterDelivery()", e);
				throw new ResourceException(e);
			}
		}
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpoint#release()
	 */
	@Override
	public void release() {
		LOGGER.trace("release()");
	}
}
