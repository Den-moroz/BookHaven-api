INSERT INTO user (id, email, password, first_name, last_name)
VALUES (1, 'email@i.ua', 'password', 'Denis', 'Unknown');

INSERT INTO book (id, title, author, isbn, price)
VALUES (1, 'Title 1', 'Author 1', '978-0307743657', 100);

INSERT INTO orders (id, user_id, status, order_date, total, shipping_address)
VALUES (1, 1, 'PENDING', '2023-1-20 20:20:20', 200, '134, Kyiv');

INSERT INTO order_item (id, book_id, order_id, price, quantity)
VALUES (1, 1, 1, 100, 1),
       (2, 1, 1, 100, 3);
