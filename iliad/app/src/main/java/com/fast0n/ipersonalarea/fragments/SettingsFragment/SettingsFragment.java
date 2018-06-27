package com.fast0n.ipersonalarea.fragments.SettingsFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fast0n.ipersonalarea.R;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final ProgressBar loading;
        final Context context;
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        final RecyclerView recyclerView;
        final List<DataSettingsFragments> infoList = new ArrayList<>();


        // java adresses
        loading = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recycler_view);
        CubeGrid cubeGrid = new CubeGrid();
        loading.setIndeterminateDrawable(cubeGrid);
        cubeGrid.setColor(getResources().getColor(R.color.colorPrimary));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        String list[] = {"Recupero elenco contatti", "Salvare i file sul dispositivo", "Fotocamera"};


        for (String a : list) {

            infoList.add(new DataSettingsFragments(a));
            CustomAdapterSettings ca = new CustomAdapterSettings(context, infoList);
            recyclerView.setAdapter(ca);
        }


        return view;
    }

}
