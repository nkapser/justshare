package com.kapserinc.justshare;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class JSViewHolder {
	private CheckBox checkBox;
	private TextView textView;
	private ImageView imageView;
	
	public JSViewHolder(){};
	
	public JSViewHolder(TextView textView, ImageView imageView, CheckBox checkBox){
		this.textView = textView;
		this.imageView = imageView;
		this.checkBox = checkBox;
	}
	
	public CheckBox getCheckBox(){
		return checkBox;
	}
	
	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public void setCheckBox(CheckBox checkBox){
		this.checkBox = checkBox;
	}
	
	public TextView getTextView(){
		return textView;
	}
	
	public void setTextView(TextView textView){
		this.textView = textView;
	}
}
