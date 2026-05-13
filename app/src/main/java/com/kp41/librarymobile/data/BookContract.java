package com.kp41.librarymobile.data;

import android.provider.BaseColumns;

public final class BookContract {
    private BookContract() {
    }

    public static class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_PAGES = "pages";
        public static final String COLUMN_AVAILABLE = "available";
    }
}
