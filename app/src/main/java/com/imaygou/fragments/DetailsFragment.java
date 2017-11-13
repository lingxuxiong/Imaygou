package com.imaygou.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaygou.R;
import com.imaygou.data.Category;

/**
 *  Represent details of a specific category.
 */

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    public static final String ARG_CATEGORY_ID = "id";
    public static final String ARG_CATEGORY_NAME = "name";

    private Category mCategory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String id = getArguments().getString(ARG_CATEGORY_ID);
            String name = getArguments().getString(ARG_CATEGORY_NAME);
            mCategory = new Category(id, name);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_detail, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView name = (TextView) view.findViewById(R.id.item_detail);
        name.setText(mCategory.getName());
    }
}
