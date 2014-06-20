package com.sopinet.android.mediauploader;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

//we will present the available selection in a list dialog, so we need an adapter
class CropOptionAdapter extends ArrayAdapter<CropOption>
{
 private List<CropOption> _items;
 private Context _ctx;

 CropOptionAdapter(Context ctx, List<CropOption> items)
 {
     super(ctx, R.layout.crop_selector, items);
     _items = items;
     _ctx = ctx;
 }

 @Override
 public View getView( int position, View convertView, ViewGroup parent )
 {
     if ( convertView == null )
         convertView = LayoutInflater.from( _ctx ).inflate( R.layout.crop_selector, null );

     CropOption item = _items.get( position );
     if ( item != null )
     {
         ( ( ImageView ) convertView.findViewById( R.id.iv_icon ) ).setImageDrawable( item.ICON );
         ( ( TextView ) convertView.findViewById( R.id.tv_name ) ).setText( item.TITLE );
         return convertView;
     }
     return null;
 }
}