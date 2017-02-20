SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='institutionSupportEmail'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column institutionSupportEmail character varying(128);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;