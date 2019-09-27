import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConsumer;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.impl.DefaultCamelContext;

public class Client {
	
	
	public class MyListener implements MessageListener{

		@Override
		public void onMessage(Message arg0) {
			// TODO Auto-generated method stub
			if(arg0 instanceof TextMessage) {
				try {
					if(arg0.getJMSCorrelationID() == msgID) {
						System.out.print("Réponse reçue");
						System.out.println("--> " + ((TextMessage) arg0).getText());
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
			destination1  = (Destination) jndiContext.lookup("MyRequest");
			destination2  = (Destination) jndiContext.lookup("MyResponse");
		}
	
	private void messaging() throws Exception {
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		MessageConsumer mc = session.createConsumer(this.destination2);
		
		MyListener listener = new MyListener();
		
		mc.setMessageListener(listener);
		
		String buffer = null;
		while(true) {
			MessageProducer mp = session.createProducer(destination1);
			System.out.println("Taper le message:");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(input != null) {
				TextMessage message = session.createTextMessage(input);
				msgID = message.getJMSMessageID();
				mp.send(message);
				System.out.println("Requête envoyée, ID " + message.getJMSMessageID() );
				buffer = null;
				if(input.equals("QUIT"))
					break;
			}
		}
		
		System.exit(101);
	}
	
}
