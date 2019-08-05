package com.dev.thinkgather.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.dev.thinkgather.Adapter.BukuAdapter;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookFragment extends Fragment {

    @BindView(R.id.recycler_content) RecyclerView recyclerContent;
    private List<Publikasi> publikasiList;
    private BukuAdapter bukuAdapter;
    private ServicePublikasi servicePublikasi;

    public BookFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        ButterKnife.bind(this, view);
        initComponents();
        loadData();
        return view;
    }

    private void loadData() {
        servicePublikasi.getAllPublikasi().enqueue(new Callback<GetPublikasi>() {
            @Override
            public void onResponse(Call<GetPublikasi> call, Response<GetPublikasi> response) {
                if(response.code() == 200){
                    publikasiList.clear();
                    if(response.body().getResult().size()!=0){
                        for(int i = 0; i < response.body().getResult().size(); i++){
                            if(!response.body().getResult().get(i).getBuku().equals("")){
                                publikasiList.add(response.body().getResult().get(i));
                            }
                        }
                    }
                    bukuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetPublikasi> call, Throwable t) {

            }
        });
    }

    private void initComponents() {
        setHasOptionsMenu(true);
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        publikasiList = new ArrayList<>();
        bukuAdapter = new BukuAdapter(getContext(), publikasiList);
        recyclerContent.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerContent.setAdapter(bukuAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_buku, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.buku_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bukuAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bukuAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
