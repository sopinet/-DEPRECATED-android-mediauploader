package com.sopinet.mediauploader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressWarnings("unchecked")
	public class SendingActivityList extends ArrayAdapter {

 	static class ViewHolder {
 	    public TextView item;
		private TextView porcentage;
		private ProgressBar progressbar;
 	}    	
 	
		Activity context;
		DataBar[] data;
		protected final LayoutInflater inflater;
 	
		public SendingActivityList(Activity context, DataBar[] data) {
	        //super(context, R.layout.sendingactivity_item, data);
			super(context, R.layout.sendingactivity_item, data);
	        this.context = context;
	        this.data = data;
	        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}        

		public View getView(int position, View convertView, ViewGroup parent)
		{
		    View item = convertView;
		    ViewHolder holder;
		 
		    if(item == null)
		    {
		        LayoutInflater inflater = context.getLayoutInflater();
		        //item = inflater.inflate(R.layout.sendingactinamevity_item, null);
		        item = inflater.inflate(R.layout.sendingactivity_item, null);
		 
		        holder = new ViewHolder();
		        holder.porcentage = (TextView)item.findViewById(R.id.status_porcentage);
		        holder.progressbar = (ProgressBar)item.findViewById(R.id.status_progress);
		        holder.item = (TextView)item.findViewById(R.id.status_text);
		        //holder.thumb = (ImageView)item.findViewById(R.id.gamoreIMAGE);
		 
		        item.setTag(holder);
		    }
		    else
		    {
		        holder = (ViewHolder)item.getTag();
		    }
		    String porc = data[position].porcentage.toString();
		    //data[position].porcentage.toString()
		    holder.porcentage.setText(porc + "%");
		    holder.item.setText(data[position].item);
		    holder.progressbar.setProgress(data[position].porcentage);
		    //holder.name.setText(porc);
		 
		    return(item);
		}				
 }