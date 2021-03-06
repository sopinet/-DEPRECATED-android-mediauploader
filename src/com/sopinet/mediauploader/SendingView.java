package com.sopinet.mediauploader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SendingView extends LinearLayout {

	private View mValue;
	private DataBar[] dataBarArray;
	public SendingActivityList adaptador = null;
	public ListView lst = null;
	private AlertDialog alertDialog;
	private HttpPostSQL usdbh = null;
	private SQLiteDatabase dbr = null;
	private SQLiteDatabase dbw = null;
	private TextView sendingText = null;
  
	public SendingView(Context context) {
		super(context);
		init();
	}
	
	public SendingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
  
 	//Build from XML layout
	public SendingView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	
	public void init()
	{
		setOrientation(LinearLayout.VERTICAL);
	    LayoutInflater inflater = (LayoutInflater) getContext()
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        inflater.inflate(R.layout.sendingview, this, true);
	        
		usdbh = HttpPostSQL.getInstance(getContext());
		dbr = usdbh.getReadable();
		dbw = usdbh.getWritable();	
		
		sendingText = (TextView) getChildAt(0);
		lst = (ListView) getChildAt(1);
		
		//new SendingActivityTask().execute();  // display the data
		int delay = 200; // delay for 0.2 sec. 
		int period = 1000; // repeat every 1 sec. 
		Timer timer = new Timer(); 
		timer.scheduleAtFixedRate(new TimerTask() 
		    { 
		        public void run() 
		        { 
		            refreshList();  // display the data
		        } 
		    }, delay, period);		
	}
	
	private void refreshList() {
		if (dbr != null) {
        	//String[] args = new String[] {"sending"};
        	//Cursor c = db.rawQuery("SELECT indice, porcentage FROM http_index WHERE status=? ", args);
			Cursor c = dbr.rawQuery("SELECT indice, status, porcentage, item FROM http_index", null);
        	dataBarArray = new DataBar[c.getCount()];
        	
        	// Update information text
        	//final TextView sendingText = (TextView)findViewById(R.id.sendingtext);
        	final int count = c.getCount();
        	((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {        	
                	if (count == 0) {
                    	sendingText.setText("No hay ninguna subida realizándose.");
                    } else if (count == 1){
                    	sendingText.setText("1 Subida se encuentra realizándose.");
                    } else {
                    	sendingText.setText(count + " Subidas se encuentran realizándose.");
                    }
                }
            });
        	
        	int i = 0;
        	if (c.moveToFirst()) {        		
       	     //Recorremos el cursor hasta que no haya más registros
       	     do {
       	    	 dataBarArray[i] = new DataBar();
       	    	 dataBarArray[i].indice = c.getString(0);
       	    	 dataBarArray[i].item = c.getString(3);
       	    	 // TODO: Check NULL
       	    	 try {
       	    		 dataBarArray[i].porcentage = Integer.valueOf(c.getString(2));
       	    	 } catch(Exception e) {
       	    		 dataBarArray[i].porcentage = 0;
       	    		 // Is null
       	    	 }
       	    	 i++;
       	     } while (c.moveToNext());
        	}
        	c.close();
		}

		adaptador = new SendingActivityList((Activity)getContext(), dataBarArray);   	
    	//lst = (ListView)findViewById(R.id.list_sendingactivity);
    	((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	lst.setAdapter(adaptador);
            }
    	});
    	
        lst.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, final long id) {
    		    alertDialog = new AlertDialog.Builder(getContext()).create();
    		    alertDialog.setTitle("Cancelar");
    		    alertDialog.setMessage("¿Deseas cancelar este envío?");
    		    alertDialog.setButton("Sí", new DialogInterface.OnClickListener() {
    		    	public void onClick(DialogInterface dialogAlert, int which) {
    		    		//actualizar = true;
    		    		if (dbw != null) {
    		    			dbw.delete("http_index", "indice="+String.valueOf(dataBarArray[(int) id].indice), null);
    		    		}
    		    	}
    		    });
    		    alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
    		    	public void onClick(DialogInterface dialogAlert, int which) {
    		    		//actualizar  = false;       
    		    	}
    		    });
    		    
    		    alertDialog.show();
            }
        });		
	}
}