package com.farmafene.commons.tx.cfg;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;

public class DSBean {

	@Autowired
	private DS1Bean ds1bean;
	@Autowired
	private DS2Bean ds2bean;
	private CountDownLatch latch = null;

	@Transactional(rollbackOn = Throwable.class)
	public String selectFromdual() throws SQLException {
		return String.format("[%1$s, %2$s]", ds1bean.selectFromdual(), ds2bean.selectFromdual());
	}

	/**
	 * @return the latch
	 */
	public CountDownLatch getLatch() {
		return latch;
	}

	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
}
