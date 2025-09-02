package com.farmafene.commons.tx.cfg;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.geronimo.connector.outbound.GenericConnectionManager;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.LocalTransactions;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.NoPool;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTrackingCoordinator;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tranql.connector.jdbc.XADataSourceWrapper;

import com.farmafene.commons.tx.jdbc.XADatasourceFromContainerDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.resource.ResourceException;

@Configuration
public class DataSourceBeans {

	@Bean("HDS1")
	HikariDataSource getDataSorurce01() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:hsqldb:mem:target/db1");
		config.setUsername("sa");
		config.setPassword("");
		config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
//	    config.addDataSourceProperty( "cachePrepStmts" , "true" );
//	    config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
//	    config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
		config.setMinimumIdle(1);
		config.setMaximumPoolSize(20);
		config.setPoolName("DS1");
		HikariDataSource hDs = new HikariDataSource(config);
		return hDs;
	}

	@Bean
	DisposableBean getHikariDataSourceDisposableBean1(//
			@Autowired @Qualifier("HDS1") HikariDataSource hDs) {
		return new DisposableBean() {
			/**
			 * 
			 * @see org.springframework.beans.factory.DisposableBean#destroy()
			 */
			@Override
			public void destroy() throws Exception {
				hDs.close();
			}
		};
	}

	@Bean("HDS2")
	HikariDataSource getDataSorurce02() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:hsqldb:mem:target/db2");
		config.setUsername("sa");
		config.setPassword("");
		config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
//	    config.addDataSourceProperty( "cachePrepStmts" , "true" );
//	    config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
//	    config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
		config.setMinimumIdle(1);
		config.setMaximumPoolSize(20);
		config.setPoolName("DS2");
		HikariDataSource hDs = new HikariDataSource(config);
		return hDs;
	}
	@Bean
	DisposableBean getHikariDataSourceDisposableBean2(//
			@Autowired @Qualifier("HDS2") HikariDataSource hDs) {
		return new DisposableBean() {
			/**
			 * 
			 * @see org.springframework.beans.factory.DisposableBean#destroy()
			 */
			@Override
			public void destroy() throws Exception {
				hDs.close();
			}
		};
	}

	@Bean("DS1")
	DataSource getManagedConnectionFactory1(//
			@Autowired GeronimoTransactionManager gtm, //
			@Autowired @Qualifier("HDS1") HikariDataSource hDs) throws ResourceException, ClassNotFoundException,
			IllegalAccessException, InstantiationException, SQLException {
		XADataSourceWrapper mcf = //
				new XADataSourceWrapper(//
						new XADatasourceFromContainerDataSource(hDs) //
				);
		mcf.setUserName(hDs.getUsername());
		mcf.setPassword(hDs.getPassword());
		GenericConnectionManager gcm = //
				new GenericConnectionManager( //
						LocalTransactions.INSTANCE, //
						new NoPool(), //
						null, //
						new ConnectionTrackingCoordinator(true), //
						gtm,
						//
						mcf, //
						"CM1", //
						/* getClass().getClassLoader() */null //
				);
		return (DataSource)mcf.createConnectionFactory(gcm);
	}

	@Bean("DS2")
	DataSource getManagedConnectionFactory2(//
			@Autowired GeronimoTransactionManager gtm, //
			@Autowired @Qualifier("HDS1") HikariDataSource hDs) throws ResourceException, ClassNotFoundException,
			IllegalAccessException, InstantiationException, SQLException {
		XADataSourceWrapper mcf = //
				new XADataSourceWrapper(//
						new XADatasourceFromContainerDataSource(hDs) //
				);
		mcf.setUserName(hDs.getUsername());
		mcf.setPassword(hDs.getPassword());
		GenericConnectionManager gcm = //
				new GenericConnectionManager( //
						LocalTransactions.INSTANCE, //
						new NoPool(), //
						null, //
						new ConnectionTrackingCoordinator(true), //
						gtm,
						//
						mcf, //
						"CM2", //
						/* getClass().getClassLoader() */null //
				);
		return (DataSource)mcf.createConnectionFactory(gcm);
	}

	@Bean
	DSBean getDSBean() {
		return new DSBean();
	}

	@Bean
	DS1Bean getDS1Bean() {
		return new DS1Bean();
	}

	@Bean
	DS2Bean getDS2Bean() {
		return new DS2Bean();
	}
}
