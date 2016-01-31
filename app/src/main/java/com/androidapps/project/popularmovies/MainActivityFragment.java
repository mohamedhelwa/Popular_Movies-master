package com.androidapps.project.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidapps.project.popularmovies.adapter.GridViewAdapter;
import com.androidapps.project.popularmovies.data.MovieContract;
import com.androidapps.project.popularmovies.model.GridItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private GridView gridView;
    private ArrayList<GridItem> mGridData;
    private GridViewAdapter gridAdapter;
    private static final String SELECTED_KEY = "selected_position";
    private int mPosition = ListView.INVALID_POSITION;

    private static final String DETAILFRAGMENT_TAG = "details";


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

    }


    public void getFavoriteMovies() {

        GridItem item;
        Uri favorites = MovieContract.MovieEntry.CONTENT_URI;
        Cursor c = getActivity().getContentResolver().query(favorites, null, null, null, MovieContract.MovieEntry._ID);

        if (!c.moveToFirst()) {

        } else {
            do {
                item = new GridItem();

                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(MovieContract.MovieEntry._ID))));
                item.setPosterUrl(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL)));
                mGridData.add(item);


            } while (c.moveToNext());
        }

        if (!c.isClosed()) {
            c.close();
        }
        gridAdapter.setGridData(mGridData);
        gridView.smoothScrollToPosition(mPosition);
        gridView.setItemChecked(mPosition, true);


    }


    public void onSortChanged() {
        updateGridWithImages();

    }


    public interface Callback {
        public void onItemSelected(String id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        //Initialize with empty data
        mGridData = new ArrayList<>();
        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_view_item, mGridData);

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);

        //gridView.setSelection(mPosition);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);
                Log.e("item id",item.getId()+"");

                if (item != null) {
                    int movieID = item.getId();
                    ((Callback) getActivity()).onItemSelected(String.valueOf(movieID));
                }

                mPosition = position;


            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        }
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStart() {
        super.onStart();
        updateGridWithImages();
    }


    private void updateGridWithImages() {
        String sortBy = Utility.getSortCriteria(getActivity());
        if (sortBy.equalsIgnoreCase("favorite")) {
            mGridData.clear();
            getFavoriteMovies();
        } else {
            if (Utility.isNetworkAvailable(getActivity())) {
                FetchImagesTask task = new FetchImagesTask();
                task.execute(sortBy);
            } else {
                Toast.makeText(getActivity(), "check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////////////////////////////////////////////////////////
    public class FetchImagesTask extends AsyncTask<String, Void, ArrayList<GridItem>> {

        //get poaster path from jason
        private  ArrayList<GridItem> getImagesDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String POSTER_PATH = "poster_path";
            final String RESULTS = "results";
            final String ID = "id";

            GridItem item;
            mGridData.clear();
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            String BASE_URL = "http://image.tmdb.org/t/p/w185/";

            for (int i = 0; i < movieArray.length(); i++) {

                item = new GridItem();
                item.setId((int) (movieArray.getJSONObject(i).getLong(ID)));
                item.setPosterUrl(BASE_URL + movieArray.getJSONObject(i).getString(POSTER_PATH));
                mGridData.add(item);

            }

          return mGridData;
        }

        @Override
        protected void onPostExecute(ArrayList<GridItem> gridData) {
            try {
                if (gridData.size() != 0) {
                    gridAdapter.setGridData(gridData);
                    if (mPosition != GridView.INVALID_POSITION) {
                        gridView.smoothScrollToPosition(mPosition);
                        gridView.setItemChecked(mPosition, true);
                    }
                }


            } catch (Exception e) {

            }
        }

        @Override
        protected   ArrayList<GridItem> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String imagetJsonStr = null;
            Integer respond;


            try {

                // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
                String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                String SORT_BY = "sort_by";
                String API_KEY = "api_key";

                String sort = null;
                if (params[0].equalsIgnoreCase("Popularity")) {
                    sort = "popularity.desc";
                } else if (params[0].equalsIgnoreCase("highest rated")) {
                    sort = "vote_average.desc";
                }
                String key = Utility.Get_API_KEY();


                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(SORT_BY, sort).
                        appendQueryParameter(API_KEY, key).
                        build();
                URL url = new URL(uriBuilder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                imagetJsonStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {
                return getImagesDataFromJson(imagetJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


    }


}
