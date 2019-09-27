import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFileMessage;
import org.apache.camel.impl.DefaultCamelContext;


public class MainClass {
	

	public static void main(String[] args) {
		try {
			CamelContext context = new DefaultCamelContext();
			context.addRoutes(new RouteBuilder() {
			public void configure() throws Exception {
					System.out.print("In configure");
					from("file:C:\\temp\\in?noop=true")
						.log("Looking for files")
						.process(new Processor() {
							public void process(Exchange e) throws Exception {
								System.out.println("In process");
								GenericFileMessage<File> fileIn =
								(GenericFileMessage<File>) e.getIn();
								System.out.println("Echange reçu : " + fileIn);
								System.out.println("Content is: " + e.getIn().getBody(String.class));
							}
					})
					.to("file:C:\\temp\\out")
					.log("File copied");
				}
			});
			context.start();
			while(true) {
				String buffer;
				Scanner sc = new Scanner(System.in);
				buffer = sc.next();
				if(buffer != null) {
					if(buffer != "QUIT") {
						break;
					}
				}
				else {
					Thread.sleep(2000);
				}
			}
			// ajouter ici un code permettant de pauser le thread courant
			context.stop();
		} catch (Throwable t) {
			t.printStackTrace();
			}
		}
	
}
