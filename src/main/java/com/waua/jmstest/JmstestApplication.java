package com.waua.jmstest;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.waua.jmstest.externalqueue.ExternalQueueConfig;
import com.waua.jmstest.externalqueue.ExternalQueueConfig.QueueConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({ ExternalQueueConfig.class })
public class JmstestApplication {

	private final Environment env;

	@Autowired
	ExternalQueueConfig exConf;

	public JmstestApplication(Environment env) {
		this.env = env;
	}

	public static void main(String[] args) {
		SpringApplication.run(JmstestApplication.class, args);

	}

	@Bean
	public boolean initExt() {
		log.debug("props: " + exConf.getDatasources().size());
		for (QueueConfig config : exConf.getDatasources().stream().filter(QueueConfig::isEnabled)
				.collect(Collectors.toList())) {
			try {
				JmsConsumer jmstrl = new JmsConsumer(config.getWlsJmsUrl(), config.getWlsJndi(), config.getWlsUser(),
						config.getWlsPassword());
				jmstrl.initializeConnParams(config.getFactoryJndi(), config.getQueueJndi(),
						(Type) Class.forName(config.getDtoName()));
				jmstrl.setService((MyService) Class.forName(config.getImplName()).newInstance());
				Thread thread = new Thread(() -> syncrohizeConsumer(jmstrl));
				thread.start();
			} catch (NamingException | JMSException | ClassNotFoundException | InstantiationException
					| IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

	public static void syncrohizeConsumer(JmsConsumer consumer) {
		try {
			synchronized (consumer) {
				while (true) {
					consumer.wait();
				}
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
