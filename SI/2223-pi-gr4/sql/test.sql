-- Fail due to the date
INSERT INTO public.participation (participant, "id_activity", "date_activity")
VALUES
    ('johndoe@example.com', 'c0b9bac4-4cbf-47ba-8be2-63695531876c', '2023-03-10');

-- Fail due to the date and the cancel state
INSERT INTO public.participation (participant, "id_activity", "date_activity")
VALUES
    ('johndoe@example.com', 'c0b9bac4-4cbf-47ba-8be2-63695531876c', '2023-03-11');

-- Fail due to the cancel state
INSERT INTO public.participation (participant, "id_activity", "date_activity")
VALUES
    ('johndoe@example.com', 'c3d2f9f8-3622-4145-8398-169586ae5dd1', '2023-04-02');