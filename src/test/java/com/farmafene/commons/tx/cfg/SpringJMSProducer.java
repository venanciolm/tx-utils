package com.farmafene.commons.tx.cfg;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.JMSException;
import jakarta.transaction.Transactional;

public class SpringJMSProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(JMSProducer.class);
	private static final String HORA_STANDARD = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	@Value("${activemq.conection.queue}")
	private String queue;
	@Autowired
	private JmsTemplate jmsCF;

	@Autowired
	private DSBean dsbean;

	@Transactional(rollbackOn = Throwable.class)
	public String sendMessage() throws JMSException, SQLException {
		String msgStr = "Hola: " + new SimpleDateFormat(HORA_STANDARD).format(new Date());
		jmsCF.convertAndSend(queue, msgStr);
		StringBuilder sb = new StringBuilder();
		String lf = System.lineSeparator();
		sb.append(lf).append("/+---------------------------------------+");
		sb.append(lf).append(" | Producer:");
		sb.append(lf).append(" +---------------------------------------+");
		sb.append(lf).append(" | Queue: ").append(queue);
		sb.append(lf).append(" | Msg:   ").append(msgStr);
		sb.append(lf).append(" +---------------------------------------+");
		LOGGER.info("{}", sb);
		dsbean.selectFromdual();
		return msgStr;
	}
}
