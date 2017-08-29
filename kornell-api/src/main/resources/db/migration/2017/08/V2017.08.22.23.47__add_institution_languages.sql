SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='allowedLanguages'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column allowedLanguages char(64) not null default 'pt_BR';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
