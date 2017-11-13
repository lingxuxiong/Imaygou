package com.imaygou.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaygou.R;
import com.imaygou.dal.CategoriesLoader;
import com.imaygou.data.Category;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Manage categories list.
 */
public class MasterFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Category>> {

    private static final String TAG = "MasterFragment";

    private static final int LOADER_ID_LOAD_CATEGORIES = 1;

    private RecyclerView mCategoriesView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_LOAD_CATEGORIES, new Bundle(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.categories, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategoriesView = (RecyclerView) view.findViewById(R.id.categories);
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
        switch (id)  {
            case LOADER_ID_LOAD_CATEGORIES:
                return new CategoriesLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
        Log.d(TAG, "onLoadFinished");
        mCategoriesView.setAdapter(new CategoriesAdapter(getActivity(), data));
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    private static class CategoryItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txvName;

        CategoryItemViewHolder(View view) {
            super(view);
            txvName = (TextView) view.findViewById(R.id.category_name);
        }

    }

    private static class CategoriesAdapter extends RecyclerView.Adapter<CategoryItemViewHolder> {

        private final WeakReference<Activity> mContext;
        private final List<Category> mCategoriesList;

        CategoriesAdapter(Activity context, List<Category> categories) {
            mContext = new WeakReference<>(context);
            mCategoriesList = categories == null ? Collections.<Category>emptyList() : categories;
        }

        @Override
        public CategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext.get()).inflate(R.layout.category_item, null);
            return new CategoryItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CategoryItemViewHolder holder, final int position) {
            holder.txvName.setText(mCategoriesList.get(position).getName());
            holder.txvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putString(DetailsFragment.ARG_CATEGORY_ID,
                            mCategoriesList.get(position).getId());
                    arguments.putString(DetailsFragment.ARG_CATEGORY_NAME,
                            mCategoriesList.get(position).getName());
                    DetailsFragment fragment = new DetailsFragment();
                    fragment.setArguments(arguments);
                    mContext.get().getFragmentManager().beginTransaction()
                            .replace(R.id.details_fragment_container, fragment)
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategoriesList.size();
        }
    }
}
