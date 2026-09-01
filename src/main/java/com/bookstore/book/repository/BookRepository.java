package com.bookstore.book.repository;

import com.bookstore.book.entity.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Search by title or author
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title,
            String author,
            Sort sort
    );

    // Filter by category
    List<Book> findByCategoryIgnoreCase(
            String category,
            Sort sort
    );

    // Search by title/author AND filter by category
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndCategoryIgnoreCase(
            String title,
            String author,
            String category,
            Sort sort
    );
}