package com.imaygou.dal;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.imaygou.entities.CategoryEntity;
import com.imaygou.entities.SubcategoryEntity;
import com.imaygou.fragments.DetailsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Load category items from device.
 */
public class SubcategoriesLoader extends AsyncTaskLoader<List<SubcategoryEntity>> {

    private static final String TAG = "CategoriesLocalLoader";

    private Bundle mArgs;
    private String mCategoryName;
    private List<SubcategoryEntity> mCachedData;

    public SubcategoriesLoader(Context context, @Nullable Bundle args) {
        super(context);
        mArgs = args;
        mCategoryName = mArgs.getString(DetailsFragment.ARG_CATEGORY_NAME);
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading, loader[" + Integer.toHexString(hashCode()) + "]");
        if (takeContentChanged() || mCachedData == null) {
            forceLoad();
        } else {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mCachedData);
        }
    }

    @Override
    public List<SubcategoryEntity> loadInBackground() {
        if (mArgs == null) {
            return Collections.EMPTY_LIST;
        }

        String categoryName = mArgs.getString(DetailsFragment.ARG_CATEGORY_NAME);
        String assetFileName = categoryName + ".json";

        AssetManager am = getContext().getAssets();
        InputStream is = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            is = am.open(assetFileName, AssetManager.ACCESS_STREAMING);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            is.close();
            bos.close();
        } catch (IOException e) {
            Log.d(TAG, "Failed to load data.", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG, "Failed to close input stream.");
                }
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Log.d(TAG, "Failed to close stream.");
                }
            }
        }

        List<SubcategoryEntity> result = new ArrayList<>();

        try {
            JSONArray subCategoriesArray = new JSONArray(bos.toString());
            int size = subCategoriesArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject subCategoryObject = (JSONObject) subCategoriesArray.get(i);
                String title = subCategoryObject.getString("title");
                String format = subCategoryObject.getString("display");
                JSONArray itemsArray = subCategoryObject.getJSONArray("items");
                int itemsSize = itemsArray.length();

                List<CategoryEntity> items = new ArrayList<>();
                for (int j = 0; j < itemsSize; j++) {
                    JSONObject item = (JSONObject) itemsArray.get(j);
                    CategoryEntity entity = new CategoryEntity(
                            item.getString("name"),
                            item.getString("label"),
                            item.getString("icon")
                    );
                    items.add(entity);
                }
                result.add(new SubcategoryEntity(title, items, format));
            }

            mCachedData = result;
        } catch (JSONException e) {
            Log.d(TAG, "exception.", e);
        }

        Log.d(TAG, "got " + result.size() + " sub category under " + mCategoryName);

        return result;
    }
}
