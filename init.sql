CREATE DATABASE archive CHARACTER SET UTF8;

USE archive;

CREATE TABLE archive_documents(
    id int AUTO_INCREMENT PRIMARY KEY,
    document_name varchar(25),
    document_path varchar(250) UNIQUE,

) ENGINE=InnoDB CHARACTER SET=utf8;

CREATE TABLE category(
    id int AUTO_INCREMENT PRIMARY KEY,
    category varchar(25),
) ENGINE=InnoDB CHARACTER SET=utf8;

CREATE TABLE document_metadata(
  document_id int PRIMARY KEY,
  date_upload TIMESTAMP,
  category_id int null,
  FOREIGN KEY (document_id) REFERENCES archive_documents(id),
  FOREIGN KEY (category_id) REFERENCES category(id),
) ENGINE=InnoDB CHARACTER SET=utf8;


