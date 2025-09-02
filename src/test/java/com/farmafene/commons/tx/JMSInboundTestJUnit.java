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
import com.farmafene.commons.tx.cfg.CargaProps;
import com.farmafene.commons.tx.cfg.DataSourceBeans;
import com.farmafene.commons.tx.cfg.GeronimoTXBeans;

import jakarta.transaction.TransactionManager;

@SpringJUnitConfig( //
		classes = { //
				CargaProps.class, //
				GeronimoTXBeans.class, //
				AMQInboundBeans.class, //
				DataSourceBeans.class //
		} //
)
public class JMSInboundTestJUnit {
	private static final Logger LOGGER = LoggerFactory.getLogger(JMSInboundTestJUnit.class);

	@Autowired
	private TransactionManager gtm;
	@Value("${transactionTimeoutSeconds:30}")
	private int transactionTimeout;
	@Value("${inbound.test.seconds:5}")
	private int testSeconds;

	@Test
	public void test01() {
		LOGGER.info("Probando lo básico");
		LOGGER.info("Transaction Timeout: {}", transactionTimeout);
		LOGGER.info("TransactionManager:  {}", gtm);
		LOGGER.info("TestSeconds:  {}", testSeconds);

		StringBuilder sb = null;
		String lf = System.lineSeparator();
		try {
			sb = new StringBuilder();
			sb.append(lf).append("/+--------------------------------------------------+");
			sb.append(lf).append(" | Esperando Mensajes (JMSOutboundTestJUnit)        |");
			sb.append(lf).append(" +--------------------------------------------------+");
			LOGGER.info("{}", sb);
			CountDownLatch l = new CountDownLatch(1);
			l.await(testSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.info("Interrumpido", e);
		} catch (Exception e) {
			LOGGER.error("Error en el proceso", e);
			Assert.isNull(e, e.getMessage());
		} finally {
			sb = new StringBuilder();
			sb.append(lf).append("/+--------------------------------------------------+");
			sb.append(lf).append(" | MIRAMOS SI HAY TX                                |");
			sb.append(lf).append(" +--------------------------------------------------+");
			LOGGER.info("{}", sb);
		}
	}
}
