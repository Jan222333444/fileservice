CREATE DATABASE archive;


CREATE TABLE archive_documents(
    id int PRIMARY KEY,
    document_name varchar(25),
    document_path varchar(250) UNIQUE

);

CREATE TABLE category(
    id int PRIMARY KEY,
    category varchar(25)
);

CREATE TABLE document_metadata(
  document_id int PRIMARY KEY,
  date_upload TIMESTAMP,
  category_id int null,
  FOREIGN KEY (document_id) REFERENCES archive_documents(id),
  FOREIGN KEY (category_id) REFERENCES category(id)
);


