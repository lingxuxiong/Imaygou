package com.imaygou.fragments;

import android.os.Bundle;
import android.os.Handler;
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
import com.imaygou.dal.Loaders;
import com.imaygou.entities.CategoryEntity;

import java.util.Collections;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Manage categories list.
 */
public class MasterFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<CategoryEntity>> {

    private static final String TAG = "MasterFragment";

    @BindView(R.id.categories)
    RecyclerView mCategoriesView;

    private Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(Loaders.LOADER_ID_LOAD_CATEGORY, new Bundle(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.categories, null);
        mUnbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public Loader<List<CategoryEntity>> onCreateLoader(int id, Bundle args) {
        return new CategoriesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<CategoryEntity>> loader, final List<CategoryEntity> data) {
        mCategoriesView.setAdapter(new CategoriesAdapter(data));

        // Default to show details of the first category.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                CategoryEntity entity = data.get(0);
                showDetail(entity.getName(), entity.getLabel());
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<List<CategoryEntity>> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    static class CategoryItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.category_name)
        TextView txvName;

        CategoryItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    private class CategoriesAdapter extends RecyclerView.Adapter<CategoryItemViewHolder> {

        private final List<CategoryEntity> mCategoriesList;

        CategoriesAdapter(List<CategoryEntity> categories) {
            mCategoriesList = categories == null ? Collections.<CategoryEntity>emptyList() : categories;
        }

        @Override
        public CategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, null);
            return new CategoryItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoryItemViewHolder holder, int position) {
            holder.txvName.setText(mCategoriesList.get(position).getLabel());
            holder.txvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    String name = mCategoriesList.get(pos).getName();
                    String label = mCategoriesList.get(pos).getLabel();
                    showDetail(name, label);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategoriesList.size();
        }
    }

    void showDetail(String categoryName, String categoryLabel) {
        Bundle arguments = new Bundle();
        arguments.putString(DetailsFragment.ARG_CATEGORY_NAME, categoryName);
        arguments.putString(DetailsFragment.ARG_CATEGORY_LABEL,categoryLabel);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(arguments);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_fragment_container, fragment)
                .commit();

    }
}
