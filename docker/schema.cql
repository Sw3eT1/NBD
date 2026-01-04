-- KEYSPACE z replikacją na dwa węzły w DC "dc1"
-- To spełnia wymaganie: konfiguracja poziomu replikacji (RF=2)
CREATE KEYSPACE IF NOT EXISTS library
WITH replication = {
'class': 'NetworkTopologyStrategy',
'dc1': 2
}
AND durable_writes = true;

USE library;

-- UDT na Address (zgodny z klasą Address w kodzie)
CREATE TYPE IF NOT EXISTS address (
house_number text,
street text,
city text,
state text,
zipcode text,
country text
);

-- Biblioteki
CREATE TABLE IF NOT EXISTS libraries_by_id (
library_id text PRIMARY KEY,
name text,
address frozen<address>,
phone_number text,
email text,
website text,
open_on_weekends boolean,
opening_hours text
);

-- Książki (po ID)
CREATE TABLE IF NOT EXISTS books_by_id (
book_id text PRIMARY KEY,
title text,
author text,
publisher text,
genre text,
isbn text,
publication_year int,
pages int,
language text,
description text
);

-- Egzemplarze książek zdenormalizowane po bibliotece
CREATE TABLE IF NOT EXISTS book_copies_by_library (
      library_id text,
      book_id text,
      copy_id text,
      status text,
      PRIMARY KEY ((library_id), book_id, copy_id)
);

-- Typy czytelników (Adult/Kid/Teenager)
CREATE TABLE IF NOT EXISTS reader_types (
reader_type_id text PRIMARY KEY,
name text,
max_books int
);

-- Czytelnicy per biblioteka
CREATE TABLE IF NOT EXISTS readers_by_library (
library_id text,
reader_id text,
name text,
surname text,
email text,
phone text,
address frozen<address>,
card_number text,
reader_type_id text,
active_rentals int,
PRIMARY KEY ((library_id), reader_id)
);

-- Pracownicy per biblioteka
CREATE TABLE IF NOT EXISTS employees_by_library (
library_id text,
employee_id text,
name text,
surname text,
email text,
phone text,
address frozen<address>,
position text,
salary double,
hire_date date,
PRIMARY KEY ((library_id), employee_id)
);

-- Wypożyczenia per czytelnik
CREATE TABLE IF NOT EXISTS rentals_by_reader (
 reader_id text,
 rental_id text,
 book_copy_id text,
 rental_date date,
 due_date date,
 return_date date,
 status text,
 fine double,
 PRIMARY KEY ((reader_id), rental_id)
);
