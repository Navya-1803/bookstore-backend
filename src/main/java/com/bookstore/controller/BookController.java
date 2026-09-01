package com.bookstore.controller;

import com.bookstore.dto.BookRequest;
import com.bookstore.dto.BookResponse;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookRequest request
    ) {

        BookResponse response =
                bookService.createBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        return ResponseEntity.ok(
                bookService.getAllBooks()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                bookService.getBookById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {

        return ResponseEntity.ok(
                bookService.updateBook(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}