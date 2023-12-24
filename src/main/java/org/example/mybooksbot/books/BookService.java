package org.example.mybooksbot.books;

import lombok.RequiredArgsConstructor;
import org.example.mybooksbot.books.dto.BookResponseDto;
import org.example.mybooksbot.books.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ModelMapper mapper;


    public List<BookResponseDto> getAll() {

        List<Book> all = bookRepository.findAll();
        return all.stream()
                .map(book -> mapper.map(book, BookResponseDto.class))
                .collect(Collectors.toList());

    }

    public List<BookResponseDto> searchByName(String name) {
        List<Book> books = bookRepository.findByNameContainingIgnoreCase(name);
        return books.stream().map(book -> mapper.map(book, BookResponseDto.class)).collect(Collectors.toList());
    }
}
