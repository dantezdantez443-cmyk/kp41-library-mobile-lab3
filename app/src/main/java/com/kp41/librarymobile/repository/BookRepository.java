package com.kp41.librarymobile.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kp41.librarymobile.data.BookContract.BookEntry;
import com.kp41.librarymobile.data.BookDatabaseHelper;
import com.kp41.librarymobile.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private final BookDatabaseHelper databaseHelper;

    public BookRepository(Context context) {
        this.databaseHelper = new BookDatabaseHelper(context);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(BookEntry.TABLE_NAME, null, null, null, null, null,
                BookEntry.COLUMN_TITLE + " ASC");
        try {
            while (cursor.moveToNext()) {
                books.add(readBook(cursor));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return books;
    }

    public Book getBookById(long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(BookEntry.TABLE_NAME, null, BookEntry._ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return readBook(cursor);
            }
            return null;
        } finally {
            cursor.close();
            db.close();
        }
    }

    public long createBook(Book book) {
        validateBook(book);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            return db.insertOrThrow(BookEntry.TABLE_NAME, null, toValues(book));
        } finally {
            db.close();
        }
    }

    public boolean updateBook(Book book) {
        validateBook(book);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            int rows = db.update(BookEntry.TABLE_NAME, toValues(book), BookEntry._ID + "=?",
                    new String[]{String.valueOf(book.getId())});
            return rows > 0;
        } finally {
            db.close();
        }
    }

    public boolean deleteBook(long id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            int rows = db.delete(BookEntry.TABLE_NAME, BookEntry._ID + "=?",
                    new String[]{String.valueOf(id)});
            return rows > 0;
        } finally {
            db.close();
        }
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Дані книги не можуть бути порожніми");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва книги є обов’язковою");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Автор книги є обов’язковим");
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            throw new IllegalArgumentException("Жанр книги є обов’язковим");
        }
        if (book.getYear() < 1000 || book.getYear() > 2100) {
            throw new IllegalArgumentException("Рік видання введено некоректно");
        }
        if (book.getPages() <= 0) {
            throw new IllegalArgumentException("Кількість сторінок має бути більшою за нуль");
        }
    }

    private ContentValues toValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_TITLE, book.getTitle().trim());
        values.put(BookEntry.COLUMN_AUTHOR, book.getAuthor().trim());
        values.put(BookEntry.COLUMN_YEAR, book.getYear());
        values.put(BookEntry.COLUMN_GENRE, book.getGenre().trim());
        values.put(BookEntry.COLUMN_PAGES, book.getPages());
        values.put(BookEntry.COLUMN_AVAILABLE, book.isAvailable() ? 1 : 0);
        return values;
    }

    private Book readBook(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(BookEntry._ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_TITLE));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_AUTHOR));
        int year = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_YEAR));
        String genre = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_GENRE));
        int pages = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PAGES));
        boolean available = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_AVAILABLE)) == 1;
        return new Book(id, title, author, year, genre, pages, available);
    }
}
