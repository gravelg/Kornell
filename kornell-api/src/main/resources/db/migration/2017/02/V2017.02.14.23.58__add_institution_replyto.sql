SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='replyTo'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column replyTo character varying(128);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;