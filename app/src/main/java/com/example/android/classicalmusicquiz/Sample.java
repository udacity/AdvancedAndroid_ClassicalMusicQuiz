package com.example.android.classicalmusicquiz;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.JsonReader;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Java Object representing a single sample. Also includes utility methods for obtaining samples
 * from assets.
 */
class Sample {

    private int mSampleID;
    private String mComposer;
    private String mTitle;
    private String mUri;
    private String mAlbumArtID;


    private Sample(int sampleID, String composer, String title, String uri, String albumArtID) {
        mSampleID = sampleID;
        mComposer = composer;
        mTitle = title;
        mUri = uri;
        mAlbumArtID = albumArtID;
    }

    /**
     * Gets portrait of the composer for a sample by the sample ID.
     * @param context The application context.
     * @param sampleID The sample ID.
     * @return The portrait Bitmap.
     */
    static Bitmap getComposerArtBySampleID(Context context, int sampleID){
        Sample sample = Sample.getSampleByID(context, sampleID);
        int albumArtID = context.getResources().getIdentifier(
                sample != null ? sample.getAlbumArtID() : null, "drawable",
                context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), albumArtID);
    }

    /**
     * Gets a single sample by its ID.
     * @param context The application context.
     * @param sampleID The sample ID.
     * @return The sample object.
     */
    static Sample getSampleByID(Context context, int sampleID) {
        JsonReader reader;
        try {
            reader = readJSONFile(context);
            reader.beginArray();
            while (reader.hasNext()) {
                Sample currentSample = readEntry(reader);
                if (currentSample.getSampleID() == sampleID) {
                    reader.close();
                    return currentSample;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets and ArrayList of the IDs for all of the Samples from the JSON file.
     * @param context The application context.
     * @return The ArrayList of all sample IDs.
     */
    static ArrayList<Integer> getAllSampleIDs(Context context){
        JsonReader reader;
        ArrayList<Integer> sampleIDs = new ArrayList<>();
        try {
            reader = readJSONFile(context);
            reader.beginArray();
            while (reader.hasNext()) {
                sampleIDs.add(readEntry(reader).getSampleID());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sampleIDs;
    }

    /**
     * Method used for obtaining a single sample from the JSON file.
     * @param reader The JSON reader object pointing a single sample JSON object.
     * @return The Sample the JsonReader is pointing to.
     */
    private static Sample readEntry(JsonReader reader) {
        Integer id = -1;
        String composer = null;
        String title = null;
        String uri = null;
        String albumArtID = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "name":
                        title = reader.nextString();
                        break;
                    case "id":
                        id = reader.nextInt();
                        break;
                    case "composer":
                        composer = reader.nextString();
                        break;
                    case "uri":
                        uri = reader.nextString();
                        break;
                    case "albumArtID":
                        albumArtID = reader.nextString();
                        break;
                    default:
                        break;
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Sample(id, composer, title, uri, albumArtID);
    }

    /**
     * Method for creating a JsonReader object that points to the JSON array of samples.
     * @param context The application context.
     * @return The JsonReader object pointing to the JSON array of samples.
     * @throws IOException Exception thrown if the sample file can't be found.
     */
    private static JsonReader readJSONFile(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        String uri = null;

        try {
            for (String asset : assetManager.list("")) {
                if (asset.endsWith(".exolist.json")) {
                    uri = "asset:///" + asset;
                }
            }
        } catch (IOException e) {
            Toast.makeText(context, R.string.sample_list_load_error, Toast.LENGTH_LONG)
                    .show();
        }

        String userAgent = Util.getUserAgent(context, "ClassicalMusicQuiz");
        DataSource dataSource = new DefaultDataSource(context, null, userAgent, false);
        DataSpec dataSpec = new DataSpec(Uri.parse(uri));
        InputStream inputStream = new DataSourceInputStream(dataSource, dataSpec);

        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        } finally {
            Util.closeQuietly(dataSource);
        }

        return reader;
    }

    // Getters and Setters

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        mTitle = title;
    }

    String getUri() {
        return mUri;
    }

    void setUri(String uri) {
        mUri = uri;
    }

    int getSampleID() {
        return mSampleID;
    }

    void setSampleID(int sampleID) {
        mSampleID = sampleID;
    }

    String getComposer() {
        return mComposer;
    }

    void setComposer(String composer) {
        mComposer = composer;
    }

    String getAlbumArtID() {
        return mAlbumArtID;
    }

    void setAlbumArtID(String albumArtID) {
        mAlbumArtID = albumArtID;
    }
}
