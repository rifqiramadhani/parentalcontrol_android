package com.qp.dev.parentalcontrol.Fragment;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.qp.dev.parentalcontrol.R;

import java.util.Objects;

public class FragmentSetting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FragmentSetting() {
    }

    public static FragmentSetting newInstance() {
        return new FragmentSetting();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString( ARG_PARAM1 );
            String mParam2 = getArguments().getString( ARG_PARAM2 );
        }
    }

    Switch overlay_permission, usage_access;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.fragment_setting, container, false);
        overlay_permission = (Switch) v.findViewById(R.id.overlay_permission_switch);
        usage_access = (Switch) v.findViewById(R.id.usage_access_permission);
        if(Settings.canDrawOverlays(getActivity())){
            overlay_permission.setChecked(true);
        }
        if(hasUsageStatsPermission()){
            usage_access.setChecked(true);
        }
        return v;
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) Objects.requireNonNull( getActivity() ).getSystemService( Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            assert appOps != null;
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getActivity().getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}