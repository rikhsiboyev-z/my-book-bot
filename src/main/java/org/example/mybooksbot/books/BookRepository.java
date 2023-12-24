package org.example.mybooksbot.books;

import org.example.mybooksbot.books.dto.BookResponseDto;
import org.example.mybooksbot.books.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {


    List<Book> findByNameContainingIgnoreCase(String name);

}
