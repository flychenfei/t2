-- set search_path to public; 

-- --------- user --------- --
CREATE TABLE "user"
(
	"id" bigserial NOT NULL,
	"username" character varying(128) NOT NULL,
	"password" character varying(128),
	CONSTRAINT user_pkey PRIMARY KEY (id)
);
ALTER TABLE "user" OWNER TO samplesocial_user;
-- --------- /user --------- --

-- --------- gmail_analytics --------- --
CREATE TABLE "gmailanalytics"
(
	"id" bigserial NOT NULL,
	"userId" bigint NOT NULL,
	"messageSubject" character varying(256),
	"senderTimeStamp" timestamp,
	"recipientTimeStamp" timestamp,
	"ConversationName" character varying(256),
	"messageType" character varying(32),
	"senderEmailAddress" character varying(256),
	"recipientEmailAddress" character varying(256),
	"recipientType" character varying(32),
	"messageLength" bigint,
	"countOfAttachments" bigint,
	"messageSize" bigint,
	"elapsedTimeSincePreviousMessage" interval,
	CONSTRAINT gmailanalytics_pkey PRIMARY KEY (id)
);
ALTER TABLE "gmailanalytics" OWNER TO samplesocial_user;
-- --------- /gmail_analytics --------- --
