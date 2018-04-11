package testItems;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import baseItems.BaseMain;
import classes.Deployment;

public class AnaTest extends BaseMain {

	public static void main(String args[]) {
		
		String empty = "";
		String blankspace1 = " ";
		String blankspaces = "    ";
		
		System.out.println("String empty length: " + empty.length());
		System.out.println("String blankspace1 length: " + blankspace1.length());
		System.out.println("String blankspaces length: " + blankspaces.length());
	 	
		// testList();
		
	}
	
	public static void testList() {
		
		List<Deployment> list1 = new ArrayList<>();
		List<Deployment> list2 = new ArrayList<>();
		
		Deployment deployment1 = new Deployment();
		Deployment deployment2 = new Deployment();
		Deployment deployment3 = new Deployment();

		deployment1.setKey("1");
		deployment1.setApplicationKey("appky1");
		deployment1.setVersion("4.1");
		deployment1.setDescription("test1");
		deployment1.setEnabled(true);

		deployment2.setKey("2");
		deployment2.setApplicationKey("appkwy2");
		deployment2.setVersion("2.2");
		deployment2.setDescription("test2");
		deployment2.setEnabled(false);

		deployment3.setKey("3");
		deployment3.setApplicationKey("appkwy3");
		deployment3.setVersion("1.3");
		deployment3.setDescription("test3");
		deployment3.setEnabled(true);
		
		list1.add(deployment1);
		list1.add(deployment2);
		list1.add(deployment3);
		
		list2.add(deployment2);
		list2.add(deployment3);
		list2.add(deployment1);
	
		/*
		System.out.println("  ** Lists not sorted ** ");
		
		System.out.println("  ** List 1 ** ");
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getKey());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getApplicationKey());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getDescription());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getVersion());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).isEnabled());
			
		}
		
		System.out.println("  ** List 2 ** ");
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getKey());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getApplicationKey());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getDescription());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getVersion());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).isEnabled());
			
		}
		
		if (list1.equals(list2)) {
			System.out.println("lists are equal");
		} else {
			System.out.println("lists are NOT equal");
		}
		
		
		Collections.sort(list1, Deployment.versionComparatorAsc);
		Collections.sort(list2, Deployment.keyComparatorAsc);
		
		System.out.println("  ** Lists sorted ** ");
		System.out.println("  ** List 1 ** ");
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getKey());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getApplicationKey());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getDescription());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).getVersion());
			System.out.println("dep " + (i+1) + " - key: " + list1.get(i).isEnabled());
			
		}
		
		System.out.println("  ** List 2 ** ");
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getKey());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getApplicationKey());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getDescription());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getVersion());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).isEnabled());
			
		}
		
		if (list1.equals(list2)) {
			System.out.println("lists are equal");
		} else {
			System.out.println("lists are NOT equal");
		}
		
		Collections.sort(list1, Deployment.versionComparatorAsc);
		Collections.sort(list2, Deployment.versionComparatorAsc);
		
		if (list1.equals(list2)) {
			System.out.println("lists are equal");
		} else {
			System.out.println("lists are NOT equal");
		}
		*/
		
		System.out.println("  ** Original List 2 ** ");
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getKey());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getApplicationKey());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getDescription());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).getVersion());
			System.out.println("dep " + (i+1) + " - key: " + list2.get(i).isEnabled());
			
		}
		
		System.out.println("  ** List 2 Sorted by KEY ASC ** ");
		
		Collections.sort(list2, Deployment.keyComparatorAsc);
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("Key: " + list2.get(i).getKey());
						
		}
		
		System.out.println("  ** List 2 Sorted by VERSION ASC ** ");
		
		Collections.sort(list2, Deployment.versionComparatorAsc);
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("Version: " + list2.get(i).getVersion());
			
		}
		
		System.out.println("  ** List 2 Sorted by ENABLED ASC ** ");
		
		Collections.sort(list2, Deployment.enabledComparatorAsc);
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("Is Enabled: " + list2.get(i).isEnabled());
			
		}
	
		System.out.println("  ** List 2 Sorted by KEY DESC** ");
		
		Collections.sort(list2, Deployment.keyComparatorDesc);
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("Key: " + list2.get(i).getKey());
						
		}
		
		System.out.println("  ** List 2 Sorted by VERSION DESC** ");
		
		Collections.sort(list2, Deployment.versionComparatorDesc);
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("Version: " + list2.get(i).getVersion());
			
		}
		
		System.out.println("  ** List 2 Sorted by ENABLED DESC** ");
		
		Collections.sort(list2, Deployment.enabledComparatorDesc);
		
		for (int i = 0; i < 3; i++) {
			
			System.out.println("Is Enabled: " + list2.get(i).isEnabled());
			
		}
		
	}
	
	
}
