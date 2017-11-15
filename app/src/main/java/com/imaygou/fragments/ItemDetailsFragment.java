package com.imaygou.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * represent details of a specific sub-category when it was clicked.
 */

public class ItemDetailsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new TextView(container.getContext());
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((TextView)view).setText(getArguments().getString("name"));
    }
}
