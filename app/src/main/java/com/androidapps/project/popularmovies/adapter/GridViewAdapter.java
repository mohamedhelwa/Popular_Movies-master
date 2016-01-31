package com.androidapps.project.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidapps.project.popularmovies.R;
import com.androidapps.project.popularmovies.model.GridItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private ArrayList<GridItem> mGridData=new ArrayList<>();
    private Context mContext;
    private int layoutResourceId;


    public GridViewAdapter(Context context,int layoutId,ArrayList<GridItem> gridData) {

        super(context, layoutId,gridData);
        this.layoutResourceId=layoutId;
        this.mContext = context;
        this.mGridData=gridData;

    }

    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGridData.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) view.findViewById(R.id.picture);

            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) view.getTag();
        }

        GridItem item = mGridData.get(position);

        String url = item.getPosterUrl();
        Picasso.with(mContext).load(url).placeholder(R.drawable.poster).into(viewHolder.image);

        view.setId(item.getId());
        return view;

    }


    private static class ViewHolder {
        public ImageView image;
    }


}















