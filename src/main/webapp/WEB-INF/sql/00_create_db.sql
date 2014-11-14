DROP DATABASE IF EXISTS samplesocial_db;
DROP USER IF EXISTS samplesocial_user;
CREATE USER samplesocial_user PASSWORD 'welcome';
CREATE DATABASE samplesocial_db owner samplesocial_user ENCODING = 'UTF-8';


-- create extension (as superuser)
\c samplesocial_db
CREATE EXTENSION hstore;
CREATE EXTENSION pg_trgm;

\c samplesocial_db samplesocial_user