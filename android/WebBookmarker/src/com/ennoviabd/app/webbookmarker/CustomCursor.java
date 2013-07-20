package com.ennoviabd.app.webbookmarker;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public  class CustomCursor extends SimpleCursorAdapter {

    private Cursor cursor;
    private Context context;

    public CustomCursor(Context context, int textViewResourceId, Cursor cursor, String from[], int to[]) {
            super(context, textViewResourceId, cursor, from, to);
            this.cursor = cursor;
            this.context=context;            
    }
   @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);
	   View v =convertView; //super.getView(position, convertView, parent);
	  	
  	 ViewHolder vh=null;
   	  if (v == null) {
             LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             v = vi.inflate(R.layout.listview_row, null);
             
         	
             vh = new ViewHolder();
             vh.idn = (TextView)v.findViewById(R.id.txtBookMarkid);                                
             v.setTag(vh);
         }
         else
         {
             vh = (ViewHolder)v.getTag();
         }
   	 if (this.cursor != null && !this.cursor.isClosed()) {
         
     	
         this.cursor.moveToPosition(position);	
         
         
         vh.idn=(TextView) v.findViewById(R.id.txtBookMarkid);
     	vh.idn.setText(cursor.getString(cursor.getColumnIndex(BookMarkContent.BOOKMARK_IDN)));
     	vh.id=cursor.getString(cursor.getColumnIndex(BookMarkContent.BOOKMARK_IDN));
     	vh.position=position;
     	final TextView txtWeb = (TextView) v.findViewById(R.id.txtvWeb);
     	final   Button btnDel = (Button) v.findViewById(R.id.btnDdlDel);
     	txtWeb.setText(cursor.getString(cursor.getColumnIndex(BookMarkContent.BOOKMARK_WEB)));
     	btnDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewHolder vh = getViewHolder(v);
				//MainActivity.setOnDelHandle(vh.id.toString());// call mainactivity to perform delete function
				int rs=context.getContentResolver().delete(BookMarkContent.Content_Uri, BookMarkContent.BOOKMARK_IDN+"="+vh.id.toString(), null);
				
			}
		});
   	 }
    return v;
	   
	}
   public static class ViewHolder
   {
       private TextView idn;
       public int position;
       public String id;
       
   }
   public  ViewHolder getViewHolder(View v)
   {
       if(v.getTag() == null)
       {
           return getViewHolder((View)v.getParent());
       }
       return (ViewHolder)v.getTag();
   }

}
