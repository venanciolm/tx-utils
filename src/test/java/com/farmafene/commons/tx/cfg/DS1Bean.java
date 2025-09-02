package com.farmafene.commons.tx.cfg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.transaction.Transactional;

public class DS1Bean {
	@Autowired
	@Qualifier("DS1")
	DataSource ds;

	@Transactional(rollbackOn = Throwable.class)
	public String selectFromdual() throws SQLException {
		String out = "-1";
		Connection con = ds.getConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT 'HORA: '||CURRENT_TIMESTAMP from INFORMATION_SCHEMA.SYSTEM_USERS");
		if (rs.next()) {
			out = rs.getString(1);
		}
		rs.close();
		st.close();
		return out;
	}
}
