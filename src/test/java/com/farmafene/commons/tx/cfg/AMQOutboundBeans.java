package com.farmafene.commons.tx.cfg;

import org.apache.activemq.artemis.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.ra.ActiveMQRAConnectionFactory;
import org.apache.activemq.artemis.ra.ActiveMQRAConnectionFactoryImpl;
import org.apache.activemq.artemis.ra.ActiveMQRAManagedConnectionFactory;
import org.apache.activemq.artemis.ra.ActiveMQResourceAdapter;
import org.apache.geronimo.connector.outbound.GenericConnectionManager;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.SinglePool;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.XATransactions;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTrackingCoordinator;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.jms.JMSException;
import jakarta.resource.ResourceException;

@Configuration
public class AMQOutboundBeans {

	@Value("${isInVMBroker:false}")
	private boolean isInVM;

	@Bean
	public JMSProducer getJMSProducer() {
		return new JMSProducer();

	}

	/**
	 * Outbound connector ... Es decir, productor!
	 * 
	 * @param gtm
	 * @return
	 * @throws ResourceException
	 * @throws JMSException
	 */
	@Bean("JMS_OUTBOUND")
	public ActiveMQRAConnectionFactory getJMSConnectionFactory(//
			@Autowired GeronimoTransactionManager gtm, //
			@Value("${activemq.producer.username}") String user, //
			@Value("${activemq.producer.password}") String password, //
			@Value("${activemq.conection.params}") String connectionParameters, //
			@Value("${activemq.outbound.pool.maxSize}") int maxSize, //
			@Value("${activemq.outbound.pool.minSize}") int minSize,
			@Value("${activemq.outbound.pool.blockingTimeoutMilliseconds}") int blockingTimeoutMilliseconds, //
			@Value("${activemq.outbound.pool.idleTimeoutMinutes}") int idleTimeoutMinutes //

	) throws ResourceException, JMSException {
		ActiveMQRAManagedConnectionFactory mcf = new ActiveMQRAManagedConnectionFactory();
		mcf.setHA(true);
		mcf.setInJtaTransaction(true);
		mcf.setAllowLocalTransactions(false);
		mcf.setBlockOnAcknowledge(true);
		mcf.setPreAcknowledge(false);
		mcf.setConnectionLoadBalancingPolicyClassName(RoundRobinConnectionLoadBalancingPolicy.class.getCanonicalName());
		if (isInVM) {
			mcf.setConnectorClassName(InVMConnectorFactory.class.getCanonicalName());
		} else {
			mcf.setConnectorClassName(NettyConnectorFactory.class.getCanonicalName());
		}
		mcf.setConnectionParameters(connectionParameters);
		ActiveMQResourceAdapter ra = new ActiveMQResourceAdapter();
		ra.setManagedConnectionFactory(mcf);
		mcf.setResourceAdapter(ra);
		// --
		ra.setHA(true);
		ra.setIgnoreJTA(false);
		ra.setUseLocalTx(false);
		ra.setPreAcknowledge(false);
		ra.setUseTopologyForLoadBalancing(true);
		ra.setBlockOnAcknowledge(false);
		ra.setConnectionLoadBalancingPolicyClassName(RoundRobinConnectionLoadBalancingPolicy.class.getCanonicalName());
		if (isInVM) {
			ra.setConnectorClassName(InVMConnectorFactory.class.getCanonicalName());
		} else {
			ra.setConnectorClassName(NettyConnectorFactory.class.getCanonicalName());
		}
		ra.setUserName(user);
		ra.setPassword(password);
		ra.setConnectionParameters(connectionParameters);
		// --
		GenericConnectionManager gcm = //
				new GenericConnectionManager( //
						new XATransactions(false, false), //
						new SinglePool( //
								maxSize, // private int maxSize;
								minSize, // private int minSize;
								blockingTimeoutMilliseconds, // private int blockingTimeoutMilliseconds;
								idleTimeoutMinutes, // private int idleTimeoutMinutes;
								true, // private boolean matchOne;
								true, // private boolean matchAll;
								true // private boolean selectOneAssumeMatch;

						), //
						null, //
						new ConnectionTrackingCoordinator(true), //
						gtm,
						//
						mcf, //
						"JMS_OUTBOUND", //
						/* getClass().getClassLoader() */null //
				);

		ActiveMQRAConnectionFactoryImpl cf = (ActiveMQRAConnectionFactoryImpl) mcf.createConnectionFactory(gcm);
		return cf;
	}
}
