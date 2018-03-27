package classes;

import java.util.ArrayList;
import java.util.List;

public class TenantAppForAppSearch 
{
	public String m_TenantName =  "";
	
	List<ApplicationClass> listOfAppsForTenant = new ArrayList<ApplicationClass>();
	
	public TenantAppForAppSearch(String tenantName)
	{
		m_TenantName = tenantName;
	}
	
	public void AddAppToList(ApplicationClass appClass)
	{
		listOfAppsForTenant.add(appClass);
	}
	
	public void Show()
	{
		System.out.println("----------------------------------------------------------");
		System.out.println("Tenant = " + m_TenantName);
		
		for(ApplicationClass appClasse : listOfAppsForTenant)
		{
			appClasse.ShowApp();
		}
		System.out.println("");
	}
	
	public List<ApplicationClass> GetApplicationList()
	{
		return listOfAppsForTenant;
	}
	
	
	
}
