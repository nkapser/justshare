package com.kapserinc.justshare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class IntroActivity extends Activity {

	private ArrayList<JSModel> listItems;
	private HashSet<JSModel> selectedItems;
	private ListView listView;
	private InstalledAppDataProvider appDataProvider;
	
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
				if(model.isSelected()){
					selectedItems.add(model);
				}else{
					selectedItems.remove(model);
				}
			}
		});
	}
	
	private void loadInstalledApps(){
		appDataProvider = (InstalledAppDataProvider) getLastNonConfigurationInstance() ;
		if(appDataProvider == null){			
			appDataProvider = InstalledAppDataProvider.getInstance(this);
			listItems = appDataProvider.getListItems();
			selectedItems = appDataProvider.getSelectedItems();
		}else{			
			listItems = appDataProvider.getListItems();
			selectedItems = appDataProvider.getSelectedItems();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){ 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menulayout, menu);
		
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(true);           
        }
                
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.share:
			invokeShareIntent();
			return false;
        case R.id.search:
            return onSearchRequested();			
		default:
			return false;
		}
	}
	
	private void invokeShareIntent(){
		if (selectedItems.size() > 0) {
			Toast.makeText(getApplicationContext(),
					"Sharing "+selectedItems.size()+" app(s)", Toast.LENGTH_SHORT)
					.show();
			Intent i = new Intent();
			i.setAction(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_TEXT, getSelectedAppsWithUri(selectedItems));
			i.putExtra(Intent.EXTRA_EMAIL, getSelectedAppsWithUri(selectedItems));
			i.putExtra(Intent.EXTRA_SUBJECT, "I have found "
					+ selectedItems.size() + " interesting App(s)");
			startActivity(i);
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
	
	@Override
	public void onPause(){	
		super.onPause();
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
	    return appDataProvider;  
	  } 
}