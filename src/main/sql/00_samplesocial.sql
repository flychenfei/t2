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
  token 						character varying(2048),
  token_date 					timestamp without time zone,
  secret 						character varying(128),
  service 						character varying(256),
  email 						character varying(256),
  fbid 							character varying(256),
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


CREATE TABLE contactinfo
(
  id 							bigserial NOT NULL,
  name 							character varying(256),
  birthday character varying(256),
	contactid int8,
	education character varying(256),
	email character varying(256),
	favoriteathletes character varying(256),
	favoriteteams character varying(256),
	firstname character varying(256),
	gender character varying(256),
	hometown character varying(256),
	hometownname character varying(256),
	interestedin character varying(256),
	languages character varying(256),
	lastname character varying(256),
	link character varying(256),
	locale character varying(256),
	location character varying(256),
	meetingfor character varying(256),
	middlename character varying(256),
	quotes character varying(256),
	relationshipstatus character varying(256),
	religion character varying(256),
	significantother character varying(256),
	sports character varying(256),
	thirdpartyid character varying(256),
	timezone character varying(256),
	type character varying(256),
	updatedtime character varying(256),
	username character varying(256),
	verified character varying(256),
	website character varying(256),
	work character varying(256),
  createdby_id                  bigint,
  created_date                  timestamp without time zone,
  updatedby_id                  bigint,
  updated_date                  timestamp without time zone,
  CONSTRAINT contactinfo_pkey PRIMARY KEY (id )
);

GRANT ALL PRIVILEGES ON contactinfo to samplesocial;
GRANT ALL PRIVILEGES ON contactinfo_id_seq to samplesocial;