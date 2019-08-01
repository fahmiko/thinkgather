package com.dev.thinkgather.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.dev.thinkgather.Activity.DetailPost;
import com.dev.thinkgather.Activity.TambahPublikasi;
import com.dev.thinkgather.Adapter.EventAdapter;
import com.dev.thinkgather.Adapter.PublikasiAdapter;
import com.dev.thinkgather.MapsActivity;
import com.dev.thinkgather.Method.ClickListenner;
import com.dev.thinkgather.Method.RecyclerTouchListener;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    @BindView(R.id.recycler_content) RecyclerView recyclerContent;
    @BindView(R.id.eventsplace) RecyclerView eventsplace;

    LinearLayoutManager linearLayoutManager;
    RecyclerView.Adapter adapter;
    RecyclerView.Adapter adapterEvent;
    List<Publikasi> publikasiList;
    ServicePublikasi service;
    SearchView searchView;
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
        setHasOptionsMenu(true);
        homeFragment = this;
        publikasiList = new ArrayList<>();
        adapter = new PublikasiAdapter(getContext(), publikasiList);
        adapterEvent = new EventAdapter(getContext(), publikasiList);
        service = ServiceClient.getClient().create(ServicePublikasi.class);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventsplace.setLayoutManager(linearLayoutManager);
        recyclerContent.setLayoutManager(new LinearLayoutManager(getContext()));

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
    }

    private void setAnimation(){
        // snapping the scroll items
        final SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(eventsplace);

        // set a timer for default item
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 1ms = 100ms
//                RecyclerView.ViewHolder viewHolderDefault = eventsplace.
//                        findViewHolderForAdapterPosition(0);
//                LinearLayout eventparentDefault = viewHolderDefault.itemView.
//                            findViewById(R.id.eventparent);
//
//                eventparentDefault.animate().scaleY(1).scaleX(1).setDuration(350).
//                        setInterpolator(new AccelerateInterpolator()).start();
//
//                LinearLayout eventcategoryDefault = viewHolderDefault.itemView.
//                        findViewById(R.id.eventbadge);
//                eventcategoryDefault.animate().alpha(1).setDuration(300).start();
//
//            }
//        }, 100);

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
                    if (response.body().getResult().size() != 0) {
                        publikasiList.addAll(response.body().getResult());
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
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] strings = {"politeknik negeri malang","universitas negeri malang"};
                startActivity(new Intent(getContext(), MapsActivity.class).putExtra("location", strings));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
}
