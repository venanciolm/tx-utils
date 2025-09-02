package com.farmafene.commons.tx.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.activemq.artemis.api.core.client.loadbalance.RandomConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.ra.ActiveMQRAManagedConnectionFactory;
import org.apache.activemq.artemis.ra.ActiveMQResourceAdapter;
import org.apache.activemq.artemis.ra.inflow.ActiveMQActivationSpec;
import org.apache.geronimo.connector.GeronimoBootstrapContext;
import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.connector.work.WorkContextHandler;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.farmafene.commons.tx.jms.JMSMessageListenerEndpointFactory;

import jakarta.jms.Queue;

@Configuration
public class AMQInboundBeans {
	@Value("${isInVMBroker:false}")
	private boolean isInVM;
	@Autowired
	private DSBean bean;

	@Bean
	public GeronimoWorkManager getGeronimoWorkManager( //
			@Value("${activemq.inbound.maxSessions}") int size //
	) throws Exception {
		@SuppressWarnings("rawtypes")
		Collection<WorkContextHandler> wchs = Collections.<WorkContextHandler>emptyList();
		ExecutorService scheduledWorkExecutorPool = Executors.newFixedThreadPool(size);
		GeronimoWorkManager gwm = new GeronimoWorkManager(//
				null, // syncWorkExecutorPool
				null, // startWorkExecutorPool
				scheduledWorkExecutorPool, // scheduledWorkExecutorPool
				wchs //
		);
		gwm.doStart();
		return gwm;
	}

	@Bean
	public GeronimoBootstrapContext getGeronimoBootstrapContext( //
			@Autowired GeronimoWorkManager gwm, //
			@Autowired GeronimoTransactionManager gtm //
	) {
		GeronimoBootstrapContext gbc = new GeronimoBootstrapContext( //
				gwm, //
				gtm, //
				gtm //
		);
		return gbc;
	}

	@Bean
	DisposableBean getDisposableBeanGeronimoWorkManager(//
			@Autowired GeronimoWorkManager gwm//
	) {
		return new DisposableBean() {

			@Override
			public void destroy() throws Exception {
				gwm.doStop();
				((ExecutorService) gwm.getScheduledWorkExecutorPool()).shutdownNow();

			}
		};

	}

	@Bean
	public ActiveMQResourceAdapter getActiveMQResourceAdapter( //
			@Autowired GeronimoTransactionManager gtm, //
			@Autowired GeronimoBootstrapContext gbtc, //
			@Value("${activemq.consumer.username}") String user, //
			@Value("${activemq.consumer.password}") String password, //
			@Value("${activemq.conection.params}") String connectionParameters, //
			@Value("${activemq.conection.queue}") String queue, //
			@Value("${activemq.inbound.minSessions}") int minSessions, //
			@Value("${activemq.inbound.maxSessions}") int maxSessions //
	) throws Exception {
		ActiveMQRAManagedConnectionFactory imcf = new ActiveMQRAManagedConnectionFactory();
		imcf.setHA(true);
		imcf.setInJtaTransaction(true);
		imcf.setBlockOnAcknowledge(true);
		imcf.setAllowLocalTransactions(false);
		imcf.setPreAcknowledge(false);
		imcf.setConnectionLoadBalancingPolicyClassName(RandomConnectionLoadBalancingPolicy.class.getCanonicalName());
		ActiveMQResourceAdapter ra = new ActiveMQResourceAdapter();
		imcf.setResourceAdapter(ra);
		ra.setHA(true);
		ra.setIgnoreJTA(false);
		ra.setUseLocalTx(false);
		ra.setPreAcknowledge(false);
		ra.setUseTopologyForLoadBalancing(true);
		ra.setBlockOnAcknowledge(false);
		ra.setConnectionLoadBalancingPolicyClassName(RandomConnectionLoadBalancingPolicy.class.getCanonicalName());
		if (isInVM) {
			ra.setConnectorClassName(InVMConnectorFactory.class.getCanonicalName());
		} else {
			ra.setConnectorClassName(NettyConnectorFactory.class.getCanonicalName());
		}
		ra.setConnectionParameters(connectionParameters);
		ra.setManagedConnectionFactory(imcf);
		ra.start(gbtc);
		//
		ActiveMQActivationSpec sp = new ActiveMQActivationSpec();
		sp.setAllowLocalTransactions(false);
		sp.setUseJNDI(false);
		sp.setDestinationType(Queue.class.getCanonicalName());
		sp.setDestination(queue);
		sp.setUserName(user);
		sp.setPassword(password);
		sp.setMinSession(minSessions);
		sp.setMaxSession(maxSessions);
		sp.setConnectionLoadBalancingPolicyClassName(RandomConnectionLoadBalancingPolicy.class.getCanonicalName());
		JMSConsumer mep = new JMSConsumer(bean, queue);
		JMSMessageListenerEndpointFactory jmsepf = new JMSMessageListenerEndpointFactory(gtm, mep);
		ra.endpointActivation(jmsepf, sp);
		return ra;
	}

	@Bean
	DisposableBean getDisposableBeanActiveMQResourceAdapter(//
			@Autowired ActiveMQResourceAdapter ra //
	) {
		return new DisposableBean() {

			@Override
			public void destroy() throws Exception {
				ra.stop();
			}
		};
	}
}
