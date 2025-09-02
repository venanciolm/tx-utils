package com.farmafene.commons.tx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import com.farmafene.commons.tx.cfg.AMQInboundBeans;
import com.farmafene.commons.tx.cfg.AMQOutboundBeans;
import com.farmafene.commons.tx.cfg.CargaProps;
import com.farmafene.commons.tx.cfg.DSBean;
import com.farmafene.commons.tx.cfg.DataSourceBeans;
import com.farmafene.commons.tx.cfg.GeronimoTXBeans;
import com.farmafene.commons.tx.cfg.JMSProducer;

import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;

@SpringJUnitConfig( //
		classes = { //
				CargaProps.class, //
				GeronimoTXBeans.class, //
				AMQInboundBeans.class, //
				AMQOutboundBeans.class, //
//				JMSProducer.class, //
				DataSourceBeans.class //
		} //
)
public class JMSTestJUnit {
	private static final Logger LOGGER = LoggerFactory.getLogger(JMSTestJUnit.class);

	@Autowired
	private TransactionManager gtm;
	@Value("${transactionTimeoutSeconds:300}")
	private int transactionTimeout;

	@Autowired
	private ConnectionFactory jmsCF;
	@Autowired
	private JMSProducer prod;
	@Autowired
	DSBean bean;

	@Test
	public void test01() {
		LOGGER.info("Probando lo básico");
		LOGGER.info("Transaction Timeout: {}", transactionTimeout);
		LOGGER.info("TransactionManager:  {}", gtm);
		LOGGER.info("JMSConnectonFactory: {}", jmsCF);

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
