package com.kapserinc.justshare;

import android.graphics.drawable.Drawable;

public class JSModel {
	private final String name;
	private final Drawable icon;
	private boolean selected;
	private String packageName;

	public JSModel(String name, Drawable icon, String packageName){
		this.name = name;
		this.icon = icon;
		this.packageName = packageName;
		this.selected = false;
	}
	
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public void toggleSelection(){
		this.selected = !selected;
	}
	
	public String getName(){
		return name;
	}
	
	public Drawable getIcon(){
		return icon;
	}
	
	public String getMarketUri(){
		return UriPrefix()+"apps/details?id="+packageName;
	}
	
	private String UriPrefix(){
		return "http://play.google.com/store/";
	}	
}
