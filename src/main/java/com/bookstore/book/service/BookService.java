package com.bookstore.book.service;

import com.bookstore.book.dto.BookRequest;
import com.bookstore.book.dto.BookResponse;
import com.bookstore.book.entity.Book;
import com.bookstore.book.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(BookRequest request) {

        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setQuantity(request.getQuantity());
        book.setCategory(request.getCategory());
        book.setImageUrl(request.getImageUrl());

        Book savedBook = bookRepository.save(book);

        return mapToResponse(savedBook);
    }

    public List<BookResponse> getAllBooks(
            String search,
            String category,
            String sortBy,
            String direction
    ) {

        // Convert empty values to null
        if (search != null && search.isBlank()) {
            search = null;
        }

        if (category != null && category.isBlank()) {
            category = null;
        }

        // Default sorting
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        if (direction == null || direction.isBlank()) {
            direction = "asc";
        }

        // Allow only valid fields for sorting
        List<String> allowedSortFields = List.of(
                "id",
                "title",
                "author",
                "price",
                "quantity",
                "category"
        );

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "id";
        }

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Sort sort = Sort.by(sortDirection, sortBy);

        List<Book> books;

        // No search and no category filter
        if (search == null && category == null) {

            books = bookRepository.findAll(sort);

        }

        // Search only
        else if (search != null && category == null) {

            books = bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                            search,
                            search,
                            sort
                    );

        }

        // Category only
        else if (search == null) {

            books = bookRepository
                    .findByCategoryIgnoreCase(
                            category,
                            sort
                    );

        }

        // Search + category
        else {

            books = bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndCategoryIgnoreCase(
                            search,
                            search,
                            category,
                            sort
                    );
        }

        return books.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found with id: " + id)
                );

        return mapToResponse(book);
    }

    public BookResponse updateBook(
            Long id,
            BookRequest request
    ) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found with id: " + id)
                );

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setQuantity(request.getQuantity());
        book.setCategory(request.getCategory());
        book.setImageUrl(request.getImageUrl());

        Book updatedBook = bookRepository.save(book);

        return mapToResponse(updatedBook);
    }

    public void deleteBook(Long id) {

        if (!bookRepository.existsById(id)) {
            throw new RuntimeException(
                    "Book not found with id: " + id
            );
        }

        bookRepository.deleteById(id);
    }

    private BookResponse mapToResponse(Book book) {

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPrice(),
                book.getQuantity(),
                book.getCategory(),
                book.getImageUrl()
        );
    }
}