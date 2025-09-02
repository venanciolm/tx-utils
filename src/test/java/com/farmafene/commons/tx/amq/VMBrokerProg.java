package com.farmafene.commons.tx.amq;

import java.io.File;

import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.JournalType;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressFullMessagePolicy;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.utils.critical.CriticalAnalyzerPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class VMBrokerProg {

	private static final Logger LOGGER = LoggerFactory.getLogger(VMBrokerProg.class);

	@Bean
	public EmbeddedActiveMQ EmbeddedActiveMQ() {
		EmbeddedActiveMQ broker = new EmbeddedActiveMQ();
		Configuration cfg = new ConfigurationImpl();
		broker.setConfiguration(cfg);
		cfg.setName("EmbeddedActiveMQ");
//		<paging-directory>target/amq/data/paging</paging-directory>
//		<bindings-directory>target/amq/bindings</bindings-directory>
//		<journal-directory>target/amq/data/journal</journal-directory>
//		<large-messages-directory>target/amq/data/large-messages</large-messages-directory>
		cfg.setBrokerInstance(new File(new File("target"), "amq-prog"));
//		<max-redelivery-records>1</max-redelivery-records>
		cfg.setMaxRedeliveryRecords(1);
//		<security-enabled>false</security-enabled>
		cfg.setSecurityEnabled(false);
//		<persistence-enabled>true</persistence-enabled>
		cfg.setPersistenceEnabled(true);
//		<journal-type>ASYNCIO</journal-type>
		cfg.setJournalType(JournalType.ASYNCIO);
//		<journal-datasync>true</journal-datasync>
		cfg.setJournalDatasync(true);
//		<journal-min-files>2</journal-min-files>
		cfg.setJournalMinFiles(2);
//		<journal-pool-files>10</journal-pool-files>
		cfg.setJournalPoolFiles(10);
//		<journal-device-block-size>4096</journal-device-block-size>
		cfg.setJournalDeviceBlockSize(4096);
//		<journal-file-size>10M</journal-file-size>
		cfg.setJournalFileSize(10 * 1024 * 1024);
//		<journal-buffer-timeout>660000</journal-buffer-timeout>
		cfg.setJournalBufferTimeout_AIO(660000);
		cfg.setJournalBufferTimeout_NIO(660000);
//		<journal-max-io>4096</journal-max-io>
		cfg.setJournalMaxIO_AIO(4096);
		cfg.setJournalMaxIO_NIO(4096);
//		<disk-scan-period>5000</disk-scan-period>
		cfg.setDiskScanPeriod(5000);
//		<max-disk-usage>90</max-disk-usage>
		cfg.setMaxDiskUsage(90);
//		<critical-analyzer>true</critical-analyzer>
		cfg.setCriticalAnalyzer(true);
//		<critical-analyzer-timeout>120000</critical-analyzer-timeout>
		cfg.setCriticalAnalyzerTimeout(120000);
//		<critical-analyzer-check-period>60000</critical-analyzer-check-period>
		cfg.setCriticalAnalyzerCheckPeriod(60000);
//		<critical-analyzer-policy>HALT</critical-analyzer-policy>
		cfg.setCriticalAnalyzerPolicy(CriticalAnalyzerPolicy.HALT);
//		<page-sync-timeout>2544000</page-sync-timeout>
		cfg.setPageSyncTimeout(2544000);
//		<global-max-messages>-1</global-max-messages>
		cfg.setGlobalMaxMessages(-1L);
//		<persist-delivery-count-before-delivery>true</persist-delivery-count-before-delivery>
		cfg.setPersistDeliveryCountBeforeDelivery(true);
		try {
			cfg.addAcceptorConfiguration("in-vm", "vm://0");
			CoreAddressConfiguration addrCfg = new CoreAddressConfiguration();
			cfg.addAddressConfiguration(addrCfg);
			addrCfg.setName("Default");

			QueueConfiguration dlqQueue = new QueueConfiguration();
			addrCfg.addQueueConfig(dlqQueue);
			dlqQueue.setRoutingType(RoutingType.ANYCAST);
			dlqQueue.setName("DeadLetterQueue");
			dlqQueue.setAddress("DLQ");
			dlqQueue.setInternal(true);
			dlqQueue.setDurable(true);
			dlqQueue.setEnabled(true);
			dlqQueue.setAutoCreateAddress(true);
			dlqQueue.setTemporary(false);

			QueueConfiguration eqQueue = new QueueConfiguration();
			addrCfg.addQueueConfig(eqQueue);
			eqQueue.setRoutingType(RoutingType.ANYCAST);
			eqQueue.setName("ExpiryQueue");
			eqQueue.setAddress("ExpiryQueue");
			eqQueue.setInternal(true);
			eqQueue.setDurable(true);
			eqQueue.setEnabled(true);
			eqQueue.setAutoCreateAddress(true);
			eqQueue.setTemporary(false);

			AddressSettings mSetting = new AddressSettings();
			mSetting.setDefaultAddressRoutingType(RoutingType.ANYCAST);
//	    <address-setting match="activemq.management#">
			cfg.addAddressSetting("activemq.management#", mSetting);
//	            <dead-letter-address>DLQ</dead-letter-address>
			mSetting.setDeadLetterAddress(new SimpleString("DLQ"));
//	            <expiry-address>ExpiryQueue</expiry-address>
			mSetting.setExpiryAddress(new SimpleString("ExpiryQueue"));
//	            <redelivery-delay>10000</redelivery-delay>
			mSetting.setRedeliveryDelay(10000L);
//	            <!-- with -1 only the global-max-size is in use for limiting -->
//	            <max-size-bytes>-1</max-size-bytes>
			mSetting.setMaxSizeBytes(-1);
//	            <message-counter-history-day-limit>10</message-counter-history-day-limit>
			mSetting.setMessageCounterHistoryDayLimit(10);
//	            <address-full-policy>PAGE</address-full-policy>
			mSetting.setAddressFullMessagePolicy(AddressFullMessagePolicy.PAGE);
//	            <auto-create-queues>true</auto-create-queues>
			mSetting.setAutoCreateQueues(true);
//	            <auto-create-addresses>true</auto-create-addresses>
			mSetting.setAutoCreateAddresses(true);
//	     </address-setting>

			AddressSettings gSetting = new AddressSettings();
			gSetting.setDefaultAddressRoutingType(RoutingType.ANYCAST);
//		<address-setting match="#">
			cfg.addAddressSetting("#", gSetting);
//			<dead-letter-address>DLQ</dead-letter-address>
			gSetting.setDeadLetterAddress(new SimpleString("DLQ"));
//			<expiry-address>ExpiryQueue</expiry-address>
			gSetting.setExpiryAddress(new SimpleString("ExpiryQueue"));
//			<!-- DLQ -->
//			<redelivery-delay>10000</redelivery-delay>
			gSetting.setMaxRedeliveryDelay(10000);
//			<max-delivery-attempts>6</max-delivery-attempts>
			gSetting.setMaxDeliveryAttempts(6);
//			<auto-create-dead-letter-resources>true</auto-create-dead-letter-resources>
			gSetting.setAutoCreateDeadLetterResources(true);
//			<dead-letter-queue-prefix></dead-letter-queue-prefix> <!-- override the default -->
			gSetting.setDeadLetterQueuePrefix(new SimpleString(""));
//			<dead-letter-queue-suffix>.dlq</dead-letter-queue-suffix>
			gSetting.setDeadLetterQueueSuffix(new SimpleString(".dlq"));
//			<!-- DLQ -->
//			<message-counter-history-day-limit>10</message-counter-history-day-limit>
			gSetting.setMessageCounterHistoryDayLimit(10);
//			<address-full-policy>PAGE</address-full-policy>
			gSetting.setAddressFullMessagePolicy(AddressFullMessagePolicy.PAGE);
//			<auto-create-queues>true</auto-create-queues>
			gSetting.setAutoCreateQueues(true);
//			<auto-create-addresses>true</auto-create-addresses>
			gSetting.setAutoCreateAddresses(true);
//			<auto-delete-queues>false</auto-delete-queues>
			gSetting.setAutoDeleteQueues(false);
//			<auto-delete-addresses>false</auto-delete-addresses>
			gSetting.setAutoDeleteAddresses(false);
//			<!-- The size of each page file -->
//			<page-size-bytes>10M</page-size-bytes>
			gSetting.setPageSizeBytes(10 * 1024 * 1024);
//			<!-- When we start applying the address-full-policy, e.g paging -->
//			<!-- Both are disabled by default, which means we will use the global-max-size/global-max-messages -->
//			<max-size-bytes>-1</max-size-bytes>
			gSetting.setMaxSizeBytes(-1);
//			<max-size-messages>-1</max-size-messages>
			gSetting.setMaxSizeMessages(-1);
//			<!-- When we read from paging into queues (memory) -->
//			<max-read-page-messages>-1</max-read-page-messages>
			gSetting.setMaxReadPageBytes(-1);
//			<max-read-page-bytes>20M</max-read-page-bytes>
			gSetting.setMaxReadPageBytes(20 * 1024 * 1024);
//			<!-- Limit on paging capacity before starting to throw errors -->
//			<page-limit-bytes>-1</page-limit-bytes>
			gSetting.setPageLimitBytes(-1L);
//			<page-limit-messages>-1</page-limit-messages>
			gSetting.setPageLimitMessages(-1L);
//		</address-setting>

		} catch (Exception e) {
			LOGGER.error("Error en la configuracion del Acceptor", e);
			throw new UnsupportedOperationException(e);
		}
		try {
			broker.start();
		} catch (Exception e) {
			LOGGER.error("Error en arranque del broker", e);
			throw new UnsupportedOperationException(e);
		}
		return broker;
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
