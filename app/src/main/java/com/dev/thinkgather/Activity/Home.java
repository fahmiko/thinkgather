package com.dev.thinkgather.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Fragment.HomeFragment;
import com.dev.thinkgather.Fragment.ProfileFragment;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Session session = Application.getSession();
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) FrameLayout container;
    @BindView(R.id.nav_view) NavigationView navView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ButterKnife.bind(this);
        if (session.checkLogin()) {
            setSupportActionBar(toolbar);
            initDrawer();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    private void updateNavHeader() {
        View headerView = navView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);
        navUsername.setText(session.getStringLogin("nama"));
        try {
            Glide.with(getApplicationContext())
                    .load(ServiceClient.BASE_URL + "uploads/members/" + session.getStringLogin("foto"))
                    .into(navUserPhot);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_signout:
                logout();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                fab.hide();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        session.sessionDestroy();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    @OnClick(R.id.fab)
    public void onClick() {
        startActivity(new Intent(getApplicationContext(), TambahPublikasi.class));
    }
}
