package com.farmafene.commons.tx.cfg;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

public class JMSConsumer implements MessageListener {

	private static final Logger LOG = LoggerFactory.getLogger(JMSConsumer.class);
	private DSBean dsbean;
	private String queue;

	public JMSConsumer(DSBean bean, String queue) {
		this.dsbean = bean;
		this.queue = queue;
	}

	@Override
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
				if (dsbean != null) {
					dsbean.selectFromdual();
					if (dsbean.getLatch() != null) {
						dsbean.getLatch().countDown();
					}
				}
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
