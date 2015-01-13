import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


public class Parse {
	
	private Kml kml;
	private Document document;
	
	public Parse(){
		
		kml = Kml.unmarshal(new File("rig2.kml"));
		document = (Document) kml.getFeature();
	}

	
	public String getDocumentName(){
		
		
		return document.getName();
	
	}
	
	public String getDocumentDescription(){
		
		return document.getDescription();
	}


	public List<Feature> getFeatures()
	{

	
	
			List<Feature> t = (List<Feature>) document.getFeature();
			
			return t;
	
	}
	
	public Folder addLithicGroupFolders(String folderName){
		//D
		Folder folder = document.createAndAddFolder();
		folder.setName(folderName);
		
		return folder;
		//folder.
		
	}
	
	public void addToFolder(Folder folder, Placemark placemark){
		
		folder.getFeature().add(placemark);
		
		
	}
	
	public void cleanUp(){
		
		//Folder old = document.createAndAddFolder();
		//old.setName("Old");
		
		//while()
		
		
		
	}
	
	
	
	
	
	public void reWrite(){
		
		try {
			kml.marshal(new File("rig3.kml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}