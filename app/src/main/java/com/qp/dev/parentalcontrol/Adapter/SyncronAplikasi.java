package com.qp.dev.parentalcontrol.Adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.qp.dev.parentalcontrol.Data.AppInfo;
import com.qp.dev.parentalcontrol.Fragment.FragmentDaftarAplikasi;
import com.qp.dev.parentalcontrol.Preference.SharedPreference;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SyncronAplikasi extends AsyncTask<String , Void, List<AppInfo>> {
    private FragmentDaftarAplikasi container;
    public SyncronAplikasi(FragmentDaftarAplikasi fragment){
        container = fragment;
    }
    @Override
    protected List<AppInfo> doInBackground(String... strings) {
        String requiredAppsType = strings[0];
        List<AppInfo> list = getListOfInstalledApp( Objects.requireNonNull( container.getActivity() ) );
        SharedPreference sharedPreference = new SharedPreference();
        List<AppInfo> lockedFilteredAppList = new ArrayList<AppInfo>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<AppInfo>();
        boolean flag = true;
        if (requiredAppsType.matches( AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
            assert list != null;
            for (int i = 0; i < list.size(); i++) {
                flag = true;
                if (sharedPreference.getLocked(container.getActivity()) != null) {
                    for (int j = 0; j < sharedPreference.getLocked(container.getActivity()).size(); j++) {
                        if (list.get(i).getPackageName().matches(sharedPreference.getLocked(container.getActivity()).get(j))) {
                            lockedFilteredAppList.add(list.get(i));
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    unlockedFilteredAppList.add(list.get(i));
                }
            }
            if (requiredAppsType.matches(AppLockConstants.LOCKED)) {
                list.clear();
                list.addAll(lockedFilteredAppList);
            } else if (requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
                list.clear();
                list.addAll(unlockedFilteredAppList);
            }
        }
        return list;
    }

    // list aplikasi
    private static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList<AppInfo> installedApps = new ArrayList<AppInfo>();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        if (apps != null && !apps.isEmpty()) {
            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                try {
                    if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                        // appInfo = packageManager.getApplicationInfo(p.packageName, 0);
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        container.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<AppInfo> appInfos) {
        super.onPostExecute(appInfos);
        if(container!=null && container.getActivity()!=null) {
            container.hideProgressBar();
            container.updateData(appInfos);
        }

    }
}