package com.waua.jmstest;

import java.sql.SQLException;

import javax.jms.JMSException;

import org.springframework.stereotype.Service;

@Service
public interface MyService {
	void receiveMessage(Object queue) throws JMSException, SQLException;
}
