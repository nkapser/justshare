package com.kapserinc.justshare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.kapserinc.justshare.R.id;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class IntroActivity extends Activity {

	private ArrayList<JSModel> listItems;
	private ArrayAdapter<JSModel> listAdapter;
	private ListView listView;
	private HashSet<Integer> selectedItems;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();		
		inflater.inflate(R.menu.menulayout, menu);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			if (selectedItems.size() > 0) {
				Toast.makeText(getApplicationContext(),
						"Sharing: " + selectedItems.size() + " item(s) ...",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent();
				i.setAction(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getSelectedApps());
				i.putExtra(Intent.EXTRA_EMAIL, getSelectedApps());
				i.putExtra(Intent.EXTRA_SUBJECT, "I have found "
						+ selectedItems.size() + " interesting App(s)");
				startActivity(Intent.createChooser(i,
						getResources().getText(R.string.share)));
			} else {
				Toast.makeText(getApplicationContext(),
						"Select atleast 1 App to share", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		default:
			break;
		}

		return true;
	}

	private String getSelectedApps() {
		String formattedText = "";
		Iterator<Integer> iterator = selectedItems.iterator();
		while (iterator.hasNext()) {
			JSModel model = listAdapter.getItem(iterator.next());
			formattedText = formattedText + model.getName() + " : "
					+ model.getMarketUri() + "\n\n";
		}
		return formattedText;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		selectedItems = new HashSet<Integer>();
		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JSModel model = listAdapter.getItem(position);
				model.toggleSelection();
				JSViewHolder viewHolder = (JSViewHolder) view.getTag();
				viewHolder.getCheckBox().setChecked(model.isSelected());

				if (model.isSelected()) {
					selectedItems.add(position);
				} else {
					selectedItems.remove(position);
				}

				Toast.makeText(getApplicationContext(),
						listItems.get(position).toString(), Toast.LENGTH_SHORT)
						.show();
			}
		});

		listItems = (ArrayList<JSModel>) getLastNonConfigurationInstance();
		if (listItems == null) {
			listItems = new ArrayList<JSModel>();

			final PackageManager pm = getPackageManager();
			List<ApplicationInfo> packages = pm
					.getInstalledApplications(PackageManager.GET_META_DATA);

			for (ApplicationInfo app : packages) {
				if (!app.packageName.contains("com.android")) {
					Drawable appIcon = app.loadIcon(pm);
					JSModel model = new JSModel(app.loadLabel(pm).toString(),
							appIcon, app.packageName);
					listItems.add(model);
				}
			}
		}
		
		listAdapter = new JSArrayAdapter(this, listItems, selectedItems);
		listView.setAdapter(listAdapter);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return listItems;
	}
}