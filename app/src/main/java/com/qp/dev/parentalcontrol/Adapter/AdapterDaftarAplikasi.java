package com.qp.dev.parentalcontrol.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.qp.dev.parentalcontrol.Data.AppInfo;
import com.qp.dev.parentalcontrol.Preference.SharedPreference;
import com.qp.dev.parentalcontrol.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterDaftarAplikasi extends RecyclerView.Adapter<AdapterDaftarAplikasi.ViewHolder> {
    private ArrayList installedApps = new ArrayList();
    private Context context;
    private SharedPreference sharedPreference;

    // buat ViewHolder untuk data aplikasi terinstall
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView applicationName;
        CardView cardView;
        ImageView icon;
        Switch switchView;

        ViewHolder(View v) {
            super( v );
            applicationName = (TextView) v.findViewById( R.id.applicationName );
            cardView = (CardView) v.findViewById( R.id.card_view );
            icon = (ImageView) v.findViewById( R.id.icon );
            switchView = (Switch) v.findViewById( R.id.switchView );
        }
    }

    public void add(int position, String item) {
    }

    public void remove(AppInfo item) {
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterDaftarAplikasi(List<AppInfo> appInfoList, Context context, String requiredAppsType) {
        installedApps = (ArrayList) appInfoList;
        this.context = context;
        sharedPreference = new SharedPreference();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public AdapterDaftarAplikasi.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // buat view dan parameternya
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.daftar_item, parent, false );
        ViewHolder vh = new ViewHolder( v );
        return vh;
    }

    // tampil nama, posisi dan icon aplikasi
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // buat content aplikasi dari holder view
        final AppInfo appInfo = (AppInfo) installedApps.get( position );
        holder.applicationName.setText( appInfo.getName() );
        holder.icon.setBackgroundDrawable( appInfo.getIcon() );
        holder.switchView.setOnCheckedChangeListener( null );
        holder.cardView.setOnClickListener( null );
        if (checkLockedItem( appInfo.getPackageName() )) {
            holder.switchView.setChecked( true );
        } else {
            holder.switchView.setChecked( false );
        }
        holder.switchView.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreference.addLocked( context, appInfo.getPackageName() );
                } else {
                    sharedPreference.removeLocked( context, appInfo.getPackageName() );
                }
            }
        } );
        holder.cardView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.switchView.performClick();
            }
        } );
    }

    // jumlah aplikasi terinstall
    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    // check aplikasi masuk list locked app
    private boolean checkLockedItem(String checkApp) {
        boolean check = false;
        List<String> locked = sharedPreference.getLocked( context );
        if (locked != null) {
            for (String lock : locked) {
                if (lock.equals( checkApp )) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }
}