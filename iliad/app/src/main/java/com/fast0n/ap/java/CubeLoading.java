package com.fast0n.ap.java;

import android.content.Context;
import android.graphics.Color;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.CubeGrid;


public class CubeLoading {
    Context context;
    private ProgressBar loadingError;
    private String themeError;

    public CubeLoading(Context con, ProgressBar loading, String theme) {
        this.context = con;
        this.loadingError = loading;
        this.themeError = theme;
    }

    public void showLoading() {

        CubeGrid cubeGrid = new CubeGrid();
        loadingError.setIndeterminateDrawable(cubeGrid);
        if (themeError.equals("0"))
            cubeGrid.setColor(Color.parseColor("#c00000"));
        else
            cubeGrid.setColor(Color.parseColor("#ffffff"));

    }
}