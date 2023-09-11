-- Insert data into the "tool" table
INSERT INTO public.tool (name)
VALUES
    ('Hammer'),
    ('Screwdriver'),
    ('Wrench');

-- Insert data into the "consumable" table
INSERT INTO public.consumable (name)
VALUES
    ('Nails'),
    ('Screws'),
    ('Bolts');

-- Insert data into the "category" table
INSERT INTO public.category (name)
VALUES
    ('Camping'),
    ('Sports'),
    ('Arts and Crafts');

-- Insert data into the "email" table
INSERT INTO public.email (email)
VALUES
    ('admin@example.com');

-- Insert data into the "admin" table
INSERT INTO public.admin (email, lastname, firstname, password)
VALUES
    ('admin@example.com', 'Admin', 'Admin', '$2a$10$2r0fXin6LKi3ImGWJm0GWesx9esDPRrh4rC6Djt3lbhyJoFBsNQ2y');

-- Insert data into activity table
INSERT INTO public.activity (id, name, max_person, min_person, responsables, image, price, localization, date_list, category) VALUES ('e4dc2fe2-f18d-42db-bc04-77c99ad973f7', 'Table craft', 10, 2, 'Nicolas', '', 25, '{"lat": 46.7922781, "lng": 7.1603362}', null, 'Sports');
INSERT INTO public.consumable_needed (activity, consumable) VALUES ('e4dc2fe2-f18d-42db-bc04-77c99ad973f7', 'Screws');
INSERT INTO public.tool_needed (activity, tool) VALUES ('e4dc2fe2-f18d-42db-bc04-77c99ad973f7', 'Hammer');

-- Insert data into occurs table
INSERT INTO public.occurs (id_activity, date_activity, canceled) VALUES ('e4dc2fe2-f18d-42db-bc04-77c99ad973f7', '2023-05-11T09:30:00.000Z', false);
INSERT INTO public.occurs (id_activity, date_activity, canceled) VALUES ('e4dc2fe2-f18d-42db-bc04-77c99ad973f7', '2023-06-11T09:30:00.000Z', true);
INSERT INTO public.occurs (id_activity, date_activity, canceled) VALUES ('e4dc2fe2-f18d-42db-bc04-77c99ad973f7', '2024-05-11T09:30:00.000Z', false);
