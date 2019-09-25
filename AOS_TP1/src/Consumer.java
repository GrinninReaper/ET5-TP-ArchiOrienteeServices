import javax.jms.Connection ;
import javax.jms.ConnectionFactory ;
import javax.jms.Destination ;
import javax.jms.JMSException;
import javax.jms.Message ;
import javax.jms.MessageConsumer ;
import javax.jms.Session ;
import javax.jms.TextMessage ;
import javax.naming.InitialContext;

public class Consumer {

	private ConnectionFactory connectionFactory;
	private Destination destination;
	
	public Connection connection;
	public Session session;
	
	public static void main(String[] args) {
		try {
			Consumer consumer = new Consumer();
			consumer.connect();
			consumer.waitForMessage();
		} catch (Throwable t) {
			t.printStackTrace() ;
		}
	}
	
	private void waitForMessage() throws Exception {
	// Créer une connexion au système de messagerie
	// Et affiche les messages au fur et à mesure de leur arrivée dans la queue
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Réception des messages jusqu’à obtention d’un message non texte
		while (true) {
			MessageConsumer mc = this.session.createConsumer(this.destination);
			Message m = mc.receive(0);
			if (m instanceof TextMessage) {
				this.traiterMessage(m); // traiterMessage( m : TextMessage ) : void
			} else {
				break;
			}
		}
	}
	
	private void connect() throws Exception {
	// Initialise les attributs connectionFactory et destination.
		InitialContext jndiContext = new InitialContext();
		connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
		destination  = (Destination) jndiContext.lookup("MyQueue");
		connection = connectionFactory.createConnection();
	}
	
	public void traiterMessage(Message m) throws JMSException {
		TextMessage tm = (TextMessage) m;
		if(tm.getText().equals("QUIT")) {
			this.connection.close();
			System.exit(1);
		}
		else {
			System.out.println(" Le message est:");
			System.out.println(tm.getText());
		}
			
	}
}
