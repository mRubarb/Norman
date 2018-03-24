package classes;

public class RouteClass 
{
	public String m_Key = "";
	public String m_tennantKey = "";	
	public String m_tennantName = "";	
	public String m_appKey = "";
	public String m_appname = "";
	public String m_deployKey = "";
	public String m_deployVersion = "";
	public String m_tenantId = "";
	public String m_description = "";
	public boolean m_enabled = false;	
	public String m_disabledReason = "";	
	public boolean m_allowServiceCalls = false;
	public String m_host = "";
	public String m_path = "";
	
	public RouteClass(String key, String tennantKey, String tennantName, String appKey, String appName, String deployKey, String deployVersion, String tenantId, String description, boolean enabled, String disabledReason,
		    boolean  allowServiceCalls, String host, String path)
	{
		m_Key = key;
		m_tennantKey = tennantKey;	
		m_tennantName = tennantName;		
		m_appKey = appKey;	
		m_deployKey = deployKey;
		m_tenantId = tenantId;
		m_description = description;
		m_enabled = enabled;	
		m_disabledReason = disabledReason;	
		m_allowServiceCalls = allowServiceCalls;
		m_host = host;
		m_path = path;
	}

	public void ShowApp()
	{
		System.out.println("----------------------------");
		System.out.println("Key: " + m_Key);
		//System.out.println("Name: " + m_Name);
		//System.out.println("Default Host: " + m_defaultHost);		
		//System.out.println("Default Path: " + m_defaultPath);		
		//System.out.println("Description: " + m_Description);
		//System.out.println("Enabled: " + m_Enabled);
	}	
}
