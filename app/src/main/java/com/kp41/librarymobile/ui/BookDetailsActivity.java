package com.kp41.librarymobile.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kp41.librarymobile.model.Book;
import com.kp41.librarymobile.repository.BookRepository;

public class BookDetailsActivity extends Activity {
    private BookRepository repository;
    private long bookId;
    private EditText titleInput;
    private EditText authorInput;
    private EditText yearInput;
    private EditText genreInput;
    private EditText pagesInput;
    private CheckBox availableInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new BookRepository(this);
        bookId = getIntent().getLongExtra("book_id", 0);
        buildLayout();
        if (bookId > 0) {
            loadBook();
        }
    }

    private void buildLayout() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 28);
        scrollView.addView(root);

        TextView title = new TextView(this);
        title.setText(bookId > 0 ? "Редагування книги" : "Створення книги");
        title.setTextSize(24);
        root.addView(title);

        titleInput = createTextInput("Назва");
        authorInput = createTextInput("Автор");
        yearInput = createNumberInput("Рік видання");
        genreInput = createTextInput("Жанр");
        pagesInput = createNumberInput("Кількість сторінок");
        availableInput = new CheckBox(this);
        availableInput.setText("Книга доступна");
        availableInput.setChecked(true);

        root.addView(titleInput);
        root.addView(authorInput);
        root.addView(yearInput);
        root.addView(genreInput);
        root.addView(pagesInput);
        root.addView(availableInput);

        Button saveButton = new Button(this);
        saveButton.setText("Зберегти");
        saveButton.setOnClickListener(v -> saveBook());
        root.addView(saveButton);

        Button backButton = new Button(this);
        backButton.setText("Назад");
        backButton.setOnClickListener(v -> finish());
        root.addView(backButton);

        setContentView(scrollView);
    }

    private EditText createTextInput(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        return input;
    }

    private EditText createNumberInput(String hint) {
        EditText input = createTextInput(hint);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        return input;
    }

    private void loadBook() {
        try {
            Book book = repository.getBookById(bookId);
            if (book == null) {
                Toast.makeText(this, "Запис не знайдено", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            titleInput.setText(book.getTitle());
            authorInput.setText(book.getAuthor());
            yearInput.setText(String.valueOf(book.getYear()));
            genreInput.setText(book.getGenre());
            pagesInput.setText(String.valueOf(book.getPages()));
            availableInput.setChecked(book.isAvailable());
        } catch (Exception ex) {
            Toast.makeText(this, "Помилка в опрацюванні запиту", Toast.LENGTH_LONG).show();
        }
    }

    private void saveBook() {
        try {
            Book book = new Book();
            book.setId(bookId);
            book.setTitle(titleInput.getText().toString());
            book.setAuthor(authorInput.getText().toString());
            book.setYear(Integer.parseInt(yearInput.getText().toString()));
            book.setGenre(genreInput.getText().toString());
            book.setPages(Integer.parseInt(pagesInput.getText().toString()));
            book.setAvailable(availableInput.isChecked());

            if (bookId > 0) {
                repository.updateBook(book);
            } else {
                repository.createBook(book);
            }
            finish();
        } catch (Exception ex) {
            Toast.makeText(this, "Помилка в опрацюванні запиту", Toast.LENGTH_LONG).show();
        }
    }
}
