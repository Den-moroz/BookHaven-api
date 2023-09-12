INSERT INTO user (id, email, password, first_name, last_name, shipping_address)
VALUES (1, 'denis@i.ua', '$2a$12$JC92qHqc8tsPNdxXOCwKH.8kqHsVzAul/gGXrSIy9KXzNFkS68H6u', 'Denis', 'Unknown', '123, Kyiv');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);
