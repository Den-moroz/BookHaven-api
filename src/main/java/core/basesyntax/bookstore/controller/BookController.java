package core.basesyntax.bookstore.controller;

import core.basesyntax.bookstore.dto.BookDto;
import core.basesyntax.bookstore.dto.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.CreateBookRequestDto;
import core.basesyntax.bookstore.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService bookService;

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping(path = "/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping(path = "/search")
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters) {
        return bookService.search(searchParameters);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
