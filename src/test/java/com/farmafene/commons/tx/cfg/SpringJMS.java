package com.farmafene.commons.tx.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.activemq.artemis.api.core.client.loadbalance.RandomConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.ra.ActiveMQRAConnectionFactoryImpl;
import org.apache.activemq.artemis.ra.ActiveMQRAManagedConnectionFactory;
import org.apache.activemq.artemis.ra.ActiveMQResourceAdapter;
import org.apache.activemq.artemis.ra.inflow.ActiveMQActivationSpec;
import org.apache.geronimo.connector.outbound.GenericConnectionManager;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.LocalTransactions;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.SinglePool;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTrackingCoordinator;
import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.connector.work.WorkContextHandler;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jca.support.SimpleBootstrapContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.work.WorkManager;

@Configuration
public class SpringJMS {
	@Value("${isInVMBroker:false}")
	private boolean isInVM;

	@Bean("SpringWorkManager")
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
	DisposableBean getDisposableBeanGeronimoWorkManager(//
			@Autowired @Qualifier("SpringWorkManager") GeronimoWorkManager gwm //
	) {
		return new DisposableBean() {

			@Override
			public void destroy() throws Exception {
				gwm.doStop();
				((ExecutorService) gwm.getScheduledWorkExecutorPool()).shutdownNow();
			}
		};

	}

	@Bean("SpringListener")
	MessageListener getMessageListener() {
		return new SpringMessageListener();
	}

	@Bean("SpringMCF")
	ActiveMQRAManagedConnectionFactory getActiveMQRAManagedConnectionFactory() {
		ActiveMQRAManagedConnectionFactory imcf = new ActiveMQRAManagedConnectionFactory();
		imcf.setHA(true);
		imcf.setInJtaTransaction(true);
		imcf.setBlockOnAcknowledge(true);
		imcf.setAllowLocalTransactions(true);
		imcf.setPreAcknowledge(false);
		imcf.setConnectionLoadBalancingPolicyClassName(RandomConnectionLoadBalancingPolicy.class.getCanonicalName());
		return imcf;
	}

	@Bean("SpringResourceAdapter")
	ActiveMQResourceAdapter getResourceAdapter( //
			@Autowired GeronimoTransactionManager gtm, //
			@Autowired @Qualifier("SpringMCF") ActiveMQRAManagedConnectionFactory imcf, //
			@Value("${activemq.consumer.username}") String user, //
			@Value("${activemq.consumer.password}") String password, //
			@Autowired @Qualifier("SpringWorkManager") WorkManager workManager //
	) throws ResourceException {

		ActiveMQResourceAdapter ra = new ActiveMQResourceAdapter();
		ra.setHA(true);
		ra.setIgnoreJTA(false);
		ra.setUseLocalTx(true);
		ra.setPreAcknowledge(false);
		ra.setUseTopologyForLoadBalancing(true);
		ra.setBlockOnAcknowledge(false);
		ra.setConnectionLoadBalancingPolicyClassName(RandomConnectionLoadBalancingPolicy.class.getCanonicalName());
		if (isInVM) {
			ra.setConnectorClassName(InVMConnectorFactory.class.getCanonicalName());
		} else {
			ra.setConnectorClassName(NettyConnectorFactory.class.getCanonicalName());
		}
		ra.start(new SimpleBootstrapContext(workManager, gtm, gtm));
		return ra;
	}

	@Bean
	DisposableBean getDisposableBeanActiveMQResourceAdapter(//
			@Autowired @Qualifier("SpringResourceAdapter") ActiveMQResourceAdapter ra //
	) {
		return new DisposableBean() {

			@Override
			public void destroy() throws Exception {
				ra.stop();
			}
		};

	}

	@Bean("SpringJMSCF")
	ConnectionFactory getActiveMQRAConnectionFactory( //
			@Autowired GeronimoTransactionManager gtm, //
			@Value("${activemq.consumer.username}") String user, //
			@Value("${activemq.consumer.password}") String password, //
			@Value("${activemq.conection.params}") String connectionParameters, //
			@Value("${activemq.outbound.pool.maxSize}") int maxSize, //
			@Value("${activemq.outbound.pool.minSize}") int minSize,
			@Value("${activemq.outbound.pool.blockingTimeoutMilliseconds}") int blockingTimeoutMilliseconds, //
			@Value("${activemq.outbound.pool.idleTimeoutMinutes}") int idleTimeoutMinutes //
	) throws ResourceException {
		ActiveMQRAManagedConnectionFactory mcf = new ActiveMQRAManagedConnectionFactory();
		mcf.setHA(true);
		mcf.setInJtaTransaction(true);
		mcf.setAllowLocalTransactions(true);
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
		ra.setUseLocalTx(true);
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
						LocalTransactions.INSTANCE, //
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

	@Bean
	JmsMessageEndpointManager jmsMessageEndpointManager( //
			@Autowired @Qualifier("SpringListener") MessageListener myMessageListener, //
			@Autowired @Qualifier("SpringResourceAdapter") ResourceAdapter ra, //
			@Value("${activemq.consumer.username}") String user, //
			@Value("${activemq.consumer.password}") String password, //
			@Value("${activemq.conection.params}") String connectionParameters, //
			@Value("${activemq.conection.queue}") String queue //

	) throws Exception {
		ActiveMQActivationSpec spec = new ActiveMQActivationSpec();
		spec.setDestination(queue);
		spec.setDestinationType(Queue.class.getCanonicalName());
		spec.setConnectionParameters(connectionParameters);
		spec.setUserName(user);
		spec.setPassword(password);
		spec.setResourceAdapter(ra);
		spec.setUseJNDI(false);

		JmsMessageEndpointManager endpointManager = new JmsMessageEndpointManager();
		endpointManager.setResourceAdapter(ra);
		endpointManager.setActivationSpec(spec);
		endpointManager.setMessageListener(myMessageListener);
		return endpointManager;
	}

	@Bean
	DSBean getDSBean() {
		return new DSBean();
	}

	@Bean
	DS1Bean getDS1Bean() {
		return new DS1Bean();
	}

	@Bean
	DS2Bean getDS2Bean() {
		return new DS2Bean();
	}

	@Bean
	JmsTemplate getJMSTemplate(//
			@Autowired @Qualifier("SpringJMSCF") ConnectionFactory cf//
	) throws ResourceException {
		JmsTemplate a = new JmsTemplate(cf);
		return a;
	}

	@Bean
	SpringJMSProducer getSpringJMSProducer() {
		return new SpringJMSProducer();
	}
}
