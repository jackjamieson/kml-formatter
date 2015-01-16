
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
	
	public String getLithicGroup(){
		String wholeString = styleURL;
		String lithicGroup = wholeString.substring(wholeString.indexOf("#")+1, wholeString.length());
		
		return lithicGroup;
		
	}
	
	public String getAge() {
		String wholeString = name;
		String age = "";
		if(wholeString.contains("("))
			age = wholeString.substring(wholeString.indexOf("(")+1, wholeString.length()-1);
		else age = "Other";
		
		return age;
		
	}
	
	public static String getLithicGroupGlobal(String placemark){
		String wholeString = placemark;
		String lithicGroup = wholeString.substring(wholeString.indexOf("#")+1, wholeString.length());
		
		return lithicGroup;
	}
	
	public static String getAgeGlobal(String placemark) {
		String wholeString = placemark;
		String age = "";
		if(wholeString.contains("("))
			age = wholeString.substring(wholeString.indexOf("(")+1, wholeString.length()-1);
		else age = "Other";
		
		return age;
		
	}

}
