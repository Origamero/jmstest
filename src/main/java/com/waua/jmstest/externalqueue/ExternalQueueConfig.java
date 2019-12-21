package com.waua.jmstest.externalqueue;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("external-queue")
public class ExternalQueueConfig {

	private List<QueueConfig> datasources;
	
	@Data
	public static class QueueConfig {

	    private boolean enabled;
	    private String wlsJmsUrl;
	    private String wlsJndi;
	    private String factoryJndi;
	    private String queueJndi;
	    private String wlsUser;
	    private String wlsPassword;
	    private String dtoName;
	    private String implName;
	}
}
