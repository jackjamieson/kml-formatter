import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Parse {

	NodeList placemarks;

	public void addEmptyFolder(Document doc)
	{
		Element root = doc.getDocumentElement();

		/*
		NodeList nodes = root.getChildNodes();
		//find the document node
		for(int i=0; i<nodes.getLength(); i++){
			Node node = nodes.item(i);

			if(node instanceof Element){
				//a child element to process
				Element child = (Element) node;
				String attribute = child.getNodeName();

				if(attribute != "Folder")
				{
					
				}
			}
		}*/
		
		Element folder = doc.createElement("Folder");
		root.appendChild(folder);
		
		Element name = doc.createElement("name");
		//create the folder xml structure here
	}

	public void doit()
	{

		try {
			//Parse p = new Parse();
			//p.parse("mydocument.xml");

			Document doc = build("rigeol.kml");

			Element rootElement = doc.getDocumentElement();
			//  Element items = 
			// Get the document's root XML node
			//NodeList root = doc.getChildNodes();

			// Node pl = getNode("Document", root);
			//Node u = getNode("Placemark", pl.getChildNodes());

			// String res = getNodeValue(pl);

			NodeList nodes = rootElement.getChildNodes();
			//find the document node
			for(int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);

				if(node instanceof Element){
					//a child element to process
					Element child = (Element) node;
					String attribute = child.getNodeName();
					//System.out.println(attribute);
					placemarks = child.getChildNodes();
				}
			}

			//Get the children of document
			for(int j=0; j<placemarks.getLength(); j++){
				Node node2 = placemarks.item(j);

				if(node2 instanceof Element){
					//a child element to process
					Element child = (Element) node2;
					String attribute = child.getNodeName();
					if(attribute.toString() == "Placemark")
					{
						NodeList placemarknodes = node2.getChildNodes();
						//find the document node
						for(int i=0; i<placemarknodes.getLength(); i++){
							Node node = placemarknodes.item(i);

							if(node instanceof Element){
								//a child element to process
								Element childb = (Element) node;
								String attributeb = childb.getNodeName();
								if(attributeb == "styleUrl")
								{
									//System.out.println("test");
									System.out.println(childb.getTextContent());

								}
								
							}
						}	
						//System.out.println(att);
					}
					//placemarks = child.getChildNodes();
				}
			}

			// Navigate down the hierarchy to get to the CEO node
			/*		    Node comp = getNode("Company", root);
		   //Node exec = getNode("Executive", comp.getChildNodes() );
		    //String execType = getNodeAttr("type", exec);

		    // Load the executive's data from the XML
		   // NodeList nodes = exec.getChildNodes();
		    String lastName = getNodeValue("LastName", nodes);
		    String firstName = getNodeValue("FirstName", nodes);
		    String street = getNodeValue("street", nodes);
		    String city = getNodeValue("city", nodes);
		    String state = getNodeValue("state", nodes);
		    String zip = getNodeValue("zip", nodes);

		    System.out.println("Executive Information:");
		    System.out.println("Type: " + execType);
		    System.out.println(lastName + ", " + firstName);
		    System.out.println(street);
		    System.out.println(city + ", " + state + " " + zip);*/
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public Document build(String file) 
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();



		//Using factory get an instance of document builder
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			dbf.setNamespaceAware(true);
			//parse using builder to get DOM representation of the XML file
			Document doc = db.parse(file);

			return doc;

		}
		catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return null;




	}


}