SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='thumbUrl'
    ) > 0,
    "SELECT 0",
    "alter table Course add column thumbUrl text;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND column_name='thumbUrl'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion add column thumbUrl text;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='thumbUrl'
    ) > 0,
    "SELECT 0",
    "alter table CourseClass add column thumbUrl text;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;