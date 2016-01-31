package com.androidapps.project.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {
    private static final String DETAILFRAGMENT_TAG = "details";
    boolean mTwoPane = false;
    private String mSortby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortby=Utility.getSortCriteria(this);


        if (findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new ViewItemDetailsActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }


        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortByLocal = Utility.getSortCriteria(this);
        if (sortByLocal != null && !sortByLocal.equals(mSortby)) {
            MainActivityFragment mainFragment = (MainActivityFragment) getSupportFragmentManager().
                    findFragmentById(R.id.grid_movie_poster);
            if (null != mainFragment) {
                mainFragment.onSortChanged();
            }
            ViewItemDetailsActivityFragment DetailsFragment = (ViewItemDetailsActivityFragment) getSupportFragmentManager().
                    findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != DetailsFragment) {
                DetailsFragment.onSortChanged(sortByLocal);
            }
            mSortby = sortByLocal;
        }
    }




    @Override
    public void onItemSelected(String id) {

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString("id_land", id);

            ViewItemDetailsActivityFragment fragment = new ViewItemDetailsActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent i = new Intent(this, ViewItemDetailsActivity.class);
            i.putExtra("movie_id", id);
            startActivity(i);
        }


    }
}
