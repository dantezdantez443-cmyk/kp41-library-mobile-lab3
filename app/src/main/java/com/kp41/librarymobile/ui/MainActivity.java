package com.kp41.librarymobile.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kp41.librarymobile.model.Book;
import com.kp41.librarymobile.repository.BookRepository;

import java.util.List;

public class MainActivity extends Activity {
    private BookRepository repository;
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new BookRepository(this);
        buildLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void buildLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 28);

        TextView title = new TextView(this);
        title.setText("Бібліотека КП-41");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        Button createButton = new Button(this);
        createButton.setText("Додати книгу");
        createButton.setOnClickListener(v -> openDetails(0));
        root.addView(createButton);

        ScrollView scrollView = new ScrollView(this);
        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(listContainer);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(root);
    }

    private void loadBooks() {
        try {
            listContainer.removeAllViews();
            List<Book> books = repository.getAllBooks();
            if (books.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("Записів поки немає");
                empty.setTextSize(18);
                empty.setPadding(0, 24, 0, 0);
                listContainer.addView(empty);
                return;
            }
            for (Book book : books) {
                listContainer.addView(createBookRow(book));
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Помилка в опрацюванні запиту", Toast.LENGTH_LONG).show();
        }
    }

    private View createBookRow(Book book) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 18, 0, 18);

        TextView info = new TextView(this);
        info.setText(book.getTitle() + "\n" + book.getAuthor() + " • " + book.getGenre()
                + " • " + book.getYear() + " • " + book.getPages() + " с."
                + "\nСтатус: " + (book.isAvailable() ? "доступна" : "видана"));
        info.setTextSize(17);
        row.addView(info);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button edit = new Button(this);
        edit.setText("Редагувати");
        edit.setOnClickListener(v -> openDetails(book.getId()));
        buttons.addView(edit, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button delete = new Button(this);
        delete.setText("Видалити");
        delete.setOnClickListener(v -> confirmDelete(book));
        buttons.addView(delete, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        row.addView(buttons);
        return row;
    }

    private void openDetails(long bookId) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra("book_id", bookId);
        startActivity(intent);
    }

    private void confirmDelete(Book book) {
        new AlertDialog.Builder(this)
                .setTitle("Видалення книги")
                .setMessage("Видалити книгу '" + book.getTitle() + "'?")
                .setPositiveButton("Так", (dialog, which) -> {
                    try {
                        repository.deleteBook(book.getId());
                        loadBooks();
                    } catch (Exception ex) {
                        Toast.makeText(this, "Помилка в опрацюванні запиту", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Ні", null)
                .show();
    }
}
