package com.farmafene.commons.tx.cfg;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.transaction.Transactional;

public class SpringMessageListener implements MessageListener {

	private static final Logger LOG = LoggerFactory.getLogger(JMSConsumer.class);
	@Autowired
	private DSBean dsbean;
	@Value("${activemq.conection.queue}")
	private String queue;

	/**
	 * 
	 * @see jakarta.jms.MessageListener#onMessage(jakarta.jms.Message)
	 */
	@Override
	@Transactional(rollbackOn = Throwable.class)
	public void onMessage(Message message) {
		if (TextMessage.class.isAssignableFrom(message.getClass())) {
			TextMessage msg = (TextMessage) message;
			try {
				StringBuilder sb = new StringBuilder();
				String lf = System.lineSeparator();
				sb.append(lf).append("/+---------------------------------------+");
				sb.append(lf).append(" | Consumer:");
				sb.append(lf).append(" +---------------------------------------+");
				sb.append(lf).append(" | Queue: ").append(queue);
				sb.append(lf).append(" | Msg:   ").append(msg.getText());
				sb.append(lf).append(" +---------------------------------------+");
				LOG.info("{}", sb);
				dsbean.selectFromdual();
				dsbean.getLatch().countDown();
			} catch (JMSException e) {
				throw new UnsupportedOperationException("Mensaje invalido", e);
			} catch (SQLException e) {
				throw new UnsupportedOperationException("Mensaje invalido", e);
			} catch (Exception e) {
				throw new UnsupportedOperationException("Mensaje invalido", e);
			}
			// throw new UnsupportedOperationException("Mensaje invalido");
		} else {
			throw new UnsupportedOperationException("Mensaje invalido");
		}
	}

}
