package com.farmafene.commons.tx.cfg;

import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
public class GeronimoTXBeans {
	@Value("${transactionTimeoutSeconds:300}")
	private int transactionTimeout;

	@Bean
	public PlatformTransactionManager getPlatformTransactionManager( //
			@Autowired GeronimoTransactionManager gtm //
	) {
		JtaTransactionManager ptm = new JtaTransactionManager(gtm, gtm);
		ptm.setTransactionSynchronizationRegistry(gtm);
		return ptm;
	}

	@Bean
	public GeronimoTransactionManager getTransactionManager() throws XAException {
		GeronimoTransactionManager gtm = new GeronimoTransactionManager(transactionTimeout);
		return gtm;
	}
}
