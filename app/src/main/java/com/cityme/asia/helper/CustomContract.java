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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class CustomContract {
    public static final String CONTENT_AUTHORITY = "com.cityme.asia";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SUGGESTION = "suggestion";

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class SuggestionEntry implements BaseColumns {
        public static final int COL_NAME = 1;
        public static final int COL_ADDRESS = 2;
        public static final int COL_SLUG = 3;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUGGESTION).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUGGESTION;
        public static final String TABLE_NAME = "suggestion";
        public static final String KEY_UNIQUE_ID = "unique_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_FULL_ADDRESS = "address";
        public static final String KEY_SLUG = "slug";
        public static final String KEY_IMAGE_URL = "image_url";

        public static Uri buildOrders() {
            return CONTENT_URI;
        }

        public static Uri buildOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
