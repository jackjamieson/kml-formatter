
//Jack Jamieson 2015
//This class holds some values of the placemark that are useful for the GUI representation.
//It is easier to pass around this object than the placemark object.
public class Pmark {
	
	private String name, description, styleURL;
	
	public Pmark(String name, String description, String styleURL){
		this.name = name;
		this.description = description;
		this.styleURL = styleURL;
	}
	
	public String toString(){
		return name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription(){
		
		return description;
	}
	
	public String getStyleURL(){
		return styleURL;
	}
	
	//Get the part after the # which represents the lithic group.
	public String getLithicGroup(){
		String wholeString = styleURL;
		String lithicGroup = wholeString.substring(wholeString.indexOf("#")+1, wholeString.length());
		
		return lithicGroup;
		
	}
	
	//Get the part after the # which represents the age.
	public String getAge() {
		String wholeString = name;
		String age = "";
		if(wholeString.contains("("))
			age = wholeString.substring(wholeString.indexOf("(")+1, wholeString.length()-1);
		else age = "Other";
		
		return age;
		
	}
	
	//Useful for finding the lithic group in ANY placemark.
	public static String getLithicGroupGlobal(String placemark){
		String wholeString = placemark;
		String lithicGroup = wholeString.substring(wholeString.indexOf("#")+1, wholeString.length());
		
		return lithicGroup;
	}
	
	//Useful for finding the age in ANY placemark.
	public static String getAgeGlobal(String placemark) {
		String wholeString = placemark;
		String age = "";
		if(wholeString.contains("("))
			age = wholeString.substring(wholeString.indexOf("(")+1, wholeString.length()-1);
		else age = "Other";
		
		return age;
		
	}

}
