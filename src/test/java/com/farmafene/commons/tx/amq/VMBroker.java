package com.farmafene.commons.tx.amq;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VMBroker {

	private static final Logger LOGGER = LoggerFactory.getLogger(VMBroker.class);

	@Bean
	public EmbeddedActiveMQ EmbeddedActiveMQ() {
		EmbeddedActiveMQ broker = new EmbeddedActiveMQ();
		broker.setConfigResourcePath("file:src/test/resources/com/farmafene/commons/tx/amq/amq.config.xml");
		try {
			broker.start();
		} catch (Exception e) {
			LOGGER.error("Error en arranque del broker", e);
			throw new UnsupportedOperationException(e);
		}
		return broker;
//		return new AbstractDisposableBeanFactory<EmbeddedActiveMQ>(broker) {
//			/**
//			 * 
//			 * @see com.farmafene.commons.tx.jms.AbstractDisposableBeanFactory#destroy()
//			 */
//			@Override
//			public void destroy() throws Exception {
//				LOGGER.info("Parando el \"EmbeddedActiveMQ\" con instancia: {}", broker);
//				broker.stop();
//			}
//		};
	}

	@Bean
	DisposableBean DisposableBeanEmbeddedActiveMQ(//
			@Autowired EmbeddedActiveMQ broker) {
		return new DisposableBean() {

			/**
			 * 
			 * @see org.springframework.beans.factory.DisposableBean#destroy()
			 */
			@Override
			public void destroy() throws Exception {
				LOGGER.info("Parando el \"EmbeddedActiveMQ\" con instancia: {}", broker);
				broker.stop();
			}
		};
	}

}
