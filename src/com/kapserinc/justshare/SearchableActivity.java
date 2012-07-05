package com.kapserinc.justshare;

import java.util.HashSet;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchableActivity extends Activity {
	
	private InstalledAppDataProvider appDataProvider;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.searchresult);
		
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
		handleIntent(getIntent());		
	}

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
            Intent introActivityIntent = new Intent(this, IntroActivity.class);
            introActivityIntent.setData(intent.getData());
            startActivity(introActivityIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

	private void showResults(String query) {
		ListView listView = (ListView)findViewById(R.id.search_result_list_view);
		appDataProvider = InstalledAppDataProvider.getInstance(this);
		JSArrayAdapter adapter = new JSArrayAdapter(this, appDataProvider.getListItems(), appDataProvider.getSelectedItems());
		adapter.getFilter().filter(query);
		listView.setAdapter(adapter);
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
		HashSet<JSModel> selectedItems = appDataProvider.getSelectedItems();
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
}
