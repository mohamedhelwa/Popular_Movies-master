package com.androidapps.project.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.androidapps.project.popularmovies.data.MovieContract.MovieEntry;
import com.androidapps.project.popularmovies.data.MovieContract.TrailerEntry;
import com.androidapps.project.popularmovies.data.MovieContract.ReviewEntry;




/**
 * Created by mohammed on 05/10/15.
 */
public class TestDB extends AndroidTestCase {
    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }


    public void testInsertReadDb() {

        // movie test data
        int test_movie_id = 125;
        String test_movie_poster_url = "http://www.google.com";
        String test_movie_description = "this is test movie";
        String test_movie_release_year = "2015";
        double test_movie_duration = 111;
        double test_movie_favorite_or_not = 1;
        double test_movie_rate = 8;

        // trailer test data
        int test_trailer_id=555;
        int  test_trailer_movie_key=125;
        String test_trailer_key="5df666dfsd";
        String test_trailer_name="trailer 1";


        // review test data
        int test_review_id=777;
        int test_review_movie_key=125;
        String test_reviewer_name="mohammed";
        String test_review_content="this is the content of the review";

                MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry._ID, test_movie_id);
        movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER_URL, test_movie_poster_url);
        movieValues.put(MovieEntry.COLUMN_MOVIE_DESCRIPTION, test_movie_description);
        movieValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_YEAR, test_movie_release_year);
        movieValues.put(MovieEntry.COLUMN_MOVIE_DURATION, test_movie_duration);
        movieValues.put(MovieEntry.COLUMN_MOVIE_FAVORITE_OR_NOT, test_movie_favorite_or_not);
        movieValues.put(MovieEntry.COLUMN_MOVIE_RATE, test_movie_rate);


        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue(movieRowId != -1);
        Log.e(LOG_TAG, "new row id : " + movieRowId);

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_ID, test_trailer_id);
        trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, test_trailer_movie_key);
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, test_trailer_key);
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_NAME, test_trailer_name);



        long trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue(trailerRowId != -1);
        Log.e(LOG_TAG, "new row id : " + trailerRowId);


        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, test_review_id);
        reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, test_review_movie_key);
        reviewValues.put(ReviewEntry.COLUMN_REVIEWER_NAME, test_reviewer_name);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_CONTENT, test_review_content);



        long reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue(reviewRowId != -1);
        Log.e(LOG_TAG, "new row id : " + reviewRowId);





        /// read movie data
        String MovieColumns[] = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_POSTER_URL,
                MovieEntry.COLUMN_MOVIE_DESCRIPTION,
                MovieEntry.COLUMN_MOVIE_RELEASE_YEAR,
                MovieEntry.COLUMN_MOVIE_DURATION,
                MovieEntry.COLUMN_MOVIE_FAVORITE_OR_NOT,
                MovieEntry.COLUMN_MOVIE_RATE
        };

        Cursor movieCursor = db.query(
                MovieEntry.TABLE_NAME,
                MovieColumns,
                null,
                null,
                null,
                null,
                null

        );


        /// read trailer data
        String TrailerColumns[] = {
                TrailerEntry.COLUMN_TRAILER_ID,
                TrailerEntry.COLUMN_MOVIE_KEY,
                TrailerEntry.COLUMN_TRAILER_KEY,
                TrailerEntry.COLUMN_TRAILER_NAME
        };

        Cursor trailerCursor = db.query(
                TrailerEntry.TABLE_NAME,
                TrailerColumns,
                null,
                null,
                null,
                null,
                null

        );


        /// read trailer data
        String ReviewColumns[] = {
               ReviewEntry.COLUMN_REVIEW_ID,
                ReviewEntry.COLUMN_MOVIE_KEY,
                ReviewEntry.COLUMN_REVIEWER_NAME,
                ReviewEntry.COLUMN_REVIEW_CONTENT
        };

        Cursor reviewCursor = db.query(
                ReviewEntry.TABLE_NAME,
                ReviewColumns,
                null,
                null,
                null,
                null,
                null

        );




        // read movie data
        if (movieCursor.moveToFirst()) {

            int movie_id_Index = movieCursor.getColumnIndex(MovieEntry._ID);
            int id = movieCursor.getInt(movie_id_Index);

            int movie_poster_url_Index = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER_URL);
            String poster = movieCursor.getString(movie_poster_url_Index);

            int movie_description_Index = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_DESCRIPTION);
            String description = movieCursor.getString(movie_description_Index);

            int movie_release_year_Index = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_YEAR);
            String release_year = movieCursor.getString(movie_release_year_Index);

            int movie_duration_Index = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_DURATION);
            double duration = movieCursor.getDouble(movie_duration_Index);

            int movie_favorite_Index = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_FAVORITE_OR_NOT);
            double favorite_or_not = movieCursor.getDouble(movie_favorite_Index);

            int movie_rate_Index = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATE);
            double rate = movieCursor.getDouble(movie_rate_Index);


            assertEquals(test_movie_id, id);
            assertEquals(test_movie_description, description);
            assertEquals(test_movie_duration, duration);
            assertEquals(test_movie_poster_url, poster);
            assertEquals(test_movie_rate, rate);
            assertEquals(test_movie_release_year, release_year);
            assertEquals(test_movie_favorite_or_not, favorite_or_not);


        }else {
            fail("no values returned :(");
        }

        // read trailer data
        if (trailerCursor.moveToFirst()) {

            int trailer_id_Index = trailerCursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_ID);
            int id = trailerCursor.getInt(trailer_id_Index);

            int trailer_movie_key_Index = trailerCursor.getColumnIndex(TrailerEntry.COLUMN_MOVIE_KEY);
            int movie_key = trailerCursor.getInt(trailer_movie_key_Index);

            int trailer_key_Index = trailerCursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_KEY);
            String trailer_key = trailerCursor.getString(trailer_key_Index);

            int trailer_name_Index = trailerCursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_NAME);
            String trailer_name = trailerCursor.getString(trailer_name_Index);



            assertEquals(test_trailer_id, id);
            assertEquals(test_trailer_movie_key, movie_key);
            assertEquals(test_trailer_key,trailer_key);
            assertEquals(test_trailer_name, trailer_name);


        }else {
            fail("no values returned :(");
        }


        // read review data
        if (reviewCursor.moveToFirst()) {

            int review_id_Index = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_ID);
            int id = reviewCursor.getInt(review_id_Index);

            int review_movie_key_Index = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_MOVIE_KEY);
            int movie_key = reviewCursor.getInt(review_movie_key_Index);

            int reviewer_name_Index = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_REVIEWER_NAME);
            String reviewer_name = reviewCursor.getString(reviewer_name_Index);

            int review_content_Index = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_CONTENT);
            String review_content = reviewCursor.getString(review_content_Index);



            assertEquals(test_review_id, id);
            assertEquals(test_review_movie_key, movie_key);
            assertEquals(test_reviewer_name,reviewer_name);
            assertEquals(test_review_content, review_content);


        }else {
            fail("no values returned :(");
        }


    }


}
