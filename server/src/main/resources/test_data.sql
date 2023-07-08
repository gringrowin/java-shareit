INSERT INTO users (name, email)
VALUES ('user1', 'user1@mail.ru'),
       ('user2', 'user2@mail.ru'),
       ('user3', 'user3@mail.ru'),
       ('user4', 'user4@mail.ru'),
       ('user5', 'user5@mail.ru');

INSERT INTO item_requests (description, requester_id, created)
VALUES ('description1', 1, '2022-01-01 00:00:00'),
       ('description2', 2, '2022-02-02 00:00:00'),
       ('description3', 3, '2020-03-03 00:00:00'),
       ('description4', 3, '2020-04-04 00:00:00');

INSERT INTO items (name, description, available, owner_id, item_request_id)
VALUES ('item1', 'description1', true, 1, null),
       ('item2', 'description2', true, 1, 3),
       ('item3', 'description3', false, 2, null),
       ('item4', 'description4', true, 4, 1);

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2020-01-01 00:00:00', '2021-01-01 00:00:00', 1, 4, 'APPROVED'),
       ('2023-01-01 00:00:00', '2024-01-01 00:00:00', 1, 4, 'REJECTED'),
       ('2022-01-01 00:00:00', '2025-01-01 00:00:00', 1, 2, 'WAITING'),
       ('20024-01-01 00:00:00', '20026-01-01 00:00:00', 1, 3, 'APPROVED'),
       ('2020-01-01 00:00:00', '2021-01-01 00:00:00', 2, 4, 'APPROVED'),
       ('20-01-01 00:00:00', '20021-01-01 00:00:00', 3, 4, 'APPROVED');

INSERT INTO comments (text, author_id, item_id, created_time)
VALUES ('text1', 3, 1, '2020-01-01 00:00:00'),
       ('text2', 4, 1, '2020-01-01 00:00:00'),
       ('text3', 2, 2, '2020-01-01 00:00:00'),
       ('text4', 2, 2, '2020-01-01 00:00:00'),
       ('text5', 3, 3, '2020-01-01 00:00:00'),
       ('text6', 3, 4, '2020-01-01 00:00:00');