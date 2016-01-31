package com.androidapps.project.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidapps.project.popularmovies.data.MovieContract.MovieEntry;
import com.androidapps.project.popularmovies.data.MovieContract.ReviewEntry;
import com.androidapps.project.popularmovies.data.MovieContract.TrailerEntry;



public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //http://stackoverflow.com/questions/13641250/sqlite-delete-cascade-not-working
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY , " +
                MovieEntry.COLUMN_MOVIE_POSTER_URL + " TEXT UNIQUE , " +
                MovieEntry.COLUMN_MOVIE_DESCRIPTION + " TEXT , " +
                MovieEntry.COLUMN_MOVIE_RELEASE_YEAR + " TEXT , " +
                MovieEntry.COLUMN_MOVIE_DURATION + " REAL , " +
                MovieEntry.COLUMN_MOVIE_FAVORITE_OR_NOT + " REAL , " +
                MovieEntry.COLUMN_MOVIE_RATE + " REAL  ," +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT " +
                " );";



        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL , " +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL , " +
                ReviewEntry.COLUMN_REVIEWER_NAME + " TEXT , " +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT , " +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT , " +


                // Set up the movie column as a foreign key to location table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " + " ON DELETE CASCADE , "+

                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ", " +
                ReviewEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                TrailerEntry.COLUMN_TRAILER_ID + " INTEGER NOT NULL , " +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL , " +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT , " +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT , " +

                // Set up the movie column as a foreign key to location table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " + "  ON DELETE CASCADE , " +
                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_KEY + ", " +
                TrailerEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }


}

/*



 */