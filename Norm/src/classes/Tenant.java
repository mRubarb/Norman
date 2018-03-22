package classes;

public class Tenant {

	private String key;
	private String name;
	private String defaultTenantID;
	private boolean enabled;
	
	
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
	
}
