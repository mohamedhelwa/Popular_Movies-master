package com.androidapps.project.popularmovies.data;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


public class MovieProvider extends ContentProvider {

    private final String LOG_TAG = MovieProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_WITH_ID = 201;
    static final int REVIEW = 300;
    static final int REVIEW_WITH_ID = 301;
    static final int MOVIE_WITH_TRAILER_WITH_REVIEW = 400;

    private static final SQLiteQueryBuilder sMovieByTrailerByReviewQueryBuilder;

    static {
        sMovieByTrailerByReviewQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieByTrailerByReviewQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailerEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " INNER JOIN " +

                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY);
    }

    

    //movie id = ?
    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";

    /*  select    movie._id , movie_title, movie_release_year,trailer_name,reviewer_name
              from
      movie  left outer join trailer on movie._id= trailer.movie_id
      left outer join review on    trailer.movie_id= review.movie_id

      where movie._id=257344*/
    private static final String select =
            " SELECT "
                    + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + ", " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_RATE + ", " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_DURATION + ", " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR + ", " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL + ", " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION + ", " +

                    MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + ", " +
                    MovieContract.TrailerEntry.COLUMN_TRAILER_KEY + ", " +
                    MovieContract.TrailerEntry.COLUMN_TRAILER_ID+", "+


                    MovieContract.ReviewEntry.COLUMN_REVIEWER_NAME+", "+
                    MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT+ ", " +
                    MovieContract.ReviewEntry.COLUMN_REVIEW_ID+ ", " +
                    MovieContract.ReviewEntry.COLUMN_REVIEW_URL+" "+



                    " FROM "+
    MovieContract.MovieEntry.TABLE_NAME+"  LEFT OUTER JOIN "+
    MovieContract.TrailerEntry.TABLE_NAME+
            " ON "+MovieContract.MovieEntry.TABLE_NAME+
            "."+MovieContract.MovieEntry._ID+
            " =  "+MovieContract.TrailerEntry.TABLE_NAME+
            "."+MovieContract.TrailerEntry.COLUMN_MOVIE_KEY
    +" LEFT OUTER JOIN "+

    MovieContract.ReviewEntry.TABLE_NAME+
            " ON "+MovieContract.TrailerEntry.TABLE_NAME+
            "."+MovieContract.TrailerEntry.COLUMN_MOVIE_KEY+
            " = "+MovieContract.ReviewEntry.TABLE_NAME+
            "."+MovieContract.ReviewEntry.COLUMN_MOVIE_KEY+
            " WHERE "+
    MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID+" = ?";

    private Cursor MovieJoinTrailerJoinReview(Uri uri, String[] projection, String sortOrder) {
        String movie_id = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;
        String selection;


        selection = sMovieIdSelection;
        selectionArgs = new String[]{movie_id};


        return sMovieByTrailerByReviewQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_TRAILER_WITH_REVIEW);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_TRAILER_WITH_REVIEW:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie/#"
            case MOVIE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_TRAILER_WITH_REVIEW: {


                String[] args = {String.valueOf(uri.getLastPathSegment())};
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(select, args);
                // retCursor = MovieJoinTrailerJoinReview(uri, projection, sortOrder);
                break;
            }
            // "trailer"
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "review"
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();    //gets the id
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""), selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_WITH_ID:

                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        MovieContract.MovieEntry._ID +
                                " = " + uri.getLastPathSegment() +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""), selectionArgs);
                break;

            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case TRAILER:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
