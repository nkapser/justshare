package com.kapserinc.justshare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class IntroActivity extends Activity {

	private ArrayList<JSModel> listItems;
	private HashSet<JSModel> selectedItems;
	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		loadInstalledApps();
		bindItemsToView();
	}
	
	private void bindItemsToView(){
		listView = (ListView)findViewById(R.id.list_view);
		JSArrayAdapter adapter = new JSArrayAdapter(this, listItems, selectedItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JSArrayAdapter adapter = (JSArrayAdapter)parent.getAdapter();
				JSModel model = adapter.getItem(position);
				model.toggleSelection();
				JSViewHolder viewHolder = (JSViewHolder) view.getTag();
				viewHolder.getCheckBox().setChecked(model.isSelected());
			}
		});
	}
	
	private void loadInstalledApps(){
		selectedItems = new HashSet<JSModel>();
		listItems = (ArrayList<JSModel>) getLastNonConfigurationInstance() ;
		if(listItems == null){
			listItems = new ArrayList<JSModel>();
			final PackageManager pm = getPackageManager();
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){ 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menulayout, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.share:
			invokeShareIntent();
			return true;
		default:
			return false;
		}
	}
	
	private void invokeShareIntent(){
		if (selectedItems.size() > 0) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_TEXT, getSelectedAppsWithUri(selectedItems));
			i.putExtra(Intent.EXTRA_EMAIL, getSelectedAppsWithUri(selectedItems));
			i.putExtra(Intent.EXTRA_SUBJECT, "I have found "
					+ selectedItems.size() + " interesting App(s)");
			startActivity(Intent.createChooser(i,
					getResources().getText(R.string.share)));
		} else {
			Toast.makeText(getApplicationContext(),
					"Select atleast 1 App to share", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	private String getSelectedAppsWithUri(HashSet<JSModel> selectedItems) {
		String formattedText = "";
		Iterator<JSModel> iterator = selectedItems.iterator();
		while (iterator.hasNext()) {
			JSModel model = iterator.next();
			formattedText = formattedText + model.getName() + " : "
					+ model.getMarketUri() + "\n\n";
		}
		return formattedText;
	}

	/**
	 * Do cleanup activities (if any).
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();
	}	
	
	@Override
	public Object onRetainNonConfigurationInstance() {  
	    return listItems;  
	  } 
}