SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='title'
    ) > 0,
    "ALTER TABLE Course CHANGE title name varchar(255);",
    "SELECT 0"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='state'
    ) > 0,
    "SELECT 0",
    "alter table Course add column state char(36) not null default 'active';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND column_name='state'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion add column state char(36) not null default 'active';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;