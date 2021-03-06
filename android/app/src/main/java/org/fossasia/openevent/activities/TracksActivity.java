package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;

import butterknife.BindView;

/**
 * User: MananWason
 * Date: 14-06-2015
 */
public class TracksActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    final private String SEARCH = "org.fossasia.openevent.searchText";

    private SessionsListAdapter sessionsListAdapter;

    private String track;

    private String searchText = "";

    private SearchView searchView;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView sessionsRecyclerView;
    @BindView(R.id.txt_no_sessions) TextView noSessionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DbSingleton dbSingleton = DbSingleton.getInstance();
        track = getIntent().getStringExtra(ConstantStrings.TRACK);

        if (!TextUtils.isEmpty(track))
            toolbar.setTitle(track);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sessionsListAdapter = new SessionsListAdapter(this, dbSingleton.getSessionbyTracksname(track));
        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        if (sessionsListAdapter.getItemCount() != 0) {
            noSessionsView.setVisibility(View.GONE);
            sessionsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tracks;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_sessions:
                return true;

            case R.id.action_share_sessions:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.SESSIONS);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_links)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_tracks).getActionView();
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!TextUtils.isEmpty(query)) {
            sessionsListAdapter.setTrackName(track);
            sessionsListAdapter.getFilter().filter(query);
        } else {
            sessionsListAdapter.setTrackName(track);
            sessionsListAdapter.refresh();
        }
        searchText = query;
        return true;
    }
}