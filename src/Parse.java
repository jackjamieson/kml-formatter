import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class Parse {

	private Kml kml;
	private Document document;
	private Folder marks;
	private Folder groups;
	private Folder ages;

	public Parse() {

	}

	public Parse(InputStream is, boolean isKMZ) {

		// String kml = "";
		// try {
		// Scanner scan = new Scanner(is);
		// String first = scan.nextLine();
		// if(first.contains("xmlns=\"http://earth.google.com/kml/2.2\"")){
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

			if (!isKMZ) {
				kml = Kml.unmarshal(bais);
				document = (Document) kml.getFeature();
			}
			else {
				/*
				File kmz = null;
				FileOutputStream fos = new FileOutputStream(kmz);
				 IOUtils.copy(bais, fos);

				 kml = Kml.unmarshalFromKmz(kmz);
				 */
				JOptionPane.showMessageDialog(null, "KMZ files are not supported.", "Error", 2);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// }
		// else{
		//
		// kml = Kml.unmarshal(is);
		// document = (Document) kml.getFeature();
		// }

		// } catch (FileNotFoundException e) {

		// e.printStackTrace();
		// }

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

	public void createSepFolders() {

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

	public Folder addLithicGroupFolders(String folderName) {
		// D
		Folder folder = groups.createAndAddFolder();
		folder.setName(folderName);

		return folder;
		// folder.

	}

	public Folder addAgeFolders(String folderName) {

		Folder folder = ages.createAndAddFolder();
		folder.setName(folderName);

		return folder;
	}

	public void addToFolder(Folder folder, Placemark placemark) {
		folder.getFeature().add(placemark);
		marks.getFeature().add(placemark);
		document.getFeature().remove(placemark);

	}

	public void addToFolderNoDelete(Folder folder, Placemark placemark) {
		folder.getFeature().add(placemark);

	}

	public void cleanUp(List<Feature> features) {

		Object[] ft = features.toArray();
		for (Object feature : ft) {
			if ((feature instanceof Folder)) {
				// ((Folder)feature).getFeature().

			}

		}

	}

	public void reWriteKML(String fileName) {

		try {
			kml.marshal(new File(fileName));
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

}