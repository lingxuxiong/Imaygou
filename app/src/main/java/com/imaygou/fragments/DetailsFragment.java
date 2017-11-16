package com.imaygou.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imaygou.R;
import com.imaygou.dal.Loaders;
import com.imaygou.dal.SubcategoriesLoader;
import com.imaygou.entities.CategoryEntity;
import com.imaygou.entities.SubcategoryEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *  Represent details of a specific category.
 */

public class DetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<SubcategoryEntity>> {

    private static final String TAG = "DetailsFragment";

    public static final String ARG_CATEGORY_NAME = "name";
    public static final String ARG_CATEGORY_LABEL = "label";

    @BindView(R.id.sub_category_details)
    RecyclerView mDetailsRecyclerView;

    private SubCategoryAdapter mSubCategoryAdapter;

    private Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubCategoryAdapter = new SubCategoryAdapter();
        mSubCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos, CategoryEntity item) {
                Log.d(TAG, "Item " + pos + " was clicked.");
                Fragment fragment = new ItemDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", item.getLabel());
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.details_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (getArguments() != null) {
            String name = getArguments().getString(ARG_CATEGORY_NAME);
            Bundle args = new Bundle();
            args.putString(ARG_CATEGORY_NAME, name);
            getLoaderManager().initLoader(Loaders.LOADER_ID_LOAD_SUBCATEGORY, args, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subcategory, null);
        mUnbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mDetailsRecyclerView.setAdapter(mSubCategoryAdapter);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new SubcategoriesLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<SubcategoryEntity>> loader, List<SubcategoryEntity> data) {
        Log.d(TAG, "onLoadFinished, " + " got " + data.size() + " items.");
        mSubCategoryAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.header) TextView mTxvHeader;

        private HeaderViewHolder(View headerView) {
            super(headerView);
            ButterKnife.bind(this, headerView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo) ImageView mLogo;
        @BindView(R.id.label) TextView mLabel;

        private ItemViewHolder(View subCategoryView) {
            super(subCategoryView);
            ButterKnife.bind(this, subCategoryView);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int pos, CategoryEntity item);
    }

    private static class SubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM_TYPE_HEADER = 1;
        private static final int ITEM_TYPE_GRID = 2;
        private static final int ITEM_TYPE_LIST = 3;

        private List<SubcategoryEntity> mData;

        private OnItemClickListener mOnItemClickListener;

        SubCategoryAdapter() {
            mData = new ArrayList<>();
        }

        SubCategoryAdapter(List<SubcategoryEntity> data) {
            mData = data;
        }

        public void setData(List<SubcategoryEntity> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }


        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof  GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (isHeaderPosition(position)) {
                            return gridLayoutManager.getSpanCount();
                        }

                        int idx = getHeaderIndexFromPosition(position);
                        boolean listFormat = SubcategoryEntity.LIST_FORMAT.equals(
                                mData.get(idx).getItemsDisplayFormat());
                        return listFormat ? gridLayoutManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER ) {
                View headerView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.subcategory_item_header, null, false);
                return new HeaderViewHolder(headerView);
            } else if (viewType == ITEM_TYPE_LIST) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.subcategory_item_list, null, false);
                return new ItemViewHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.subcategory_item_grid, null, false);
                return new ItemViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (isHeaderPosition(position)) {
                int idx = getHeaderIndexFromPosition(position);
                String header = mData.get(idx).getTitle();
                ((HeaderViewHolder)holder).mTxvHeader.setText(header);
            } else {
                ItemViewHolder vh = ((ItemViewHolder)holder);
                int idx[] = getItemIndexFromPosition(position);
                final CategoryEntity entity = mData.get(idx[0]).getItems().get(idx[1]);
                vh.mLabel.setText(entity.getLabel());

                ImageView logoView = vh.mLogo;
                String logo = entity.getIcon();
                boolean hasExtension = logo.indexOf(".") > 0;
                String logoWithoutExt = hasExtension ? logo.substring(0, logo.indexOf(".")) : logo;
                Context context = logoView.getContext();
                int id = context.getResources().getIdentifier(logoWithoutExt, "drawable", context.getPackageName());
                Glide.with(context).load(id).into(logoView);

                if (mOnItemClickListener != null) {
                    vh.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.onItemClick(holder.getAdapterPosition(), entity);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            int size = mData.size();
            for (SubcategoryEntity entity : mData) {
                size += entity.getItems().size();
            }
            return size;
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeaderPosition(position)) {
                return ITEM_TYPE_HEADER;
            }

            int idx = getHeaderIndexFromPosition(position);
            boolean listFormat = SubcategoryEntity.LIST_FORMAT.equals(
                    mData.get(idx).getItemsDisplayFormat());
            return listFormat ? ITEM_TYPE_LIST : ITEM_TYPE_GRID;
        }

        private boolean isHeaderPosition(int position) {
            int size = mData.size();
            Set<Integer> headers = new HashSet<>(size);

            int pos = 0;
            for (int i = 0; i < size; i++) {
                headers.add(pos);
                pos += mData.get(i).getItems().size() + 1;
            }
            return headers.contains(position);
        }

        private int getHeaderIndexFromPosition(int position) {
            int idx = 0, pos = 0, size = mData.size();
            do {
                pos += mData.get(idx).getItems().size() + 1;
                if (position < pos) {
                    return idx;
                } else if (position == pos) {
                    return idx + 1;
                }
            } while (idx++ < size);

            return 0;
        }

        private int[] getItemIndexFromPosition(int position) {
            int idx = 0, pos = 0, size = mData.size();
            do {
                int sz = mData.get(idx).getItems().size() + 1;
                if (position < (pos + sz)) {
                    return new int[]{idx, position - pos - 1};
                } else {
                    pos += sz;
                }
            } while (idx++ < size);

            return new int[] {0, 0};
        }

    }
}
