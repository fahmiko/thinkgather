package com.dev.thinkgather.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.dev.thinkgather.Activity.DetailPost;
import com.dev.thinkgather.Activity.TambahPublikasi;
import com.dev.thinkgather.Adapter.EventAdapter;
import com.dev.thinkgather.Adapter.PublikasiAdapter;
import com.dev.thinkgather.MapsActivity;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.ClickListenner;
import com.dev.thinkgather.Method.RecyclerTouchListener;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    @BindView(R.id.recycler_content) RecyclerView recyclerContent;
    @BindView(R.id.eventsplace) RecyclerView eventsplace;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter adapterEvent;
    private List<Publikasi> publikasiList;
    private List<Publikasi> publikasiMember;
    private ServicePublikasi service;
    private SearchView searchView;
    private SimpleCursorAdapter cursorAdapter;
    private Session session = Application.getSession();

    public static HomeFragment homeFragment;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initComponents();
        loadData();
        setAnimation();
        return view;
    }

    private void initComponents() {
        homeFragment = this;
        setHasOptionsMenu(true);
        publikasiList = new ArrayList<>();
        publikasiMember = new ArrayList<>();
        adapter = new PublikasiAdapter(getContext(), publikasiList);
        adapterEvent = new EventAdapter(getContext(), publikasiMember);
        service = ServiceClient.getClient().create(ServicePublikasi.class);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventsplace.setLayoutManager(linearLayoutManager);
        recyclerContent.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsplace.addOnItemTouchListener(new RecyclerTouchListener(getContext(), eventsplace, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                Publikasi publikasi = publikasiMember.get(position);
                startActivity(new Intent(getContext(), DetailPost.class).putExtra("Publikasi", publikasi));
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        }));
        eventsplace.setAdapter(adapterEvent);
        recyclerContent.setAdapter(adapter);
        recyclerContent.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerContent, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                Publikasi publikasi = publikasiList.get(position);
                startActivity(new Intent(getContext(), DetailPost.class).putExtra("Publikasi", publikasi));
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        }));

        final String[] from = new String[]{"keilmuan","lokasi"};
        final int[] to = new int[] {R.id.sr_judul,R.id.sr_location};
        cursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.search_list,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    private void setAnimation(){
        // snapping the scroll items
        final SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(eventsplace);
        /*
        // set a timer for default item
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1ms = 100ms
                RecyclerView.ViewHolder viewHolderDefault = eventsplace.
                        findViewHolderForAdapterPosition(0);
                LinearLayout eventparentDefault = viewHolderDefault.itemView.
                            findViewById(R.id.eventparent);

                eventparentDefault.animate().scaleY(1).scaleX(1).setDuration(350).
                        setInterpolator(new AccelerateInterpolator()).start();

                LinearLayout eventcategoryDefault = viewHolderDefault.itemView.
                        findViewById(R.id.eventbadge);
                eventcategoryDefault.animate().alpha(1).setDuration(300).start();

            }
        }, 100);
        */

        // add animate scroll
        eventsplace.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    View view = snapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(view);

                    RecyclerView.ViewHolder viewHolder =
                            eventsplace.findViewHolderForAdapterPosition(pos);

                    LinearLayout eventparent = viewHolder.itemView.findViewById(R.id.eventparent);
                    eventparent.animate().scaleY(1).scaleX(1).setDuration(350).
                            setInterpolator(new AccelerateInterpolator()).start();

                    LinearLayout eventcategory = viewHolder.itemView.
                            findViewById(R.id.eventbadge);
                    eventcategory.animate().alpha(1).setDuration(300).start();

                }
                else {

                    View view = snapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(view);

                    RecyclerView.ViewHolder viewHolder =
                            eventsplace.findViewHolderForAdapterPosition(pos);

                    LinearLayout eventparent = viewHolder.itemView.findViewById(R.id.eventparent);
                    eventparent.animate().scaleY(0.7f).scaleX(0.7f).
                            setInterpolator(new AccelerateInterpolator()).setDuration(350).start();

                    LinearLayout eventcategory = viewHolder.itemView.
                            findViewById(R.id.eventbadge);
                    eventcategory.animate().alpha(0).setDuration(300).start();
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    public void loadData() {
        service.getAllPublikasi().enqueue(new Callback<GetPublikasi>() {
            @Override
            public void onResponse(Call<GetPublikasi> call, Response<GetPublikasi> response) {
                if (response.code() == 200) {
                    publikasiList.clear();
                    publikasiMember.clear();
                    if (response.body().getResult().size() != 0) {
                        publikasiList.addAll(response.body().getResult());
                        int jml_publikasi = 0;
                        for(int i = 0; i < response.body().getResult().size(); i++){
                            if(publikasiList.get(i).getIdMember().equals(session.getStringLogin("id_member"))){
                                jml_publikasi++;
                                publikasiMember.add(response.body().getResult().get(i));
                            }
                        }
                        session.savePublikasi(""+jml_publikasi);
                    }
                    adapterEvent.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetPublikasi> call, Throwable t) {

            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_option, menu);
//        /*
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setSuggestionsAdapter(cursorAdapter);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Cari minat keilmuan");

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor cursor = (Cursor) cursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("keilmuan"));
                searchView.setQuery(txt, true);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) cursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("keilmuan"));
                checkMinat(txt);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                checkMinat(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
//                Toast.makeText(getContext(), SUGGESTIONS.length, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
//        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                startActivity(new Intent(getContext(), TambahPublikasi.class));
                break;
        }
        return false;
    }

    private void populateAdapter(String query){
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID , "keilmuan", "lokasi"});
        for (int i=0; i<publikasiList.size(); i++) {
            if (publikasiList.get(i).getMinat().toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, publikasiList.get(i).getMinat(), publikasiList.get(i).getInstitusi()});
        }
        cursorAdapter.changeCursor(c);
    }

    private void checkMinat(String queryText){
        List<Publikasi> filterPublikasi = new ArrayList<>();
        for(int i = 0; i < publikasiList.size(); i++){
            if(queryText.toLowerCase().equals(publikasiList.get(i).getMinat().toLowerCase())){
                filterPublikasi.add(publikasiList.get(i));
            }
        }
        if(filterPublikasi.size()==0){
            Toast.makeText(getContext(), "Data tidak ada", Toast.LENGTH_SHORT).show();
        }else{
            String[] strings = new String[filterPublikasi.size()];
            for(int i = 0; i < filterPublikasi.size(); i++){
                strings[i] = filterPublikasi.get(i).getInstitusi();
            }
            startActivity(new Intent(getContext(), MapsActivity.class).putExtra("minat", queryText).putExtra("location", strings));
        }

    }
}
