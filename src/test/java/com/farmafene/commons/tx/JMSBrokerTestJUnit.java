package com.farmafene.commons.tx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import com.farmafene.commons.tx.amq.CargaProps;
import com.farmafene.commons.tx.amq.VMBroker;
import com.farmafene.commons.tx.cfg.AMQInboundBeans;
import com.farmafene.commons.tx.cfg.AMQOutboundBeans;
import com.farmafene.commons.tx.cfg.DSBean;
import com.farmafene.commons.tx.cfg.DataSourceBeans;
import com.farmafene.commons.tx.cfg.GeronimoTXBeans;
import com.farmafene.commons.tx.cfg.JMSProducer;

import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;

@SpringJUnitConfig( //
		classes = { //
				CargaProps.class, //
				VMBroker.class,
				GeronimoTXBeans.class, //
				AMQOutboundBeans.class, //
				AMQInboundBeans.class, //
				DataSourceBeans.class //
		} //
)
public class JMSBrokerTestJUnit {
	private static final Logger LOGGER = LoggerFactory.getLogger(JMSBrokerTestJUnit.class);

	@Autowired
	private EmbeddedActiveMQ broker;
	@Autowired
	private TransactionManager gtm;
	@Value("${transactionTimeoutSeconds:300}")
	private int transactionTimeout;

	@Autowired
	private ConnectionFactory jmsCF;
	@Autowired
	private JMSProducer prod;
	@Autowired
	private DSBean bean;

	@Test
	public void test01() {
		LOGGER.info("Probando lo básico");
		LOGGER.info("            Broker: {}", broker);
		LOGGER.info("TransactionTimeout: {}", transactionTimeout);
		LOGGER.info("TransactionManager: {}", gtm);
		LOGGER.info(" ConnectionFactory: {}", jmsCF);
		LOGGER.info("       JMSProducer: {}", prod);
		LOGGER.info("            DSBean: {}", bean);
		StringBuilder sb = null;
		String lf = System.lineSeparator();
		try {
			sb = new StringBuilder();
			sb.append(lf).append("/+--------------------------------------------------+");
			sb.append(lf).append(" | ENVIANDO EL MENSAJE                              |");
			sb.append(lf).append(" +--------------------------------------------------+");
			CountDownLatch l = new CountDownLatch(1);
			bean.setLatch(l);
			prod.sendMessage();
			sb = new StringBuilder();
			sb.append(lf).append("/+--------------------------------------------------+");
			sb.append(lf).append(" | ENVIADO EL MENSAJE                               |");
			sb.append(lf).append(" +--------------------------------------------------+");
			LOGGER.info("{}", sb);
			l.await(5, TimeUnit.SECONDS);
			sb = new StringBuilder();
			sb.append(lf).append("/+--------------------------------------------------+");
			sb.append(lf).append(" | MIRAMOS SI HAY TX                                |");
			sb.append(lf).append(" +--------------------------------------------------+");
			LOGGER.info("{}", sb);
		} catch (InterruptedException e) {
			LOGGER.info("{}", "/*******************", e);
		} catch (Exception e) {
			LOGGER.error("Error en el proceso", e);
			Assert.isNull(e, e.getMessage());
		}
	}
}
