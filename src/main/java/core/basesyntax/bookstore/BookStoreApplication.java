package core.basesyntax.bookstore;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(BookService bookService) {
        return args -> {
            Book newBook = new Book();
            newBook.setTitle("Kobzar");
            newBook.setPrice(BigDecimal.valueOf(100));
            newBook.setAuthor("Taras Schevchenko");
            newBook.setIsbn("a1b");
            bookService.save(newBook);
            bookService.findAll().forEach(System.out::println);
        };
    }
}
