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
	
	
	/* Comparator for sorting the list by Key Ascending */
    public static Comparator<Deployment> keyComparatorAsc = new Comparator<Deployment>() {

		public int compare(Deployment dep1, Deployment dep2) {
			
		   String deploymentKey1 = dep1.getKey().toUpperCase();
		   String deploymentKey2 = dep2.getKey().toUpperCase();
	
		   //ascending order
		   return deploymentKey1.compareTo(deploymentKey2);
	
	    }
	};

    /* Comparator for sorting the list by Version Ascending */
	public static Comparator<Deployment> versionComparatorAsc = new Comparator<Deployment>() {

		public int compare(Deployment dep1, Deployment dep2) {
			
		   String deploymentVersion1 = dep1.getVersion().toUpperCase();
		   String deploymentVersion2 = dep2.getVersion().toUpperCase();
		   
		   //ascending order
		   return deploymentVersion1.compareTo(deploymentVersion2);
	
	    }
	};
    
    /* Comparator for sorting the list by Enabled Ascending */
	public static Comparator<Deployment> enabledComparatorAsc = new Comparator<Deployment>() {

		public int compare(Deployment dep1, Deployment dep2) {
			
		   Boolean deploymentEnabled1 = dep1.isEnabled();
		   Boolean deploymentEnabled2 = dep2.isEnabled();
		   
		   //ascending order
		   return deploymentEnabled1.compareTo(deploymentEnabled2);
	
	    }
	};

	/* Comparator for sorting the list by Key Descending */
    public static Comparator<Deployment> keyComparatorDesc = new Comparator<Deployment>() {

		public int compare(Deployment dep1, Deployment dep2) {
			
		   String deploymentKey1 = dep1.getKey().toUpperCase();
		   String deploymentKey2 = dep2.getKey().toUpperCase();
	
		   //descending order
		   return deploymentKey2.compareTo(deploymentKey1);
	    }
	};

    /* Comparator for sorting the list by Version Descending */
	public static Comparator<Deployment> versionComparatorDesc = new Comparator<Deployment>() {

		public int compare(Deployment dep1, Deployment dep2) {
			
		   String deploymentVersion1 = dep1.getVersion().toUpperCase();
		   String deploymentVersion2 = dep2.getVersion().toUpperCase();
		   
		   //descending order
		   return deploymentVersion2.compareTo(deploymentVersion1);
	    }
	};
    
    /* Comparator for sorting the list by Enabled Descending */
	public static Comparator<Deployment> enabledComparatorDesc = new Comparator<Deployment>() {

		public int compare(Deployment dep1, Deployment dep2) {
			
		   Boolean deploymentEnabled1 = dep1.isEnabled();
		   Boolean deploymentEnabled2 = dep2.isEnabled();
		   
		   //descending order
		   return deploymentEnabled2.compareTo(deploymentEnabled1);
	    }
	};
	
}