INSERT INTO user (id, email, password, first_name, last_name)
VALUES (1, 'email@i.ua', 'password', 'Denis', 'Unknown');

INSERT INTO role (id, name)
VALUES (1, 'ROLE_ADMIN');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);

INSERT INTO book (id, title, author, isbn, price)
VALUES (1, 'Title 1', 'Author 1', '978-0307743657', 100);

INSERT INTO shopping_cart (user_id)
VALUES (1);

INSERT INTO cart_item (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 10);

INSERT INTO shopping_cart_cart_item(shopping_cart_id, cart_item_id)
VALUES (1, 1);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address)
VALUES (1, 1, 'PENDING', 100, NOW(), '123, London');

INSERT INTO order_item (id, order_id, book_id, quantity, price)
VALUES (1, 1, 1, 10, 100);
