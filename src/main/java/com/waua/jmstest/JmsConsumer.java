package com.waua.jmstest;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsConsumer implements MessageListener {

	private Hashtable<String, String> wlsEnvParamHashTbl = null;
	private QueueConnection queueConnection = null;
	private QueueSession queueSession = null;
	private QueueReceiver queueReceiver = null;

	private String wlsJmsUrl = null;
	private String wlsJndi = null;
	private String jmsFactory = null;
	private String jndiQueue = null;
	private String trlWlsUser = null;
	private String trlWlsPassword = null;

	private Type type = null;

	private MyService service;

	public JmsConsumer(String wlsJmsUrl, String wlsJndi, String trlWlsUser, String trlWlsPassword) {

		this.wlsJmsUrl = wlsJmsUrl;
		this.wlsJndi = wlsJndi;
		this.trlWlsUser = trlWlsUser;
		this.trlWlsPassword = trlWlsPassword;

		wlsEnvParamHashTbl = new Hashtable<>();
		wlsEnvParamHashTbl.put(Context.PROVIDER_URL, wlsJmsUrl);
		wlsEnvParamHashTbl.put(Context.INITIAL_CONTEXT_FACTORY, wlsJndi);
		wlsEnvParamHashTbl.put(Context.SECURITY_PRINCIPAL, trlWlsUser);
		wlsEnvParamHashTbl.put(Context.SECURITY_CREDENTIALS, trlWlsPassword);

	}

	public void setService(MyService srv) {
		service = srv;
	}

	public void initializeConnParams(String jmsFactory, String jndiQueue, Type type)
			throws NamingException, JMSException {

		this.type = type;
		InitialContext initialContext = new InitialContext(wlsEnvParamHashTbl);
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(jmsFactory);
		queueConnection = queueConnectionFactory.createQueueConnection(trlWlsUser, trlWlsPassword);
		queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = (Queue) initialContext.lookup(jndiQueue);
		queueReceiver = queueSession.createReceiver(queue);
		queueReceiver.setMessageListener(this);
		queueConnection.start();
	}

	/**
	 * onMessage() listener from MessageListener class to read messages
	 */
	@Override
	public void onMessage(Message message) {

		try {
			service.receiveMessage(ConvertUtils.jsonToType(((TextMessage) message).getText(), type));

		} catch (JMSException | SQLException e) {
			try {
				queueSession.rollback();
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
	}

	public QueueReceiver getReceiver() {
		return queueReceiver;
	}

	/**
	 * This method closes all connections
	 * 
	 * @throws JMSException
	 */
	public void close() throws JMSException {

		queueReceiver.close();
		queueSession.close();
		queueConnection.close();
	}
}
