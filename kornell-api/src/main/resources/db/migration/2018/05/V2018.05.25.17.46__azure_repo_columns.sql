SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='sandbox'
    ) > 0,
    "SELECT 0",
    "alter table ContentRepository
      add column accountName varchar(64),
      add column accountKey varchar(64),
      add column container varchar(64);
    "
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
