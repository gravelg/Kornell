SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='internationalized'
    ) > 0,
    "alter table Institution drop column internationalized;",
    "SELECT 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;