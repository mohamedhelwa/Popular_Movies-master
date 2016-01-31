package com.androidapps.project.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidapps.project.popularmovies.data.MovieContract;


public class Utility {

    public static String getSortCriteria(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_default_popularity));
    }

    public static boolean isFavorited(Context context, String id) {
        Uri favorites = MovieContract.MovieEntry.CONTENT_URI;
        Cursor c=null;
        try {
          c = context.getContentResolver().query(favorites, null,
                    MovieContract.MovieEntry.TABLE_NAME + "." +
                            MovieContract.MovieEntry._ID + " = ?",
                    new String[]{id}
                    , MovieContract.MovieEntry._ID);

            int result=c.getCount();
            if(result>0){
                return  true;
            }
        }catch (Exception e){
            Log.e("cursor count : ",e.toString());

        }finally {
            if (c!= null){
                c.close();
            }
        }

        return false;

    }

    public static String Get_API_KEY() {
        return "fa85fde8cc46aa3bcefd0b02bb3cbcdc";
    }

    //Based on a stackoverflow snippet
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
