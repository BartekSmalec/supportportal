CREATE TABLE IF NOT EXISTS users (
   id BIGINT NOT NULL,
   user_id VARCHAR(255) NULL,
   first_name VARCHAR(255) NULL,
   last_name VARCHAR(255) NULL,
   username VARCHAR(255) NULL,
   password VARCHAR(255) NULL,
   email VARCHAR(255) NULL,
   profile_image_url VARCHAR(255) NULL,
   last_login_date TIMESTAMP NULL,
   last_login_date_display TIMESTAMP NULL,
   join_date TIMESTAMP NULL,
   role VARCHAR(255) NULL,
   is_active BOOLEAN NULL,
   is_non_locked BOOLEAN NULL,
   authorities bytea NULL,
   CONSTRAINT pk_users PRIMARY KEY (id)
);

