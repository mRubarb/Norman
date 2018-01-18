package classes;

public class ApplicationClass 
{
	public String m_Key = "";
	public String m_Name = "";
	public String m_Description = "";
	public boolean m_Enabled = false;
	
	public ApplicationClass(String key, String name, String description, boolean enabled)
	{
		m_Key = key;
		m_Name = name;
		m_Description = description;
		m_Enabled = enabled;
	}

	public void ShowApp()
	{
		System.out.println("----------------------------");
		System.out.println(m_Key);
		System.out.println(m_Name);
		System.out.println(m_Description);
		System.out.println(m_Enabled);
	}
}
