SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='advancedMode'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column advancedMode tinyint(1) default 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='contentSpec'
    ) > 0,
    "SELECT 0",
    "alter table Course add column contentSpec varchar(255);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='contentSpec'
    ) > 0,
    "UPDATE Course c INNER JOIN CourseVersion cv on c.uuid = cv.course_uuid set c.contentSpec = cv.contentSpec;",
    "SELECT 0"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;