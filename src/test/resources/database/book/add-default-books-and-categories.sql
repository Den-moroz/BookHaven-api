INSERT INTO book (id, title, author, isbn, price, description, cover_image)
VALUES
    (1, 'Book 1 Title', 'Author 1', '978-0-316-03647-3', 20, 'Description 1 for book 1', 'http://example.com/book1.jpg'),
    (2, 'Book 2 Title', 'Author 2', '978-0-316-03647-7', 45, 'Description 2 for book 2', 'http://example.com/book2.jpg'),
    (3, 'Book 3 Title', 'Author 3', '978-0-316-03647-4', 28, 'Description 3 for book 3', 'http://example.com/book3.jpg');

INSERT INTO category (id, name, description)
VALUES
    (1, 'Category 1', 'Description 1'),
    (2, 'Category 2', 'Description 2');

INSERT INTO book_category (book_id, category_id)
VALUES
    (1, 2),
    (2, 1),
    (3, 2);
