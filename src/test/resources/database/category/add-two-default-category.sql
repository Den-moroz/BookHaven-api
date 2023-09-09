INSERT INTO category (id, name, description)
VALUES
    (1, 'Category 1', 'Description 1'),
    (2, 'Category 2', 'Description 2'),
    (3, 'Category 3', 'Description 3');

INSERT INTO book (id, title, author, isbn, price, description, cover_image)
VALUES
    (1, 'Title 1', 'Author 1', 'ISBN 1', 20, 'Description 1', 'Cover image 1');
INSERT INTO book_category (book_id, category_id)
VALUES
    (1, 1)