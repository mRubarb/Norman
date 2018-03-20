package classes;

public class ApplicationClass 
{
	public String m_Key = "";
	public String m_Name = "";
	public String m_Description = "";
	public String m_defaultHost = "";
	public String m_defaultPath = "";
	public boolean m_Enabled = false;
	
	
	// bladdxx
	public ApplicationClass(String key, String name, String description, boolean enabled, String defaultHost, String defaultPath)
	{
		m_Key = key;
		m_Name = name;
		m_Description = description;
		m_Enabled = enabled;
		m_defaultHost = defaultHost;
		m_defaultPath = defaultPath;
	}

	public void ShowApp()
	{
		System.out.println("----------------------------");
		System.out.println("Key: " + m_Key);
		System.out.println("Name: " + m_Name);
		System.out.println("Default Host: " + m_defaultHost);		
		System.out.println("Default Path: " + m_defaultPath);		
		System.out.println("Description: " + m_Description);
		System.out.println("Enabled: " + m_Enabled);
	}
}
