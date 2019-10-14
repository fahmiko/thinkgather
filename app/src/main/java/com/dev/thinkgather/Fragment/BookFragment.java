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
import com.dev.thinkgather.Adapter.PublikasiAdapter;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.Favorite;
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

    @BindView(R.id.recycler_content) RecyclerView favPost;
    @BindView(R.id.recycler_mypost) RecyclerView myPost;
    private List<Publikasi> favPublikasi = new ArrayList<>();
    private List<Publikasi> myPublikasi = new ArrayList<>();
    private PublikasiAdapter favPublikasiAdapter;
    private PublikasiAdapter myPublikasiAdapter;
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
                    retrivePublikasi(response);
                }
            }

            @Override
            public void onFailure(Call<GetPublikasi> call, Throwable t) {

            }
        });
    }

    private void retrivePublikasi(Response<GetPublikasi> response) {
        Session session = Application.getSession();
        myPublikasi.clear();
        favPublikasi.clear();
        List<Favorite> favoriteList = favPublikasiAdapter.getFavoriteList();

        for(int i = 0; i < response.body().getResult().size(); i++){
            for (int j = 0; j < favoriteList.size(); j++){
                if(favoriteList.get(j).getIdPublikasi().equals(response.body().getResult().get(i).getIdPublikasi())){
                    favPublikasi.add(response.body().getResult().get(i));
                }
            }
        }

        for(int i = 0; i < response.body().getResult().size(); i++){
            if(response.body().getResult().get(i).getIdMember().equals(session.getStringLogin("id_member"))){
                myPublikasi.add(response.body().getResult().get(i));
            }
        }
        favPublikasiAdapter.notifyDataSetChanged();
        myPublikasiAdapter.notifyDataSetChanged();
    }

    private void initComponents() {
        setHasOptionsMenu(true);
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        favPost.setLayoutManager(new LinearLayoutManager(getContext()));
        myPost.setLayoutManager(new LinearLayoutManager(getContext()));
        favPublikasiAdapter = new PublikasiAdapter(getContext(), favPublikasi, "favorite", "book");
        myPublikasiAdapter  = new PublikasiAdapter(getContext(), myPublikasi, "my_collection", "profile");
        favPost.setAdapter(favPublikasiAdapter);
        myPost.setAdapter(myPublikasiAdapter);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_buku, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.buku_search)
//                .getActionView();
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getActivity().getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                bukuAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                bukuAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//    }
}
