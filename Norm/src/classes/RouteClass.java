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
		m_appname = appName;		
		m_deployKey = deployKey;
		m_deployVersion = deployVersion;
		m_tenantId = tenantId;
		m_description = description;
		m_enabled = enabled;
		m_disabledReason = disabledReason;
		m_allowServiceCalls = allowServiceCalls;
		m_host = host;
		m_path = path;
	}

	public void ShowRoute()
	{
		System.out.println("----------------------------");
		System.out.println("key: " + m_Key);
		System.out.println("host: " + m_host);
		System.out.println("path: " + m_path);
		System.out.println("tenantID: " + m_tenantId);		
		System.out.println("tenantKey: " + m_tennantKey);		
		System.out.println("tenantName: " + m_tennantName);
		System.out.println("appKey: " + m_appKey);	
		System.out.println("appName: " + m_appname);		
		System.out.println("deployKey: " + m_deployKey);		
		System.out.println("deployVersion: " + m_deployVersion);
		System.out.println("enabled: " + m_enabled);
		System.out.println("reason: " + m_disabledReason);		
		System.out.println("allowServiceCall: " + m_allowServiceCalls);		
		
	//m_appKey
	
	}	
}
