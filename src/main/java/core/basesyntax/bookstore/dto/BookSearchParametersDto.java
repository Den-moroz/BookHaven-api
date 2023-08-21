package core.basesyntax.bookstore.dto;

public record BookSearchParametersDto(String[] title, String[] author,
                                      Integer fromPrice, Integer toPrice) {
}
