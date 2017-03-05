SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='pagseguroId'
    ) > 0,
    "SELECT 0",
    "alter table CourseClass add column pagseguroId character varying(20);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;