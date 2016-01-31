package com.androidapps.project.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidapps.project.popularmovies.adapter.ListViewAdapter;
import com.androidapps.project.popularmovies.adapter.ReviewsListViewAdapter;
import com.androidapps.project.popularmovies.data.MovieContract;
import com.androidapps.project.popularmovies.model.Movie;
import com.androidapps.project.popularmovies.model.Review;
import com.androidapps.project.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

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
public class ViewItemDetailsActivityFragment extends Fragment implements View.OnClickListener {

    ImageButton favorite;
    View viewLine;
    TextView trailersHeader;
    TextView reviewsHeader;
    TextView rate;
    TextView relaseYear;
    TextView movieTile;
    TextView duration;
    TextView description;
    ImageView poster;
    ListView listViewTrailers;
    ListView listViewReviews;
    ListViewAdapter trailersListAdapter;
    ReviewsListViewAdapter reviewsListAdapter;
    String Movie_id = null;
    private ShareActionProvider mShareActionProvider;
    private String trailerURL;
    private ArrayList<Trailer> trailerData;
    private Movie movie;
    private Review review;
    private Trailer trailer;
    private ArrayList<Review> reviewData;


    static final String DETAIL_URI = "URI";

    // private Cursor cursor;
    public ViewItemDetailsActivityFragment() {
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle args = getArguments();
        if (args != null) {

            if (args.getString("id_intent") != null) {
                Movie_id = args.getString("id_intent");
            } else {
                Movie_id = args.getString("id_land");
            }
        } else {
            Movie_id = null;//getActivity().getIntent().getStringExtra("movie_id");
        }
        View view = inflater.inflate(R.layout.fragment_view_item_details, container, false);

        initComponent(view);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.


        inflater.inflate(R.menu.menu_view_item_details_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (trailerURL != null) {
            mShareActionProvider.setShareIntent(createShareTrailerURLIntent());
        }
    }

    private Intent createShareTrailerURLIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + trailerURL);
        return shareIntent;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponent(View view) {


        trailersHeader = (TextView) view.findViewById(R.id.tv_trailers_header);
        reviewsHeader = (TextView) view.findViewById(R.id.tv_reviews_header);
        viewLine = (View) view.findViewById(R.id.view_line);

        favorite = (ImageButton) view.findViewById(R.id.btn_favorite);

        movie = new Movie();
        review = new Review();
        trailer = new Trailer();

        rate = (TextView) view.findViewById(R.id.tv_rate);
        relaseYear = (TextView) view.findViewById(R.id.tv_year);
        movieTile = (TextView) view.findViewById(R.id.tv_title);
        duration = (TextView) view.findViewById(R.id.tv_duration);
        description = (TextView) view.findViewById(R.id.tv_description);
        poster = (ImageView) view.findViewById(R.id.iv_movie_poster);


        listViewTrailers = (ListView) view.findViewById(R.id.list_trails);
        listViewReviews = (ListView) view.findViewById(R.id.list_reviews);

        trailerData = new ArrayList<>();
        trailersListAdapter = new ListViewAdapter(getActivity(), trailerData, R.layout.list_view_item);

        reviewData = new ArrayList<>();
        reviewsListAdapter = new ReviewsListViewAdapter(getActivity(), reviewData, R.layout.reviews_list_item);

        listViewTrailers.setAdapter(trailersListAdapter);
        listViewReviews.setAdapter(reviewsListAdapter);


        listViewTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + view.getTag().toString()));
//                    startActivity(intent);
//                } catch (ActivityNotFoundException ex) {
                Trailer trailer = (Trailer) parent.getItemAtPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
                // }
                // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(view.getTag().toString()));
                // startActivity(intent);
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));
            }
        });

        favorite.setOnClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();
        updateDetailsActivity();


    }

    private void updateDetailsActivity() {

        trailerData.clear();
        reviewData.clear();
        String id = Movie_id;
        FetchDataTask task1 = new FetchDataTask();
        FetchTrailersVideosTask task2 = new FetchTrailersVideosTask();
        FetchReviewsTask task3 = new FetchReviewsTask();
        if (id != null && Utility.isNetworkAvailable(getActivity())) {

            task1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
            task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
        } else if (id != null && Utility.isFavorited(getActivity(), id)&& (!Utility.isNetworkAvailable(getActivity()))) {
            showFavoriteDetails(id);
        }
    }

    private void showFavoriteDetails(String id) {

        String COLUMNS[] = new String[]{
                MovieContract.MovieEntry.TABLE_NAME + '.' + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.TABLE_NAME + '.' + MovieContract.MovieEntry.COLUMN_MOVIE_RATE,
                MovieContract.MovieEntry.TABLE_NAME + '.' + MovieContract.MovieEntry.COLUMN_MOVIE_DURATION,
                MovieContract.MovieEntry.TABLE_NAME + '.' + MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR,
                MovieContract.MovieEntry.TABLE_NAME + '.' + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL,
                MovieContract.MovieEntry.TABLE_NAME + '.' + MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION,

                MovieContract.TrailerEntry.TABLE_NAME + '.' + MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
                MovieContract.TrailerEntry.TABLE_NAME + '.' + MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,
                MovieContract.TrailerEntry.TABLE_NAME + '.' + MovieContract.TrailerEntry.COLUMN_TRAILER_ID,

                MovieContract.ReviewEntry.TABLE_NAME + '.' + MovieContract.ReviewEntry.COLUMN_REVIEWER_NAME,
                MovieContract.ReviewEntry.TABLE_NAME + '.' + MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
                MovieContract.ReviewEntry.TABLE_NAME + '.' + MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
                MovieContract.ReviewEntry.TABLE_NAME + '.' + MovieContract.ReviewEntry.COLUMN_REVIEW_URL


        };


        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        Cursor c = getActivity().getContentResolver().query(ContentUris.withAppendedId(
                        MovieContract.MovieEntry.CONTENT_URI, Long.decode(id)), COLUMNS, null, null,
                null);


        // when checking for an custom object in Arraylist
        // http://examples.javacodegeeks.com/core-java/util/list/java-list-contains-method-example/
        try {
            if (c.moveToFirst()) {


                do {
                    trailer = new Trailer();
                    review = new Review();

                    if (c.getPosition() == 0) {
                        movieTile.setVisibility(View.VISIBLE);
                        rate.setText(c.getLong(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATE)) + "/10");
                        relaseYear.setText(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR)));
                        movieTile.setText(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
                        duration.setText(c.getLong(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DURATION)) + "min");
                        description.setText(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION)));
                        favorite.setBackgroundResource(R.drawable.ic_star_black_48dp);
                        String url = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL));
                        Picasso.with(getActivity()).load(url).into(poster);

                    }


                    trailer.setId(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_ID)));
                    trailer.setKey(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY)));
                    trailer.setName(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME)));


                    if (trailerData.contains(trailer)) {
                        // do nothing

                    } else if (trailer.getId() != null) {
                        trailerData.add(trailer);
                    }

                    review.setId(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_ID)));
                    review.setUrl(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_URL)));
                    review.setReviewrName(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEWER_NAME)));
                    review.setContent(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT)));


                    if (reviewData.contains(review)) {
                        // do nothing
                    } else if (review.getId() != null) {
                        reviewData.add(review);

                    }

                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.e("exception : ", ex.toString());
        } finally {
            if (!c.isClosed() || c != null) {
                c.close();
            }
        }


        if (trailerData.size() > 0) {
            trailersListAdapter.setTrailerData(trailerData);
            trailersHeader.setVisibility(View.VISIBLE);
            trailerURL = trailerData.get(0).getKey();


        }
        if (reviewData.size() > 0) {
            reviewsListAdapter.setReviewData(reviewData);
            reviewsHeader.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_favorite:

                updateFavorite();
                break;
        }


    }


    private void updateFavorite() {
        // check first if it is in favorite
        Uri favorites = MovieContract.MovieEntry.CONTENT_URI;

        boolean isFavorite = Utility.isFavorited(getActivity(), Movie_id);

        if (isFavorite) {
            Uri deleteUri = MovieContract.MovieEntry.CONTENT_URI;
            int rowdeleted = getActivity().getContentResolver().delete(deleteUri,
                    MovieContract.MovieEntry._ID + " =?", new String[]{Movie_id});
            if (rowdeleted > 0) {
                favorite.setBackgroundResource(R.drawable.ic_star_outline_black_48dp);
            }
        } else {
            try {
                // Add a new Movie record
                if (movie != null) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry._ID, Integer.parseInt(Movie_id));
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, movie.getPosterUrl());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movie.getDescription());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR,movie.getYear());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DURATION, movie.getDuration());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE_OR_NOT, 1);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATE, movie.getRate());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());

                    Uri uriMovie = getActivity().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI, movieValues);
                }
                // add new review record
                if (reviewData.size() > 0) {
                    ContentValues reviewValues;
                    for (Review review : reviewData) {
                        reviewValues = new ContentValues();
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movie.getId());
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEWER_NAME, review.getReviewrName());
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());
                        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, review.getUrl());

                        Uri uriReview = getActivity().getContentResolver().insert(
                                MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
                    }

                }

                // add new trailer record
                if (trailerData.size() > 0) {
                    ContentValues trailerValues;
                    for (Trailer trailer : trailerData) {
                        trailerValues = new ContentValues();
                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movie.getId());
                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());

                        Uri uriTrailer = getActivity().getContentResolver().insert(
                                MovieContract.TrailerEntry.CONTENT_URI, trailerValues);

                    }
                }
                favorite.setBackgroundResource(R.drawable.ic_star_black_48dp);

            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onSortChanged(String sortByLocal) {
        String sortby = sortByLocal;
        if (null != sortby) {

//            if(sortby.equalsIgnoreCase("favorite")){
//                getFavoriteMovies();
//            }else {
            FetchDataTask task = new FetchDataTask();
            task.execute(sortby);
            // }
        }
    }


    ////////////////////////////////////////////////////////////////////
    public class FetchDataTask extends AsyncTask<String, Void, Movie> {


        //get poaster path from jason
        private Movie getImagesDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String RUNTIME = "runtime";
            final String ID = "id";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            movie = new Movie();

            movie.setTitle(movieJson.getString(ORIGINAL_TITLE));
            movie.setPosterUrl(BASE_URL + movieJson.getString(POSTER_PATH));
            movie.setDescription(movieJson.getString(OVERVIEW));
            movie.setRate(movieJson.getLong(VOTE_AVERAGE));
            movie.setYear(movieJson.getString(RELEASE_DATE));
            movie.setDuration(movieJson.getLong(RUNTIME));
            movie.setId(movieJson.getInt(ID));
            movie.setFavorite(0);
            return movie;

        }


        @Override
        protected void onPostExecute(Movie movie) {

            final String BASE_URL = "http://image.tmdb.org/t/p/w185/";


            try {

                if (movie != null) {
                    movieTile.setVisibility(View.VISIBLE);
                    rate.setText(movie.getRate() + "/10");
                    relaseYear.setText(movie.getYear());
                    movieTile.setText(movie.getTitle());
                    duration.setText(movie.getDuration() + "min");
                    description.setText(movie.getDescription());

                    String url = BASE_URL + movie.getPosterUrl();
                    //favorite.setTag(url);
                    Picasso.with(getActivity()).load(url).into(poster);

                    if (Utility.isFavorited(getActivity(), Movie_id)) {
                        favorite.setBackgroundResource(R.drawable.ic_star_black_48dp);
                    } else {
                        favorite.setBackgroundResource(R.drawable.ic_star_outline_black_48dp);

                    }

                } else {
                    Log.e("movie ", "is null");

                }
            } catch (Exception ex) {
                Log.e("movie exception", ex.toString());
            }

        }

        @Override
        protected Movie doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String imagetJsonStr = null;


            try {

                // http://api.themoviedb.org/3/movie/307081?api_key=[YOUR_API_KEY]
                String BASE_URL = "http://api.themoviedb.org/3/movie/";
                String API_KEY = "api_key";
                String key = Utility.Get_API_KEY();//"[YOUR API KEY]";


                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY, key)
                        .build();
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
            } catch (
                    IOException e
                    )

            {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally

            {
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
            } catch (
                    JSONException e
                    )

            {
                e.printStackTrace();
            }

            return null;
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    public class FetchTrailersVideosTask extends AsyncTask<String, Void, ArrayList<Trailer>> {


        //get poaster path from jason
        private ArrayList<Trailer> getTrailerDataFromJson(String movieJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.

            final String RESULTS = "results";
            final String NAME = "name";
            final String KEY = "key";
            final String ID = "id";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray(RESULTS);


            for (int i = 0; i < movieJsonArray.length(); i++) {

                trailer = new Trailer();
                JSONObject objectData = movieJsonArray.getJSONObject(i);
                trailer.setId(objectData.getString(ID));
                trailer.setName(objectData.getString(NAME));
                trailer.setKey(objectData.getString(KEY));

                trailerData.add(trailer);

            }

            return trailerData;


        }


        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String imagetJsonStr = null;


            try {

                // http://api.themoviedb.org/3/movie/99861/videos?api_key=[YOUR API KEY]
                String BASE_URL = "http://api.themoviedb.org/3/movie/";
                String API_KEY = "api_key";
                String VIDEOS = "videos";
                String key = Utility.Get_API_KEY();//"[YOUR API KEY]";


                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendPath(VIDEOS)
                        .appendQueryParameter(API_KEY, key)
                        .build();
                URL url = new URL(uriBuilder.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
            } catch (
                    IOException e
                    )

            {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally

            {
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

            try

            {
                return getTrailerDataFromJson(imagetJsonStr);
            } catch (
                    JSONException e
                    )

            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {

            //  final String BASE_URL = "https://www.youtube.com/watch?v=";

            try {
                if (trailers.size() != 0) {
                    trailersListAdapter.setTrailerData(trailers);
                    trailersHeader.setVisibility(View.VISIBLE);
                    trailerURL = trailers.get(0).getKey();
                    mShareActionProvider.setShareIntent(createShareTrailerURLIntent());

                }else {
                    mShareActionProvider.setShareIntent(null);
                }


            } catch (Exception e) {

            }
        }

    }


    ///////////////////////////////////////////////////////////////////////////////
    //http://api.themoviedb.org/3/movie/16642/reviews?api_key=[YOUR API KEY]
    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {


        //get poaster path from jason
        private ArrayList<Review> getReviewsDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            final String RESULTS = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String URL = "url";
            final String ID = "id";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray(RESULTS);


            for (int i = 0; i < movieJsonArray.length(); i++) {
                try {
                    review = new Review();
                    JSONObject objectData = movieJsonArray.getJSONObject(i);
                    review.setId(objectData.getString(ID));
                    review.setUrl(objectData.getString(URL));
                    review.setReviewrName(objectData.getString(AUTHOR));
                    review.setContent(objectData.getString(CONTENT));

                    reviewData.add(review);
                } catch (Exception ex) {

                }
            }


            return reviewData;


        }


        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {


            try {
                if (reviews.size() != 0) {
                    reviewsHeader.setVisibility(View.VISIBLE);
                    reviewsListAdapter.setReviewData(reviews);
                }


            } catch (Exception ex) {
            }

        }


        @Override
        protected ArrayList<Review> doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String imagetJsonStr = null;


            try {

                //http://api.themoviedb.org/3/movie/16642/reviews?api_key=[YOUR API KEY]
                String BASE_URL = "http://api.themoviedb.org/3/movie/";
                String API_KEY = "api_key";
                String REVIEWS = "reviews";
                String key = Utility.Get_API_KEY();//"[YOUR API KEY]";


                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendPath(REVIEWS)
                        .appendQueryParameter(API_KEY, key)
                        .build();
                URL url = new URL(uriBuilder.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
            } catch (IOException e)

            {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally

            {
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

            try

            {
                return getReviewsDataFromJson(imagetJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}


///////////////////////////////////////////////////////////////////////////////


//https://www.youtube.com/watch?v=xInh3VhAWs8