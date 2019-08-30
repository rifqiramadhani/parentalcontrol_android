package com.qp.dev.parentalcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.qp.dev.parentalcontrol.Data.AppInfo;
import com.qp.dev.parentalcontrol.Fragment.FragmentDaftarAplikasi;
import com.qp.dev.parentalcontrol.Fragment.FragmentPola;
import com.qp.dev.parentalcontrol.Fragment.FragmentSetting;
import com.qp.dev.parentalcontrol.Fragment.FragmentTentang;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = getSharedPreferences( AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fragmentManager = getSupportFragmentManager();
        Objects.requireNonNull( getSupportActionBar() ).setTitle("Daftar Aplikasi");
        Fragment f = FragmentDaftarAplikasi.newInstance(AppLockConstants.ALL_APPS);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen( GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getCurrentFragment() instanceof FragmentDaftarAplikasi) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
                Objects.requireNonNull( getSupportActionBar() ).setTitle("Daftar Aplikasi");
                Fragment f = FragmentDaftarAplikasi.newInstance(AppLockConstants.ALL_APPS);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
            }
        }
    }

    // tampil transisi fragment
    public Fragment getCurrentFragment() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // nama navigasi ActionBar tiap layar
        int id = item.getItemId();

        if (id == R.id.nav_all_apps) {
            Objects.requireNonNull( getSupportActionBar() ).setTitle("Daftar Aplikasi");
            Fragment f = FragmentDaftarAplikasi.newInstance(AppLockConstants.ALL_APPS);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if (id == R.id.nav_locked_apps) {
            Objects.requireNonNull( getSupportActionBar() ).setTitle("Aplikasi Dikunci");
            Fragment f = FragmentDaftarAplikasi.newInstance(AppLockConstants.LOCKED);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if (id == R.id.nav_unlocked_apps) {
            Objects.requireNonNull( getSupportActionBar() ).setTitle("Aplikasi Tidak Dikunci");
            Fragment f = FragmentDaftarAplikasi.newInstance(AppLockConstants.UNLOCKED);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if (id == R.id.nav_change_pattern) {
            Objects.requireNonNull( getSupportActionBar() ).setTitle("Ubah Password");
            Fragment f = FragmentPola.newInstance();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if(id == R.id.nav_allow_access){
            Objects.requireNonNull( getSupportActionBar() ).setTitle("Izinkan Akses");
            Fragment f = FragmentSetting.newInstance();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if(id == R.id.nav_about){
            Objects.requireNonNull( getSupportActionBar() ).setTitle("Tentang");
            Fragment f = FragmentTentang.newInstance();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // tampil data list aplikasi terinstall
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList<AppInfo> installedApps = new ArrayList<AppInfo>();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        if (apps != null && !apps.isEmpty()) {
            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                // tampil di home awal
                try {
                    if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                        AppInfo app = new AppInfo();
                        app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                        app.setPackageName(p.packageName);
                        app.setVersionName(p.versionName);
                        app.setVersionCode(p.versionCode);
                        app.setIcon(p.applicationInfo.loadIcon(packageManager));
                        installedApps.add(app);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return installedApps;
        }
        return null;
    }
}