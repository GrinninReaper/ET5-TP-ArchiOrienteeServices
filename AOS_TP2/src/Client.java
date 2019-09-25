import java.util.Scanner;

import javax.jms.Connection ;
import javax.jms.ConnectionFactory ;
import javax.jms.Destination ;
import javax.jms.JMSException;
import javax.jms.Message ;
import javax.jms.MessageConsumer ;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session ;
import javax.jms.TextMessage ;
import javax.naming.InitialContext;

public class Client {
	
	
	public class MyListener implements MessageListener{

		@Override
		public void onMessage(Message arg0) {
			// TODO Auto-generated method stub
			if(arg0 instanceof TextMessage) {
				try {
					if(arg0.getJMSCorrelationID() == msgID) {
						System.out.println("In if");
						System.out.println(((TextMessage) arg0).getText());
					}
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	private ConnectionFactory connectionFactory;
	private Destination destination1;
	private Destination destination2;
	private Connection connection;
	
	public static String msgID;
	
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.connect();
			client.messaging();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void connect() throws Exception {
		// Initialise les attributs connectionFactory et destination.
			InitialContext jndiContext = new InitialContext();
			connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
			connection = connectionFactory.createConnection();
			destination1  = (Destination) jndiContext.lookup("MyQueue");
			destination2  = (Destination) jndiContext.lookup("MyQueue");
		}
	
	private void messaging() throws JMSException {
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		MessageConsumer mc = session.createConsumer(this.destination1);
		
		MyListener listener = new MyListener();
		
		mc.setMessageListener(listener);
		
		String buffer = null;
		while(true) {
			MessageProducer mp = session.createProducer(destination2);
			System.out.println("Taper le message:");
			Scanner sc = new Scanner(System.in);
			buffer = sc.next();
			if(buffer != null) {
				TextMessage message = session.createTextMessage(buffer);
				msgID = message.getJMSMessageID();
				mp.send(message);
			}
		}
	}
	
}
