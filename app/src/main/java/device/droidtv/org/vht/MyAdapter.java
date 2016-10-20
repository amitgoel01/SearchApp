package device.droidtv.org.vht;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by amit.goyal on 8/29/2016.
 */
public class MyAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> mArrayList;
    private Context mContext = null;
    public MyAdapter(Context context, ArrayList<Item> arrayList){
        super(context, R.layout.row_content, arrayList);
        mContext = context;
        mArrayList = arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_content, parent, false);

        TextView counntry = (TextView)rowView.findViewById(R.id.country);
        TextView name = (TextView)rowView.findViewById(R.id.name);
        TextView abbr = (TextView)rowView.findViewById(R.id.abbr);
        TextView area = (TextView)rowView.findViewById(R.id.area);
        TextView largest_city = (TextView)rowView.findViewById(R.id.largest_city);
        TextView capital = (TextView)rowView.findViewById(R.id.capital);

        counntry.setText(mArrayList.get(position).getCountry());
        name.setText(mArrayList.get(position).getName());
        abbr.setText(mArrayList.get(position).getAbbr());
        area.setText(mArrayList.get(position).getArea());
        if(mArrayList.get(position).getLargest_city() != null) {
            largest_city.setText(mArrayList.get(position).getLargest_city());
        }
        capital.setText(mArrayList.get(position).getCapital());
        return rowView;
    }

    public void setItemList(ArrayList<Item> list) {
        this.mArrayList = list;
    }
}
