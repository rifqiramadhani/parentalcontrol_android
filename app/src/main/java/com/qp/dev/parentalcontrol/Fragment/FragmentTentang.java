package com.qp.dev.parentalcontrol.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qp.dev.parentalcontrol.R;

public class FragmentTentang extends Fragment {
    View view;

    public static FragmentTentang newInstance() {
        FragmentTentang f = new FragmentTentang();
        return (f);
    }

    public FragmentTentang() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.fragment_tentang, container, false );
        return view;
    }
}