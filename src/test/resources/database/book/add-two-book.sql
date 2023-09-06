INSERT INTO book (title, author, isbn, price, description, coverImage, is_deleted) VALUES ('Book 1', 'Author 1', 'ISBN-1', 19.99, 'Description 1', 'cover1.jpg', false);
INSERT INTO book (title, author, isbn, price, description, coverImage, is_deleted) VALUES ('Book 2', 'Author 2', 'ISBN-2', 24.99, 'Description 2', 'cover2.jpg', false);
INSERT INTO book_category (book_id, category_id) VALUES (1, 1);
INSERT INTO book_category (book_id, category_id) VALUES (1, 2);
INSERT INTO book_category (book_id, category_id) VALUES (2, 2);