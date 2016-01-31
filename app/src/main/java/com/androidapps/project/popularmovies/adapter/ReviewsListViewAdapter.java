package com.androidapps.project.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidapps.project.popularmovies.R;
import com.androidapps.project.popularmovies.model.Review;

import java.util.ArrayList;


public class ReviewsListViewAdapter extends ArrayAdapter<Review> {
    ArrayList<Review> reviewData;
    Context context;
    private int layoutId;


    public ReviewsListViewAdapter(Context context, ArrayList<Review> reviewData, int layoutId) {
        super(context, layoutId ,reviewData);
        this.context = context;
        this.reviewData = reviewData;
        this.layoutId = layoutId;

    }

    public void setReviewData(ArrayList<Review> reviewData) {
        this.reviewData = reviewData;
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return reviewData.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    //disable Click for listview
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vi = convertView;
        String url = null;



        if (vi == null) {

            vi = inflater.inflate(layoutId, null);

        }

        Review review=reviewData.get(position);
        TextView author = (TextView) vi
                .findViewById(R.id.tv_review_author);
        author.setText(review.getReviewrName());


        TextView reviewContent = (TextView) vi
                .findViewById(R.id.tv_review_content);
        reviewContent.setText(review.getContent());


        return vi;

    }





}


