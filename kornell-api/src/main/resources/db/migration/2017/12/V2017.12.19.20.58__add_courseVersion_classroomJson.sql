SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND column_name='classroomJson'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion add column classroomJson mediumtext;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
