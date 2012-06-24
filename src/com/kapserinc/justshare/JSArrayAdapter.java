package com.kapserinc.justshare;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class JSArrayAdapter extends ArrayAdapter<JSModel>{

	private HashSet<JSModel> selectedItems;
	private final LayoutInflater inflater;
	
	public JSArrayAdapter(Context context, ArrayList<JSModel> listItems, HashSet<JSModel> selectedItems) {
		super(context, R.layout.rowlayout, R.id.app_label, listItems);

		this.selectedItems = selectedItems;
		// Caching the layout inflater!
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		JSModel model = this.getItem(position);

		CheckBox checkBox;
		TextView textView;
		ImageView imageView;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.rowlayout, null);

			imageView = (ImageView) convertView.findViewById(R.id.app_icon);
			imageView.setImageDrawable(model.getIcon());

			textView = (TextView) convertView.findViewById(R.id.app_label);
			checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

			convertView.setTag(new JSViewHolder(textView, imageView, checkBox));
			checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					JSModel model = (JSModel) cb.getTag();
					if(cb.isChecked()){
						selectedItems.add(model);						
					}else{
						selectedItems.remove(model);
					}
					model.setSelected(cb.isChecked());
				}
			}); // Re-use if the view is already present
			
		} else {
			JSViewHolder viewHolder = (JSViewHolder) convertView.getTag();
			checkBox = viewHolder.getCheckBox();
			textView = viewHolder.getTextView();
			imageView = viewHolder.getImageView();
		}

		checkBox.setTag(model);

		checkBox.setSelected(model.isSelected());
		textView.setText(model.getName());
		imageView.setImageDrawable(model.getIcon());	

		return convertView;
	}
}
