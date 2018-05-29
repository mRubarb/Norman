package classes;

import java.util.Comparator;

public class Tenant {

	private String key;
	private String name;
	private String defaultTenantID;
	private boolean enabled;
	
	private int applicationCount;
	private int deploymentCount;
	private int routeCount;
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultTenantID() {
		return defaultTenantID;
	}
	public void setDefaultTenantID(String defaultTenantID) {
		this.defaultTenantID = defaultTenantID;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getApplicationCount() {
		return applicationCount;
	}
	public void setApplicationCount(int applicationCount) {
		this.applicationCount = applicationCount;
	}
	public int getDeploymentCount() {
		return deploymentCount;
	}
	public void setDeploymentCount(int deploymentCount) {
		this.deploymentCount = deploymentCount;
	}
	public int getRouteCount() {
		return routeCount;
	}
	public void setRouteCount(int routeCount) {
		this.routeCount = routeCount;
	}

	/* Comparator for sorting the list by Key Ascending */
    public static Comparator<Tenant> keyComparatorAsc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   String tenantKey1 = tenant1.getKey().toLowerCase(); 
		   String tenantKey2 = tenant2.getKey().toLowerCase();
	
		   // Set tenant keys lowercase, because they can contain underscores 
		   // Underscores go before lowercase letters and after uppercase letters,
		   // and this seems to be approach taken to select items on UI: 1st underscore, then letter. 
		   // E.g.: Sort by Key in ASC order:
		   // ....
		   // TEN_1
		   // TEN_2
		   // TENANTABC
		   // ....
		   
		   //ascending order
		   return tenantKey1.compareTo(tenantKey2);
	
	    }
	};

    /* Comparator for sorting the list by Name Ascending */
	public static Comparator<Tenant> nameComparatorAsc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   String tenantName1 = tenant1.getName().toLowerCase();
		   String tenantName2 = tenant2.getName().toLowerCase();
		   
		   //ascending order
		   return tenantName1.compareTo(tenantName2);
	
	    }
	};
    
	/* Comparator for sorting the list by Default Tenant ID Ascending */
	public static Comparator<Tenant> tenantIDComparatorAsc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   String tenantID1 = tenant1.getDefaultTenantID().toLowerCase();
		   String tenantID2 = tenant2.getDefaultTenantID().toLowerCase();
		   
		   //ascending order
		   return tenantID1.compareTo(tenantID2);
	
	    }
	};
    
	/* Comparator for sorting the list by Enabled Ascending */
	public static Comparator<Tenant> enabledComparatorAsc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   Boolean tenantEnabled1 = tenant1.isEnabled();
		   Boolean tenantEnabled2 = tenant2.isEnabled();
		   
		   //ascending order
		   return tenantEnabled1.compareTo(tenantEnabled2);
	
	    }
	};

	/* Comparator for sorting the list by Key Descending */
    public static Comparator<Tenant> keyComparatorDesc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   String tenantKey1 = tenant1.getKey().toLowerCase();
		   String tenantKey2 = tenant2.getKey().toLowerCase();
	
		   //descending order
		   return tenantKey2.compareTo(tenantKey1);
	    }
	};

    /* Comparator for sorting the list by Name Descending */
	public static Comparator<Tenant> nameComparatorDesc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   String tenantName1 = tenant1.getName().toLowerCase();
		   String tenantName2 = tenant2.getName().toLowerCase();
		   
		   //descending order
		   return tenantName2.compareTo(tenantName1);
	    }
	};
    
	/* Comparator for sorting the list by Default Tenant ID Descending */
	public static Comparator<Tenant> tenantIDComparatorDesc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   String tenantID1 = tenant1.getDefaultTenantID().toLowerCase();
		   String tenantID2 = tenant2.getDefaultTenantID().toLowerCase();
		   
		   //ascending order
		   return tenantID2.compareTo(tenantID1);
	
	    }
	};
    
	
    /* Comparator for sorting the list by Enabled Descending */
	public static Comparator<Tenant> enabledComparatorDesc = new Comparator<Tenant>() {

		public int compare(Tenant tenant1, Tenant tenant2) {
			
		   Boolean tenantEnabled1 = tenant1.isEnabled();
		   Boolean tenantEnabled2 = tenant2.isEnabled();
		   
		   //descending order
		   return tenantEnabled2.compareTo(tenantEnabled1);
	    }
	};
	
	
}
