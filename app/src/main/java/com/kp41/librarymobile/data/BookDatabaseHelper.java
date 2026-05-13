package com.kp41.librarymobile.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kp41.librarymobile.data.BookContract.BookEntry;

public class BookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "library_mobile.db";
    private static final int DATABASE_VERSION = 1;

    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + BookEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + BookEntry.COLUMN_YEAR + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_GENRE + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PAGES + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_AVAILABLE + " INTEGER NOT NULL DEFAULT 1"
                + ")";
        db.execSQL(sql);

        db.execSQL("INSERT INTO books(title, author, year, genre, pages, available) VALUES" +
                "('Кайдашева сім’я', 'Іван Нечуй-Левицький', 1879, 'Повість', 192, 1)," +
                "('Тіні забутих предків', 'Михайло Коцюбинський', 1911, 'Повість', 160, 1)," +
                "('Місто', 'Валер’ян Підмогильний', 1928, 'Роман', 320, 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME);
        onCreate(db);
    }
}
