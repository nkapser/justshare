package com.kapserinc.justshare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.google.ads.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class IntroActivity extends Activity {

	private ArrayList<JSModel> listItems;
	private JSArrayAdapter listAdapter;
	private ListView listView;
	private HashSet<JSModel> selectedItems;
	private AdRequest adRequest;	
	
	private AdView adView;
	private LinearLayout mainLayout;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();		
		inflater.inflate(R.menu.menulayout, menu);
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
		Iterator<JSModel> iterator = selectedItems.iterator();
		while (iterator.hasNext()) {
			JSModel model = iterator.next();
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
		listView = (ListView) findViewById(R.id.listView1);
		selectedItems = new HashSet<JSModel>();							
		listItems = (ArrayList<JSModel>)this.getLastNonConfigurationInstance();
		
		if(listItems == null){			
			listItems = new ArrayList<JSModel>();
		}
		
		listAdapter = new JSArrayAdapter(this, listItems, selectedItems);		
		MyAppLoaderTask appLoaderTask = new MyAppLoaderTask(this, listView, listItems, listAdapter);
		appLoaderTask.execute(PackageManager.GET_META_DATA);	
		
//		loadAd();
	}
	
	private class MyAppLoaderTask extends AsyncTask<Integer, Void, ArrayList<JSModel>>{
		
		private final Context context;
		private final ListView view;
		private final ArrayList<JSModel> listItems;
		private final JSArrayAdapter listAdapter;
		private ProgressDialog dialog;
		
		MyAppLoaderTask(Context context, ListView view, ArrayList<JSModel> listItems, JSArrayAdapter listAdapter){
			this.context = context;
			this.view = view;
			this.listItems = listItems;
			this.listAdapter = listAdapter;
		}
		
		@Override
		protected void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("Loading...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected  ArrayList<JSModel> doInBackground(Integer... params) {
				final PackageManager pm = getPackageManager();
				List<ApplicationInfo> packages = pm
						.getInstalledApplications(params[0]);

				for (ApplicationInfo app : packages) {
					if (!app.packageName.contains("com.android")) {
						Drawable appIcon = app.loadIcon(pm);
						JSModel model = new JSModel(app.loadLabel(pm).toString(),
								appIcon, app.packageName);
						listItems.add(model);
					}
				}						
			
			return listItems;
		}
		
		@Override
		protected void onPostExecute(ArrayList<JSModel> result){
			listAdapter.setItems(result);
			view.setAdapter(listAdapter);
			view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					JSModel model = listAdapter.getItem(position);
					model.toggleSelection();
					JSViewHolder viewHolder = (JSViewHolder) view.getTag();
					viewHolder.getCheckBox().setChecked(model.isSelected());

					if (model.isSelected()) {
						selectedItems.add(model);
						Toast.makeText(getApplicationContext(),
								listItems.get(position).getName(), Toast.LENGTH_SHORT)
								.show();
					} else {
						selectedItems.remove(model);
					}
				}

			});

			dialog.dismiss();
		}
	}
	
	@Override
	public void onDestroy(){
		if(adView != null){
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		//		loadAd();
		return listItems;
	}
	
	private void loadAd(){
		adView = new AdView(this, AdSize.BANNER, "a14fe16eed30f4b");
		adRequest = new AdRequest();
		adView.loadAd(adRequest);
		mainLayout = (LinearLayout)findViewById(R.id.main_layout);
		mainLayout.addView(adView);		
	}

}