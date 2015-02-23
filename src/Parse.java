import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


//Jack Jamieson 2015
//This class handles the parsing of KML files using the JAK library.  Also requires Apache commons io.
//This does not have anything to do with the GUI representation of the file breakdown.
public class Parse {

	private Kml kml;
	private Document document;
	private Document origDoc;
	private Folder marks;
	private Folder groups;
	private Folder ages;
	private Folder agesWithLithic;

	public Parse() {

	}

	//Read in the KML
	public Parse(InputStream is, boolean isKMZ) {


		String str;
		try {
			str = IOUtils.toString(is);

			IOUtils.closeQuietly(is);
			str = str
					.replace(
							"xmlns=\"http://earth.google.com/kml/2.2\"",
							"xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\"");
			ByteArrayInputStream bais = new ByteArrayInputStream(
					str.getBytes("UTF-8"));
			//The JAK library cannot read the old GE KML header so we have to replace it before reading.

			//KMZ files are a whole nother mess, just don't read them.
			if (!isKMZ) {
				kml = Kml.unmarshal(bais);
				document = (Document) kml.getFeature();
				origDoc = (Document) kml.getFeature();
			}
			else {
				JOptionPane.showMessageDialog(null, "KMZ files are not supported.", "Error", 2);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	

	}

	public String getDocumentName() {

		return document.getName();

	}

	public String getDocumentDescription() {

		return document.getDescription();
	}

	public List<Feature> getFeatures() {

		List<Feature> t = (List<Feature>) document.getFeature();

		return t;

	}

	//Create new folders in the in-memory KML
	public void createSepFolders() {

		marks = document.createAndAddFolder();
		marks.setName("Features");

		groups = document.createAndAddFolder();
		groups.setName("Lithic Groups");

		ages = document.createAndAddFolder();
		ages.setName("Age");
		
		agesWithLithic = document.createAndAddFolder();
		agesWithLithic.setName("Ages with Lithic Subgroups");

	}

	public void deleteSepFolders() {
		//document.getFeature().remove(marks);
		
		document.getFeature().remove(groups);
		document.getFeature().remove(ages);
		document.getFeature().remove(agesWithLithic);

	}

	public Folder addLithicGroupFolders(String folderName) {
		// D
		Folder folder = groups.createAndAddFolder();
		folder.setName(folderName);

		return folder;
		

	}

	public Folder addAgeFolders(String folderName) {

		Folder folder = ages.createAndAddFolder();
		folder.setName(folderName);

		return folder;
	}
	
	public Folder addAgeLithicFolders(String folderName) {

		Folder folder = agesWithLithic.createAndAddFolder();
		folder.setName(folderName);

		return folder;
	}

	public void addToFolder(Folder folder, Placemark placemark) {
		folder.getFeature().add(placemark);
		marks.getFeature().add(placemark);
		document.getFeature().remove(placemark);

	}
	
	public void addToFolder(Folder folder, Folder folder2) {
		folder.getFeature().add(folder2);
		
	}

	public void addToFolderNoDelete(Folder folder, Placemark placemark) {
		folder.getFeature().add(placemark);

	}

	public void reWriteKML(String fileName) {

		try {
			kml.marshal(new File(fileName));
			//toOrig();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void reWriteKMZ(String fileName) {

		try {
			kml.marshalAsKmz(fileName, kml);
			// kml.marshal(new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void toOrig(){
		document = origDoc;
	}

}