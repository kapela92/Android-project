package com.example.packard.myapplication;

/**
 * Created by Packard on 2015-08-10.
 */


        import java.util.List;

        //import com.example.packard.myapplication.model.oganizerzadanie;
        import android.app.Activity;
        import android.graphics.Paint;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

public class oganizerzadaniesAdapter extends ArrayAdapter<oganizerzadanie> {
    private Activity context;
    private List<oganizerzadanie> zadanies;
    public oganizerzadaniesAdapter(Activity context, List<oganizerzadanie> zadanies) {
        super(context, R.layout.oganizer_list_item, zadanies);
        this.context = context;
        this.zadanies = zadanies;
    }

    static class ViewHolder {
        public TextView tvoganizerDescription;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.oganizer_list_item, null, true);
            viewHolder = new ViewHolder();
            viewHolder.tvoganizerDescription = (TextView) rowView.findViewById(R.id.tvoganizerDescription);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        oganizerzadanie zadanie = zadanies.get(position);
        viewHolder.tvoganizerDescription.setText(zadanie.getDescription());
        if(zadanie.isCompleted()) {
            viewHolder.tvoganizerDescription
                    .setPaintFlags(viewHolder.tvoganizerDescription.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.tvoganizerDescription
                    .setPaintFlags(viewHolder.tvoganizerDescription.getPaintFlags() &
                            ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return rowView;
    }
}