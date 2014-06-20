package com.sopinet.android.mediauploader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SendingActivity extends Activity {
	private SendingActivityData[] dataBarArray;
	public SendingActivityList adaptador = null;
	public ListView lst = null;
	private AlertDialog alertDialog;
	private HttpPostSQL usdbh = null;
	private SQLiteDatabase dbr = null;
	private SQLiteDatabase dbw = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendingactivity_list);
		
		usdbh = HttpPostSQL.getInstance(SendingActivity.this);
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
	}
	
	private void refreshList() {
		if (dbr != null) {
        	//String[] args = new String[] {"sending"};
        	//Cursor c = db.rawQuery("SELECT indice, porcentage FROM http_index WHERE status=? ", args);
			Cursor c = dbr.rawQuery("SELECT indice, status, porcentage, item FROM http_index", null);
        	dataBarArray = new SendingActivityData[c.getCount()];

        	// Update information text
        	final TextView sendingText = (TextView)findViewById(R.id.sendingtext);
        	final int count = c.getCount();
        	SendingActivity.this.runOnUiThread(new Runnable() {
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
       	    	 dataBarArray[i] = new SendingActivityData();
       	    	 dataBarArray[i].indice = c.getString(0);
       	    	 // TODO: Check NULL
       	    	 try {
       	    		 dataBarArray[i].porcentage = Integer.valueOf(c.getString(2));
       	    	 } catch(Exception e) {
       	    		 dataBarArray[i].porcentage = 0;
       	    		 // Is null
       	    	 }
       	    	 dataBarArray[i].item = c.getString(3);
       	    	 i++;
       	     } while (c.moveToNext());
        	}
        	c.close();
		}

		adaptador = new SendingActivityList(SendingActivity.this, dataBarArray);   	
    	lst = (ListView)findViewById(R.id.list_sendingactivity);
    	SendingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	lst.setAdapter(adaptador);
            }
    	});
    	
        lst.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, final long id) {
    		    alertDialog = new AlertDialog.Builder(SendingActivity.this).create();
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