package com.active.services.redeemer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Bootstrap implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);
	
	@Override
	public void setApplicationContext(ApplicationContext cxt) throws BeansException {
		LOG.info("Waiting for redeemer...");
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) cxt;
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Initializer.class);
		builder.addConstructorArgValue(cxt);
		builder.setScope("singleton");
		beanFactory.registerBeanDefinition("redeemerInitializer", builder.getBeanDefinition());
		LOG.info("Redeemer is come back!");
	}
	
}
