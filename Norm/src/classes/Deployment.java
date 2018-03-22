package classes;

import java.util.Comparator;

public class Deployment {

	private String key;
	private String applicationKey;
	private String version;
	private String description;
	private boolean enabled;
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getApplicationKey() {
		return applicationKey;
	}
	public void setApplicationKey(String applicationKey) {
		this.applicationKey = applicationKey;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	/* Comparator for sorting the list by Key */
    public static Comparator<Deployment> deploymentKeyComparator = new Comparator<Deployment>() {

	public int compare(Deployment dep1, Deployment dep2) {
		
	   String deploymentKey1 = dep1.getKey().toUpperCase();
	   String deploymentKey2 = dep2.getKey().toUpperCase();

	   //ascending order
	   return deploymentKey1.compareTo(deploymentKey2);

	   //descending order
	   //return deploymentKey2.compareTo(deploymentKey1);
    }};

    /* Comparator for sorting the list by Version */
  
    
    /* Comparator for sorting the list by Enabled */
    
}