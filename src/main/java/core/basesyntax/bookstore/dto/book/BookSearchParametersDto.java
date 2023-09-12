package core.basesyntax.bookstore.dto.book;

public record BookSearchParametersDto(String[] title, String[] author,
                                      Integer fromPrice, Integer toPrice) {
}
