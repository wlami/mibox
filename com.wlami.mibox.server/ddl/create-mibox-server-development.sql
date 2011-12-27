CREATE TABLE USER (USERNAME VARCHAR(255) NOT NULL, EMAIL VARCHAR(255), PASSWORD VARCHAR(255), PRIMARY KEY (USERNAME));
CREATE TABLE CHUNKHDDMAPPING (ID char(64), VALUE char(36), PRIMARY KEY (ID));
CREATE INDEX INDEX_CHUNKHDDMAPPING_VALUE ON CHUNKHDDMAPPING (VALUE);
CREATE TABLE GROUPS (USERNAME VARCHAR(255) NOT NULL, GROUPS VARCHAR(255) NOT NULL, PRIMARY KEY (USERNAME, GROUPS));

-- INSERT STATEMENTS FOR DEVELOPMENT
INSERT INTO USER (USERNAME, EMAIL, PASSWORD) VALUES ("admin", "admin@mibox", "admin");
INSERT INTO USER (USERNAME, EMAIL, PASSWORD) VALUES ("user",  "user@mibox",  "user" );
INSERT INTO GROUPS (USERNAME, GROUPS) VALUES ("admin", "admin");
INSERT INTO GROUPS (USERNAME, GROUPS) VALUES ("user",  "user" );