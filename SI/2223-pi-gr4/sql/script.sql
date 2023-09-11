drop table if exists "email" cascade;
drop table if exists "admin" cascade;
drop table if exists "participant" cascade;
drop table if exists "activity" cascade;
drop table if exists "tool_needed" cascade;
drop table if exists "tool" cascade;
drop table if exists "consumable_needed" cascade;
drop table if exists "consumable" cascade;
drop table if exists "category" cascade;
drop table if exists "occurs" cascade;
drop table if exists "participation" cascade;
drop table if exists "info" cascade;
drop view if exists "occurs_view" cascade;


create table public.info
(
    id    varchar(255) not null
        primary key,
    value varchar(255)
);

create table public.tool
(
    name varchar not null
        primary key
);

create table public.consumable
(
    name varchar not null
        primary key
);

create table public.category
(
    name varchar not null
        primary key
);

create table public.activity
(
    id              varchar not null
        primary key,
    name            varchar not null,
    "max_person"    integer not null,
    "min_person"    integer not null,
    responsables    varchar not null,
    image           bytea   not null,
    price           numeric not null,
    localization    json    not null,
    "date_list"     json,
    category        varchar
        references public.category,
    modification_id numeric default 0
);

create table public.occurs
(
    "id_activity"   varchar not null
        references public.activity on delete cascade,
    "date_activity" date    not null,
    canceled        boolean,
    constraint occurs_pk
        primary key ("id_activity", "date_activity")
);

create table public."tool_needed"
(
    activity varchar not null
        constraint "tool_needed_activity_id_fk"
            references public.activity on delete cascade,
    tool     varchar not null
        constraint "tool_needed_tool_name_fk"
            references public.tool,
    constraint "pk_tool_needed"
        primary key (activity, tool)
);

create table public."consumable_needed"
(
    activity   varchar not null
        constraint "consumable_needed_activity_id_fk"
            references public.activity on delete cascade,
    consumable varchar not null
        constraint "consumable_needed_consumable_name_fk"
            references public.consumable,
    constraint "pk_consumable_needed"
        primary key (activity, consumable)
);

create table public.email
(
    email varchar not null
        primary key
);

create table public.participant
(
    email     varchar not null
        primary key
        references public.email,
    lastname  varchar not null,
    firstname varchar not null,
    password  varchar not null
);

create table public.admin
(
    email     varchar not null
        primary key
        references public.email,
    firstname varchar not null,
    lastname  varchar not null,
    password  varchar
);

create table public.participation
(
    participant     varchar not null
        constraint participation_participant_email_fk
            references public.participant on delete cascade,
    "id_activity"   varchar not null,
    "date_activity" date    not null,
    constraint "pk_participation"
        primary key (participant, "date_activity", "id_activity"),
    constraint "participation_occurs_id_activity_date_activity_fk"
        foreign key ("id_activity", "date_activity") references public.occurs on delete cascade
);


-- TRIGGERS
CREATE OR REPLACE FUNCTION check_occurs_date()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS(SELECT 1
              FROM occurs
              WHERE "id_activity" = NEW."id_activity"
                AND "date_activity" = NEW."date_activity"
                AND "date_activity" <= NOW()) THEN
        RAISE EXCEPTION 'Occurs state must be opened state and not teminated';
    END IF;
    IF EXISTS(SELECT 1
              FROM occurs
              WHERE "id_activity" = NEW."id_activity"
                AND "date_activity" = NEW."date_activity"
                AND "canceled") THEN
        RAISE EXCEPTION 'Occurs state must be opened state and not canceled';
    END IF;
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_participation_date
    BEFORE INSERT OR UPDATE
    ON participation
    FOR EACH ROW
EXECUTE FUNCTION check_occurs_date();

--- Trigger to add a new email when a new participant is added
CREATE OR REPLACE FUNCTION add_email()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO email (email)
    VALUES (NEW.email);
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER add_email
    BEFORE INSERT
    ON participant
    FOR EACH ROW
EXECUTE FUNCTION add_email();

create or replace view public.occurs_view
            (id, date_activity, canceled, name, max_person, min_person, responsables, image, price, localization,
             category)
as
SELECT a.id,
       occurs.date_activity,
       occurs.canceled,
       a.name,
       a.max_person,
       a.min_person,
       a.responsables,
       a.image,
       a.price,
       a.localization,
       a.category
FROM occurs
         JOIN activity a ON a.id::text = occurs.id_activity::text;

-- Insert data into the "info" table
INSERT INTO public.info (id, value)
VALUES ('db_version', '0.1.0');

-- Création de l'utilisateur manager
CREATE USER manager WITH PASSWORD 'aodimfoas';
GRANT ALL PRIVILEGES ON DATABASE postgres TO manager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO manager;

-- Création de l'utilisateur guest
CREATE USER guest WITH PASSWORD 'ldjaipwqnjds';
GRANT SELECT, DELETE, INSERT, UPDATE, TRIGGER ON ALL TABLES IN SCHEMA public TO guest;
