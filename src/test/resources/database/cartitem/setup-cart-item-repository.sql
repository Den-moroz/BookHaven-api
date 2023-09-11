INSERT INTO book (id, title, author, isbn, price)
VALUES (1, 'Title 1', 'Author 1', '978-0307743657', 100);

INSERT INTO user (id, email, password, first_name, last_name)
VALUES (1, 'email@i.ua', 'password', 'Denis', 'Unknown');

INSERT INTO shopping_cart (user_id)
VALUES (1);

INSERT INTO cart_item (id, book_id, quantity, shopping_cart_id)
VALUES (1, 1, 100, 1);

INSERT INTO shopping_cart_cart_item (shopping_cart_id, cart_item_id)
VALUES (1, 1)