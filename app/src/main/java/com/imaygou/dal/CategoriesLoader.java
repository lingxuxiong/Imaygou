package com.imaygou.dal;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.imaygou.data.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Load category items.
 **/
public class CategoriesLoader extends AsyncTaskLoader<List<Category>> {

    private static final String TAG = "CategoriesLoader";

    private List<Category> mCachedData;

    public CategoriesLoader(Context context) {
        super(context);
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
    public List<Category> loadInBackground() {
        AssetManager am = getContext().getAssets();
        InputStream is = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            is = am.open("categories.json", AssetManager.ACCESS_STREAMING);
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

        List<Category> result = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(bos.toString());
            int size = jsonArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                String id = object.getString("id");
                String name = object.getString("name");
                result.add(new Category(id, name));
            }

            mCachedData = result;
        } catch (JSONException e) {
            Log.d(TAG, "exception.", e);
        }
        return result;
    }
}
