package com.androidapps.project.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidapps.project.popularmovies.R;
import com.androidapps.project.popularmovies.model.Trailer;

import java.util.ArrayList;


public class ListViewAdapter extends ArrayAdapter<Trailer> {
    ArrayList<Trailer> trailerData;
    Context context;
    private int layoutId;


    public ListViewAdapter(Context context, ArrayList<Trailer> trailerData, int layoutId) {
        super(context, layoutId, trailerData);
        this.context = context;
        this.trailerData = trailerData;
        this.layoutId = layoutId;

    }

    public void setTrailerData(ArrayList<Trailer> trailerData) {
        this.trailerData = trailerData;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return trailerData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vi = convertView;
        String url = null;


        if (vi == null) {

            vi = inflater.inflate(layoutId, parent, false);
            //inflater.inflate(R.layout.list_view_item, null);


            // set image based on selected text


        }

        Trailer trailerItem = trailerData.get(position);

        TextView trailerName = (TextView) vi
                .findViewById(R.id.tv_trailer_number);

        trailerName.setText(trailerItem.getName());

        //url = youtubeKey.get(position);
        // vi.setTag(url);


        return vi;

    }


}

