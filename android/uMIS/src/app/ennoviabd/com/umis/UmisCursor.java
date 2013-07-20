package app.ennoviabd.com.umis;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public  class UmisCursor extends SimpleCursorAdapter {

    private Cursor cursor;
    private Context context;

    public UmisCursor(Context context, int textViewResourceId, Cursor cursor, String from[], int to[]) {
            super(context, textViewResourceId, cursor, from, to);
            this.cursor = cursor;
            this.context=context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder vh=null;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.fillgridview, null);
            	
                vh = new ViewHolder();
                vh.idn = (TextView)v.findViewById(R.id.txt_gv_studid);                                
                v.setTag(vh);
            }
            else
            {
                vh = (ViewHolder)v.getTag();
            }
            cursor.moveToPosition(position);
            if (cursor != null) {
            	final TextView lt = (TextView) v.findViewById(R.id.txt_gv_studid);
            	final   TextView rt = (TextView) v.findViewById(R.id.txt_gv_studname);
            	vh.idn=(TextView) v.findViewById(R.id.txt_gv_studname);
            	vh.idn.setText(cursor.getString(1));
            	vh.id=cursor.getString(2);
            	vh.position=position;
            	lt.setText(cursor.getString(1));
            	rt.setText(cursor.getString(2));
            	//TextView edttxt = (TextView) v.findViewById(R.id.txt_gv_edit);
            	//edttxt.setText("Edit");
            	
            	
            	/*
            	
            	lt.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//cursor.moveToPosition(position);
						//TextView textViewTwo = (TextView) v.findViewById(R.id.txt_gv_studname);
						//Toast.makeText(context,textViewTwo.getText(), Toast.LENGTH_LONG).show();
						//v.gett
						ViewHolder vh = getViewHolder(v);
						Toast.makeText(context,String.valueOf(vh.position), Toast.LENGTH_LONG).show();
						//AdmissionActivity.onRecordSelectClick(String.valueOf(vh.position),context);
						
						
					}
				});*/
            	
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
