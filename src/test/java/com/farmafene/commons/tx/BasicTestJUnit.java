package com.farmafene.commons.tx;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import com.farmafene.commons.tx.cfg.CargaProps;
import com.farmafene.commons.tx.cfg.DS1Bean;
import com.farmafene.commons.tx.cfg.DS2Bean;
import com.farmafene.commons.tx.cfg.DSBean;
import com.farmafene.commons.tx.cfg.DataSourceBeans;
import com.farmafene.commons.tx.cfg.GeronimoTXBeans;

import jakarta.transaction.TransactionManager;

@SpringJUnitConfig( //
		classes = { //
				CargaProps.class, //
				GeronimoTXBeans.class, //
				DataSourceBeans.class //
		} //
)
public class BasicTestJUnit {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicTestJUnit.class);

	@Autowired
	private TransactionManager gtm;
	@Value("${transactionTimeoutSeconds:300}")
	private int transactionTimeout;
	@Autowired
	@Qualifier("DS1")
	private DataSource ds1;
	@Autowired
	@Qualifier("DS2")
	private DataSource ds2;
	@Autowired
	private DS1Bean ds1bean;
	@Autowired
	private DS2Bean ds2bean;
	@Autowired
	private DSBean dsbean;

	@Test
	public void test01() {
		LOGGER.info("Probando lo básico ..");
		LOGGER.info("Transaction Timeout: {}", transactionTimeout);
		LOGGER.info(" TransactionManager: {}", gtm);
		LOGGER.info("                DS1: {}", ds1);
		LOGGER.info("                DS2: {}", ds2);
		try {
			LOGGER.info("  Salida primera TX: {}", ds1bean.selectFromdual());
			LOGGER.info("  Salida segunda TX: {}", ds2bean.selectFromdual());
			LOGGER.info("  Salida conjunta:   {}", dsbean.selectFromdual());
		} catch (Exception e) {
			LOGGER.error("Error en el proceso", e);
			Assert.isNull(e, "El valor debe ser null");
		}
	}
}
