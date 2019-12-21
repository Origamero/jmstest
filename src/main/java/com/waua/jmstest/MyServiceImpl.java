package com.waua.jmstest;

import java.sql.SQLException;

import javax.jms.JMSException;

import org.springframework.stereotype.Service;;

@Service
public class MyServiceImpl implements MyService {

	@Override
	public void receiveMessage(Object aqJMSMessage) throws JMSException, SQLException {
		System.out.println("el implement");
	}

}
