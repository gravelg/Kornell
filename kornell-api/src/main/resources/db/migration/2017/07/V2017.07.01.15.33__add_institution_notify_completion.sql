SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='notifyInstitutionAdmins'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column notifyInstitutionAdmins tinyint(1) default 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
