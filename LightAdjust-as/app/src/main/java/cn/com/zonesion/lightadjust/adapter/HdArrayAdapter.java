package cn.com.zonesion.lightadjust.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HdArrayAdapter extends ArrayAdapter<String>{
		
	 private Context mContext;
	    private String[] mStringArray;

	    public HdArrayAdapter(@NonNull Context context, String[] stringArray) {
	        super(context, android.R.layout.simple_spinner_item,stringArray);
	        mContext = context;
	        mStringArray = stringArray;
	    }

	    @Override
	    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
	        //修改Spinner展开后的字体颜色
	        if(convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(mContext);
	            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent,false);
	        }
	        //此处text1是Spinner默认的用来显示文字的TextView
	        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
	        tv.setText(mStringArray[position]);
	        tv.setTextSize(18f);
	        tv.setTextColor(Color.RED);
	        return tv;
	    }

	    @NonNull
	    @Override
	    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
	        // 修改Spinner选择后结果的字体颜色
	        if(convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(mContext);
	            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent,false);
	        }
	        //此处text1是Spinner默认的用来显示文字的TextView
	        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
	        tv.setText(mStringArray[position]);
	        tv.setTextSize(13f);
	        tv.setTextColor(Color.BLUE);
	        return tv;
	    }
}
