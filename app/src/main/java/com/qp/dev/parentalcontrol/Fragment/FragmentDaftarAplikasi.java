package com.qp.dev.parentalcontrol.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qp.dev.parentalcontrol.Adapter.AdapterDaftarAplikasi;
import com.qp.dev.parentalcontrol.Adapter.SyncronAplikasi;
import com.qp.dev.parentalcontrol.Data.AppInfo;
import com.qp.dev.parentalcontrol.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentDaftarAplikasi extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    static String requiredAppsType;

    public static FragmentDaftarAplikasi newInstance(String requiredApps) {
        requiredAppsType = requiredApps;
        FragmentDaftarAplikasi f = new FragmentDaftarAplikasi();
        return (f);
    }

    public FragmentDaftarAplikasi() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ProgressDialog progressDialog;
    List<AppInfo> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daftar_app, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Apps ...");

        mRecyclerView = (RecyclerView) v.findViewById( R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getActivity() );
        mRecyclerView.setLayoutManager( mLayoutManager );
        mAdapter = new AdapterDaftarAplikasi(list , getActivity(), requiredAppsType);
        mRecyclerView.setAdapter(mAdapter);

        SyncronAplikasi task = new SyncronAplikasi(this);
        task.execute(requiredAppsType);

        return v;
    }

    public void showProgressBar() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressDialog.show();
    }

    public void hideProgressBar(){
        progressDialog.dismiss();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(List<AppInfo> list){
        this.list.clear();
        this.list.addAll(list);
        mAdapter.notifyDataSetChanged();
    }
}