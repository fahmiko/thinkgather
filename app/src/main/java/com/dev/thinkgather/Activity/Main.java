package com.dev.thinkgather.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Fragment.BookFragment;
import com.dev.thinkgather.MapsActivity;
import com.dev.thinkgather.Fragment.HomeFragment;
import com.dev.thinkgather.Fragment.ProfileFragment;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Session session = Application.getSession();
    @BindView(R.id.navbar) NavigationView navbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public static Main main;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);
        ButterKnife.bind(this);
        main = this;

        if (session.checkLogin()) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            updateDrawer();
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    public void updateDrawer() {
        navbar.setNavigationItemSelectedListener(this);
        View headerView = navbar.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.nama_pengguna);
        TextView email = headerView.findViewById(R.id.email_pengguna);
        CircleImageView circleImageView = headerView.findViewById(R.id.profile_image);
        name.setText(session.getStringLogin("nama"));
        email.setText(session.getStringLogin("email"));
        Glide.with(getApplicationContext())
                .load(ServiceClient.BASE_URL+"uploads/members/"+session.getStringLogin("foto"))
                .into(circleImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.db:
                mDrawerLayout.closeDrawers();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                getSupportActionBar().setTitle("Dashboard");
                break;
            case R.id.buku:
                mDrawerLayout.closeDrawers();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new BookFragment()).commit();
                getSupportActionBar().setTitle("Daftar Buku");
                break;
            case R.id.akun:
                mDrawerLayout.closeDrawers();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                getSupportActionBar().setTitle("Profil");
                break;
            case R.id.logout:
                session.sessionDestroy();
                startActivity(new Intent(getApplicationContext(), Login.class));
                break;
        }
        return true;
    }
}
