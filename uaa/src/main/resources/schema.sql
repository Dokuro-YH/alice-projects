DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
  id                     VARCHAR(255)                        NOT NULL,
  username               VARCHAR(255)                        NOT NULL,
  password               VARCHAR(255)                        NOT NULL,
  email                  VARCHAR(255),
  phone_number           VARCHAR(255),
  authorities            VARCHAR(2000),
  created                TIMESTAMP DEFAULT current_timestamp NOT NULL,
  modified               TIMESTAMP DEFAULT current_timestamp NOT NULL,
  last_logon_time        BIGINT,
  previous_logon_time    BIGINT,
  additional_information VARCHAR(2000),
  PRIMARY KEY (id),
  UNIQUE KEY (email),
  UNIQUE KEY (phone_number)
);

DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE IF NOT EXISTS oauth_client_details (
  client_id              VARCHAR(255) NOT NULL,
  client_secret          VARCHAR(255),
  authorities            VARCHAR(255),
  authorized_grant_types VARCHAR(255) NOT NULL,
  resource_ids           VARCHAR(255),
  scopes                 VARCHAR(255),
  auto_approve_scopes    VARCHAR(255),
  redirect_uris          VARCHAR(255),
  access_token_validity  INT,
  refresh_token_validity INT,
  additional_information VARCHAR(2000),
  PRIMARY KEY (client_id)
);