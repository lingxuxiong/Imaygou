package com.imaygou.dal;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.imaygou.entities.CategoryEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Load categories from online server, and fall back to locally load if fails.
 */

public class CategoriesLoader extends AsyncTaskLoader<List<CategoryEntity>> {

    private static final String TAG = "CategoriesLoader";

    private List<CategoryEntity> mCachedData;

    public CategoriesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged() || mCachedData == null) {
            forceLoad();
        } else {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mCachedData);
        }
    }
    @Override
    public List<CategoryEntity> loadInBackground() {
        List<CategoryEntity> result = new ArrayList<>();
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://url.path.to.server/?query='categories'");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                int size = jsonArray.length();

                for (int i = 0; i < size; i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    String id = object.getString("id");
                    String name = object.getString("name");
                    result.add(new CategoryEntity(id, name));
                }

                mCachedData = result;

            }
        } catch (Exception e) {
            Log.d(TAG, "Exception caught.", e);
            // fall back to load local data.
            result = loadLocally();
            mCachedData = result;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    private List<CategoryEntity> loadLocally() {
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

            try {
                bos.close();
            } catch (IOException e) {
                Log.d(TAG, "Failed to close stream.");
            }
        }

        List<CategoryEntity> result = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(bos.toString());
            int size = jsonArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                String name = object.getString("name");
                String label = object.getString("label");
                result.add(new CategoryEntity(name, label, null));
            }

        } catch (JSONException e) {
            Log.d(TAG, "exception.", e);
        }

        return result;
    }
}
