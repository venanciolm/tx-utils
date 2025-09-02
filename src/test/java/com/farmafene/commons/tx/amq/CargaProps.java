package com.farmafene.commons.tx.amq;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources( //
		value = { //
				@PropertySource(//
						value = { //
								"classpath:com/farmafene/commons/tx/amq/vm.properties"//
						}, //
						ignoreResourceNotFound = true //
				) //
		} //
) //
public class CargaProps {
}
