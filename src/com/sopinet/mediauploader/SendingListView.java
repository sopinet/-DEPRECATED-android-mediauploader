package com.sopinet.mediauploader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SendingListView extends ListView
{
	private DataBar[] dataBarArray;
	public SendingActivityList adaptador = null;
	public ListView lst = null;
	private AlertDialog alertDialog;
	private HttpPostSQL usdbh = null;
	private SQLiteDatabase dbr = null;
	private SQLiteDatabase dbw = null;
	
	private Context context = null;
	
	public SendingListView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	//This example uses this method since being built from XML
	public SendingListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init();
	}
  
 	//Build from XML layout
	public SendingListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
  
	public void init()
	{
		usdbh = HttpPostSQL.getInstance(context);
		dbr = usdbh.getReadable();
		dbw = usdbh.getWritable();
		
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
		
		/*
		test = new ArrayAdapter<String>(getContext(),R.layout.row, R.id.label , testItems);
		setAdapter(test);
		setOnItemClickListener(new ListSelection());
		*/
	}
	
	private void refreshList() {
		if (dbr != null) {
        	//String[] args = new String[] {"sending"};
        	//Cursor c = db.rawQuery("SELECT indice, porcentage FROM http_index WHERE status=? ", args);
			Cursor c = dbr.rawQuery("SELECT indice, porcentage FROM http_index", null);
        	dataBarArray = new DataBar[c.getCount()];
        	
        	// Update information text
        	/*
        	final TextView sendingText = (TextView)findViewById(R.id.sendingtext);
        	final int count = c.getCount();
        	((Activity)context).runOnUiThread(new Runnable() {
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
            */
        	
        	int i = 0;
        	if (c.moveToFirst()) {        		
       	     //Recorremos el cursor hasta que no haya más registros
       	     do {
       	    	 dataBarArray[i] = new DataBar();
       	    	 dataBarArray[i].indice = c.getString(0);
       	    	 dataBarArray[i].name = c.getString(0);
       	    	 // TODO: Check NULL
       	    	 try {
       	    		 dataBarArray[i].porcentage = Integer.valueOf(c.getString(1));
       	    	 } catch(Exception e) {
       	    		 dataBarArray[i].porcentage = 0;
       	    		 // Is null
       	    	 }
       	    	 i++;
       	     } while (c.moveToNext());
        	}
        	c.close();
		}

		adaptador = new SendingActivityList((Activity)context, dataBarArray);   	
    	//lst = (ListView)findViewById(R.id.list_sendingactivity);
    	((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	setAdapter(adaptador);
            }
    	});
    	
        setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, final long id) {
    		    alertDialog = new AlertDialog.Builder(context).create();
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
  
/*	
	private class ListSelection implements OnItemClickListener
	{
 
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setMessage("You pressed item #" + (position+1));
			builder.setPositiveButton("OK", null);
			builder.show();
		}
	}
*/
}