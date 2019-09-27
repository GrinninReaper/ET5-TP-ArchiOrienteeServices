import java.io.File;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFileMessage;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.component.jms.JmsMessage;

import javax.jms.TextMessage ;



public class CamelMain {
	
	private static boolean keepGoing = true;
	public static String request = null;

	public static void main(String[] args) {
		try {
			CamelContext context = new DefaultCamelContext();
			InitialContext jndiContext = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
			
			context.addComponent("jms-test", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
			context.addRoutes(new RouteBuilder() {
			public void configure() throws Exception {
					System.out.println("CamelContext lancé: En attente de message...");
					from("jms-test:fournisseur.Request")
						.process(new Processor() {
							public void process(Exchange e) throws Exception {
								JmsMessage message = (JmsMessage) e.getIn();
								String messageID = message.getMessageId();
								request = message.getBody(String.class);
								System.out.println("Requête reçue : " + request);
								
								if(request.equals("QUIT")) {
									keepGoing = false;
								}
								else {
									FournisseurService FS = new FournisseurService();
									String rslt = Float.toString(FS.getPrix(request));
									ProducerTemplate producer = context.createProducerTemplate();
									producer.sendBody("jms-test:fournisseur.Response", rslt);
									producer.stop();
								}
							}
					});
				}
			});
			
			context.start();
			if(request!= null) {
				
				//producer.sendBody("activemq:fournisseur.MyQueue", rslt);
				request = null;
			}
			while(keepGoing) {
				//System.out.println("Waiting");
				Thread.sleep(2000);
			}
			// ajouter ici un code permettant de pauser le thread courant
			context.stop();
		} catch (Throwable t) {
			t.printStackTrace();
			}
		}
	
}
