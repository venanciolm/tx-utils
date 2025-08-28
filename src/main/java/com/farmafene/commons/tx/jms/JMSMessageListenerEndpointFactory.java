package com.farmafene.commons.tx.jms;

import java.lang.reflect.Method;

import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.MessageListener;
import jakarta.resource.spi.UnavailableException;
import jakarta.resource.spi.endpoint.MessageEndpoint;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;
import jakarta.transaction.TransactionManager;

public class JMSMessageListenerEndpointFactory implements MessageEndpointFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(JMSMessageListenerEndpointFactory.class);
	private MessageListener messageListener;
	private TransactionManager transactionManager;
	private String activationName = "JMS_Activation";
	private boolean deliveryTransacted = true;

	/**
	 * Constructor básico del EndpointFactory
	 * 
	 * @param transactionManager @param transactionManager (si hay gestor y se
	 *                           quiere gestionar la TX transacionalmente en el
	 *                           listener
	 * 
	 * @param messageListener    Instancia que gestiona la llegada de mensajes.
	 */
	public JMSMessageListenerEndpointFactory(TransactionManager transactionManager, MessageListener messageListener) {
		this.messageListener = messageListener;
		this.transactionManager = transactionManager;
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpointFactory#createEndpoint(javax.transaction.xa.XAResource)
	 */
	@Override
	public MessageEndpoint createEndpoint(XAResource xaResource) throws UnavailableException {
		LOGGER.info("createEndpoint(XAResource: {})", xaResource);
		return new JMSMessageEndpoint(xaResource, messageListener, transactionManager);
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpointFactory#createEndpoint(javax.transaction.xa.XAResource,
	 *      long)
	 */
	@Override
	public MessageEndpoint createEndpoint(XAResource xaResource, long timeout) throws UnavailableException {
		LOGGER.info("createEndpoint(XAResource: {}, timeout: {})", xaResource, timeout);
		return new JMSMessageEndpoint(xaResource, messageListener, transactionManager);
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpointFactory#isDeliveryTransacted(java.lang.reflect.Method)
	 */
	@Override
	public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
		LOGGER.info("isDeliveryTransacted(Method: {})", method);
		return deliveryTransacted;
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpointFactory#getActivationName()
	 */
	@Override
	public String getActivationName() {
		return this.activationName;
	}

	/**
	 * 
	 * @see jakarta.resource.spi.endpoint.MessageEndpointFactory#getEndpointClass()
	 */
	@Override
	public Class<?> getEndpointClass() {
		return MessageListener.class;
	}

	/**
	 * @return the messageListener
	 */
	public MessageListener getMessageListener() {
		return messageListener;
	}

	/**
	 * @param messageListener the messageListener to set
	 */
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the deliveryTransacted
	 */
	public boolean isDeliveryTransacted() {
		return deliveryTransacted;
	}

	/**
	 * @param deliveryTransacted the deliveryTransacted to set
	 */
	public void setDeliveryTransacted(boolean deliveryTransacted) {
		this.deliveryTransacted = deliveryTransacted;
	}

	/**
	 * @param activationName the activationName to set
	 */
	public void setActivationName(String activationName) {
		this.activationName = activationName;
	}
}
