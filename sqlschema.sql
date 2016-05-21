

create database hr;

use database hr;

CREATE TABLE sources
  (
     sourceId        BIGINT NOT NULL auto_increment,
     sourceName   VARCHAR(20),
     sourceType   VARCHAR(20),
     sourcePath   VARCHAR(255),
     PRIMARY KEY (sourceId)
  );

CREATE TABLE fields
  (
     fieldId        BIGINT NOT NULL auto_increment,
     sourceId       BIGINT NOT NULL,
     fieldName   VARCHAR(20),
     dataType   VARCHAR(20),
     valueType   VARCHAR(255),
     properties   VARCHAR(10000),
     PRIMARY KEY (fieldId)
  );
