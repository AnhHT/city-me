/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cityme.asia.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cityme.asia.helper.CustomContract.SuggestionEntry;

/**
 * Manages a local database for weather data.
 */
public class CustomDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "cityme.db";
    private static final int DATABASE_VERSION = 6;

    public CustomDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SUGGESTION = "CREATE TABLE " + SuggestionEntry.TABLE_NAME + " (" +
                SuggestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SuggestionEntry.KEY_UNIQUE_ID + " TEXT NOT NULL," +
                SuggestionEntry.KEY_NAME + " TEXT," +
                SuggestionEntry.KEY_FULL_ADDRESS + " TEXT," +
                SuggestionEntry.KEY_SLUG + " TEXT," +
                SuggestionEntry.KEY_IMAGE_URL + " TEXT );";

        sqLiteDatabase.execSQL(SQL_CREATE_SUGGESTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SuggestionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
