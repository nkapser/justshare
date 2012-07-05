package com.kapserinc.justshare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class InstalledAppDataProvider {

	private static InstalledAppDataProvider installedAppDataProvider = null;
	private ArrayList<JSModel> listItems;
	private HashSet<JSModel> selectedItems;
	private Context context;
	
	/**
	 * creating default constructor as intent is leaking!
	 */
	InstalledAppDataProvider(){		
	}
	
	private InstalledAppDataProvider(Context context){
		this.context = context;
		listItems = new ArrayList<JSModel>();
		selectedItems = new HashSet<JSModel>();
		loadData();
	}
	
	public static InstalledAppDataProvider getInstance(Context context){
		if(installedAppDataProvider == null){
			installedAppDataProvider = new InstalledAppDataProvider(context);
		}
		return installedAppDataProvider;
	}
	
	private void loadData(){
		final PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo app : packages) {
			if (!app.packageName.contains("com.android")) {
				Drawable appIcon = app.loadIcon(pm);
				JSModel model = new JSModel(app.loadLabel(pm).toString(),
						appIcon, app.packageName);
				listItems.add(model);
			}
		}
	}
	
	public ArrayList<JSModel> getListItems(){
		return listItems;
	}
	
	public void onDestroy(){
		if(context != null){
			context = null;
		}
	}

	public HashSet<JSModel> getSelectedItems() {
		return selectedItems;
	}
}
