package com.farmafene.commons.tx.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources( //
		value = { //
				@PropertySource(//
						value = { //
								"classpath:com/farmafene/commons/tx/cfg/values.properties"//
						}, //
						ignoreResourceNotFound = true //
				) //
		} //
) //
public class CargaProps {
}
