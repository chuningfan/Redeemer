package com.active.services.redeemer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

public class Bootstrap implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);
	
	@Override
	public void setApplicationContext(ApplicationContext cxt) throws BeansException {
		LOG.info("Waiting for redeemer...");
		ConfigurableApplicationContext cac = (ConfigurableApplicationContext) cxt;
		ConfigurableListableBeanFactory clbf = cac.getBeanFactory();
		clbf.registerSingleton("redeemerInitializer", new Initializer(cxt, cxt.getBean(MongoTemplate.class)));
		LOG.info("Redeemer is come back!");
	}
	
}
