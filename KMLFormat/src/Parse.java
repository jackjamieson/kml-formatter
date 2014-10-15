import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Parse {

	NodeList placemarks;
	
	public void createTemplate(){
		
		try {
			PrintWriter out = new PrintWriter("template.kml");
			
			String template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n<Folder>\n\u0009<name>Units template empty</name>\n\u0009<Folder>\n\u0009\u0009<name>Amphibolite</name>\n\u0009\u0009<visibility>0</visibility>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Argillite and hornfels</name>\n\u0009\u0009<visibility>0</visibility>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Carbonates</name>\n\u0009\u0009<visibility>0</visibility>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Felsic intrusives and granofels</name>\n\u0009\u0009<visibility>0</visibility>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Felsic extrusives</name>\n\u0009\u0009<visibility>0</visibility>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Gneiss</name>\n\u0009\u0009<visibility>0</visibility>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Granofels</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Intermediate intrusives</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Intermediate extrusives</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Jurassic plutonic</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Jurassic sedimentary</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Jurassic volcanic</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Marble and calcareous sedimentary</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Mafic extrusives and metavolcanics</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Mafic intrusives</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Mylonite and tectonite</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Sandstone</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Schist</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Shale</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Siltstone</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Slate</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Triassic sedimentary</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Quartzite</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Ultramafics</name>\n\u0009</Folder>\n\u0009<Folder>\n\u0009\u0009<name>Unassigned</name>\n\u0009</Folder>\n</Folder>\n</kml>";
			
			out.println(template);
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

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

		DOMSource source = new DOMSource(doc);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StreamResult result = new StreamResult("rigeol.kml");
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create the folder xml structure here
	}

	public void doit()
	{

		try {
			//Parse p = new Parse();
			//p.parse("mydocument.xml");

			Document doc = build("rigeol.kml");
			createTemplate();
			//addEmptyFolder(doc);
			//System.out.println(doc.getTextContent());
			//addEmptyFolder(doc);

			Element rootElement = doc.getDocumentElement();
			//	Element folder = doc.createElement("Folder");
			//rootElement.appendChild(folder);
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
									child.normalize();
									//child.
									String result = childb.getTextContent().substring(childb.getTextContent().indexOf("#")+1, childb.getTextContent().length());
									//	if(result.contains("horn"))
									System.out.println(result);

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