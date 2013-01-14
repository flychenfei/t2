DROP SCHEMA IF EXISTS samplesocial CASCADE;


CREATE SCHEMA samplesocial;

-- ............................................................................

GRANT USAGE ON SCHEMA samplesocial to samplesocial;

-- ............................................................................

SET search_path TO samplesocial;

CREATE TABLE principal
(
  id 							bigserial NOT NULL,
  username 						character varying(128) NOT NULL unique ,
  password 						character varying(128),
  createdby_id                  bigint,
  created_date                  timestamp without time zone,
  updatedby_id                  bigint,
  updated_date                  timestamp without time zone,
  CONSTRAINT principal_pkey PRIMARY KEY (id )
);

GRANT ALL PRIVILEGES ON principal to samplesocial;
GRANT ALL PRIVILEGES ON principal_id_seq to samplesocial;

CREATE TABLE social_id_entity
(
  id 							bigserial NOT NULL,
  user_id 						bigint NOT NULL,
  token 						character varying(256),
  token_date 					timestamp without time zone,
  service 						character varying(256),
  email 						character varying(256),
  createdby_id                  bigint,
  created_date                  timestamp without time zone,
  updatedby_id                  bigint,
  updated_date                  timestamp without time zone,
  CONSTRAINT social_id_entity_pkey PRIMARY KEY (id )
);

GRANT ALL PRIVILEGES ON social_id_entity to samplesocial;
GRANT ALL PRIVILEGES ON social_id_entity_id_seq to samplesocial;

CREATE TABLE contact
(
  id 							bigserial NOT NULL,
  name 							character varying(256),
  fbid 							character varying(256),
  fbtoken 						character varying(256),
  email 						character varying(256),
  hometownname 					character varying(256),
  createdby_id                  bigint,
  created_date                  timestamp without time zone,
  updatedby_id                  bigint,
  updated_date                  timestamp without time zone,
  CONSTRAINT contact_pkey PRIMARY KEY (id )
);

GRANT ALL PRIVILEGES ON contact to samplesocial;
GRANT ALL PRIVILEGES ON contact_id_seq to samplesocial;