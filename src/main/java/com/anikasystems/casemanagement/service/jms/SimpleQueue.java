package com.anikasystems.casemanagement.service.jms;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class SimpleQueue {

	private static final String CLIENTID = "CaseManagement";
	private String queueName;
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	private MessageConsumer consumer;
	
	public SimpleQueue(String queueName) throws Exception {
		super();
		// The name of the queue.
		this.queueName = queueName;
		// URL of the JMS server is required to create connection factory.
		// DEFAULT_BROKER_URL is : tcp://localhost:61616 and is indicates that JMS
		// server is running on localhost
		connectionFactory = new ActiveMQConnectionFactory("ssl://b-61353410-db56-416e-9bc5-b775ccd80d45-1.mq.us-east-1.amazonaws.com:61617");
		// Getting JMS connection from the server and starting it
		connection = connectionFactory.createConnection("admin", "admin1234567");
		// connection.setClientID(CLIENTID);
		connection.start();
		// Creating a non-transactional session to send/receive JMS message.
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Destination represents here our queue ’BankAccountProcessingQueue’ on the JMS
		// server.
		// The queue will be created automatically on the JSM server if its not already
		// created.
		destination = session.createQueue(this.queueName);
		// MessageProducer is used for sending (producing) messages to the queue.
		producer = session.createProducer(destination);
		// MessageConsumer is used for receiving (consuming) messages from the queue.
		consumer = session.createConsumer(destination);
	}

	public void send(String textMessage)
			throws Exception {
		// We will send a text message 
		TextMessage message = session.createTextMessage(textMessage);
		// push the message into queue
		producer.send(message);
		System.out.printf("'%s' text message sent to the queue '%s' running on local JMS Server.\n", message, queueName);
	}
	
	public void receive() throws Exception {
		// receive the message from the queue.
		Message message = consumer.receive();
		// Since We are using TestMessage in our example. MessageProducer sent us a TextMessage
		// So we need cast to it to get access to its getText() method which will give us the text of the message
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			System.out.printf("Received message '%s' from the queue '%s' running on local JMS Server.\n", textMessage.getText(), queueName);
		}
	}

	public void close() throws JMSException {
		producer.close();
		producer = null;
		consumer.close();
		consumer = null;
		session.close();
		session = null;
		connection.close();
		connection = null;
	}
}