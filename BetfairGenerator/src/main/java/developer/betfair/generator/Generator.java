package developer.betfair.generator;

import java.io.FileOutputStream;
import java.io.IOException;

public class Generator {

	FileOutputStream fouth;
	FileOutputStream foutc;
	
	public Generator() {
	}
	
	public static void jaxbTransform(String[] args) {       
        try {
            // Get the example into an object we can send
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Interface.class.getPackage().getName());
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            String model = "SportsAPING.xml";
            String api="betting";
            String genType = "R";

            if (args.length == 0) {
            	System.err.println("Model:"+model + " genType:" + genType);
            }
            
            if (args.length == 1) {
                model = args[0];
            	System.err.println("Model:"+model + " genType:" + genType);
            }
            
            if (args.length == 2) {
                model = args[0];
                genType = args[1];
            }

            if (model.equals("HeartbeatAPING.xml")) {
            	api="heartbeat";
            }
            else if (model.equals("AccountAPING.xml")) {
            	api="account";
            }
            
            Interface xmlInterface = (Interface) unmarshaller.unmarshal(new java.io.File("xml-resources/"+model)); //NOI18N
            switch (genType) {
                case "R":
                    new RGenerator(xmlInterface,"R/",api);
                    break;
                case "Cpp":
                    break;
                default:
                    System.err.println("Not supported generation type"+genType);
                    break;
            }
        } catch (javax.xml.bind.JAXBException | IOException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
	}
	
	public static void main(String[] args) {
		jaxbTransform(args);
	}

}
