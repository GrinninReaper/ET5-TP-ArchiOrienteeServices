import java.util.Scanner ;
import javax.jms.Connection ;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination ;
import javax.jms.MessageProducer ;
import javax.jms.Session ;
import javax.jms.TextMessage ;
import javax.naming.InitialContext;

public class Producer {
	private ConnectionFactory connectionFactory;
	private Destination destination;
	
	private Connection connection;
	
	public static void main(String[] args) {
		try {
			Producer producer = new Producer();
			producer.connect();
			producer.sendMessages();
		} catch (Throwable t) {
		t.printStackTrace();
		}
	}
	
	private void sendMessages() throws Exception {
	// Créer une connexion au système de messagerie
	// Emet des messages au fur et à mesure que l’utilisateur les saisit
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		String buffer = null;
		while(true) {
			System.out.println("Taper le message:");
			Scanner sc = new Scanner(System.in);
			buffer = sc.next();
			//String buffer = "hello";
			if(buffer != null) {
				MessageProducer messageProducer = session.createProducer(destination);
				TextMessage message = session.createTextMessage(buffer);
				messageProducer.send(message);
				System.out.println("Message sent");
			}
		}
	}
	
	private void connect() throws Exception {
	// Initialise les attributs connectionFactory et destination.
		InitialContext jndiContext = new InitialContext();
		connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
		connection = connectionFactory.createConnection();
		destination  = (Destination) jndiContext.lookup("MyQueue");
	}
}
