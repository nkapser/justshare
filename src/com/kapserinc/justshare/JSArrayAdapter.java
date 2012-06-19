package com.kapserinc.justshare;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class JSArrayAdapter extends ArrayAdapter<JSModel> {

	private final LayoutInflater inflater;
	
	public JSArrayAdapter(Context context, List<JSModel> modelList){
		super(context, R.layout.rowlayout, R.id.textView1, modelList);
		
		//Caching the layout inflater!
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){		
		JSModel model = this.getItem(position);
		
		CheckBox checkBox;
		TextView textView;
		ImageView imageView;
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.rowlayout, null);
			
			imageView = (ImageView) convertView.findViewById(R.id.appIcon);
			imageView.setImageDrawable(model.getIcon());
			
			textView = (TextView) convertView.findViewById(R.id.textView1);
			checkBox = (CheckBox) convertView.findViewById(R.id.check); 
			
			convertView.setTag(new JSViewHolder(textView, imageView, checkBox));
			checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox)v;
					JSModel model = (JSModel) cb.getTag();					
					model.setSelected(cb.isChecked());
				}
			}); //Re-use if the view is already present
		}else {
			JSViewHolder viewHolder = (JSViewHolder) convertView.getTag();
			checkBox = viewHolder.getCheckBox();
			textView = viewHolder.getTextView();
			imageView = viewHolder.getImageView();
		}
		
		checkBox.setTag(model);
		
		checkBox.setSelected(model.isSelected());
		textView.setText(model.toString());
		imageView.setImageDrawable(model.getIcon());
		
		return convertView;
	}

}