import java.util.Scanner;

import javax.jms.Connection ;
import javax.jms.ConnectionFactory ;
import javax.jms.Destination ;
import javax.jms.JMSException;
import javax.jms.Message ;
import javax.jms.MessageConsumer ;
import javax.jms.MessageProducer;
import javax.jms.Session ;
import javax.jms.TextMessage ;
import javax.naming.InitialContext;


public class Service {
	
	private ConnectionFactory connectionFactory;
	private Destination destination1;
	private Destination destination2;
	
	public Connection connection;
	
	public static void main(String[] args) {
		try {
			Service service = new Service();
			service.connect();
			service.messaging();
		} catch (Throwable t) {
			t.printStackTrace() ;
		}
	}

	public String reverse(String msg) {
		StringBuilder sb = new StringBuilder(msg);
		String rslt = sb.reverse().toString();
		return rslt;
	}
	
	private void connect() throws Exception {
	// Initialise les attributs connectionFactory et destination.
		InitialContext jndiContext = new InitialContext();
		connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
		connection = connectionFactory.createConnection();
		destination1  = (Destination) jndiContext.lookup("MyQueue");
		destination2 = (Destination) jndiContext.lookup("MyQueue");
	}
	
	private void messaging() throws JMSException {
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		while(true) {
			MessageConsumer mc = session.createConsumer(destination1);

			Message m = mc.receive(0);
			
			if (m instanceof TextMessage) {
				String txt = ((TextMessage) m).getText();
				String msg = reverse(txt); // traiterMessage( m : TextMessage ) : void
				TextMessage message = session.createTextMessage(msg);
				MessageProducer mp = session.createProducer(destination2);
				mp.send(message);
				message.setJMSCorrelationID(m.getJMSMessageID());
			}
			else {
				break;
			}
			mc.close();
		}
	}
	
	//connection

	
}
