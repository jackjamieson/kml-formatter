import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


public class Parse {
	
	private Kml kml;
	private Document document;
	private Folder marks;
	private Folder groups;
	private Folder ages;
	
	public Parse(){
		
		
	}
	
	public Parse(InputStream is) throws IOException{
		
		//String kml = "";
		//try {
			//Scanner scan = new Scanner(is);
			//String first = scan.nextLine();
			//if(first.contains("xmlns=\"http://earth.google.com/kml/2.2\"")){
				String str = IOUtils.toString( is );
			    IOUtils.closeQuietly( is );
			    str = str.replace("xmlns=\"http://earth.google.com/kml/2.2\"", "xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\"" );
			    ByteArrayInputStream bais = new ByteArrayInputStream( str.getBytes( "UTF-8" ) );
			    
			    kml = Kml.unmarshal(bais);
				document = (Document) kml.getFeature();
				
				
				
				
				
			//}
			//else{
			//	
			//	kml = Kml.unmarshal(is);
			//	document = (Document) kml.getFeature();
			//}
			
			
		//} catch (FileNotFoundException e) {
			
			
		//	e.printStackTrace();
		//}
		
		
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
	
	public void createSepFolders(){
		
		marks = document.createAndAddFolder();
		marks.setName("Features");
		
		groups = document.createAndAddFolder();
		groups.setName("Lithic Groups");
		
		ages = document.createAndAddFolder();
		ages.setName("Age");
		
		
	}
	
	public void deleteSepFolders() {
		document.getFeature().remove(marks);
		document.getFeature().remove(groups);
		document.getFeature().remove(ages);

	}
	
	public Folder addLithicGroupFolders(String folderName){
		//D
		Folder folder = groups.createAndAddFolder();
		folder.setName(folderName);
		
		return folder;
		//folder.
		
	}
	
	public Folder addAgeFolders(String folderName) {
		
		Folder folder = ages.createAndAddFolder();
		folder.setName(folderName);
		
		return folder;
	}
	
	public void addToFolder(Folder folder, Placemark placemark){
		folder.getFeature().add(placemark);
		marks.getFeature().add(placemark);
		document.getFeature().remove(placemark);

		
	}
	
	public void addToFolderNoDelete(Folder folder, Placemark placemark) {
		folder.getFeature().add(placemark);

	}
	
	public void cleanUp(List<Feature> features){
		
		Object[] ft = features.toArray();
		for(Object feature : ft){
			if((feature instanceof Folder)){
			//	((Folder)feature).getFeature().
				
			}
			
			
		}
		
		
		
	}
	
	
	
	
	
	public void reWrite(String fileName){
		
		try {
			kml.marshal(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}