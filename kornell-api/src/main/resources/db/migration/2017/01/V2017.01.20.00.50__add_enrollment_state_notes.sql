SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='EnrollmentStateChanged' 
          AND column_name='notes'
    ) > 0,
    "SELECT 0",
    "alter table EnrollmentStateChanged add column notes text;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Enrollment' 
          AND column_name='enrollmentSource'
    ) > 0,
    "SELECT 0",
    "alter table Enrollment add column enrollmentSource character varying(32);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
update Enrollment set enrollmentSource = 'WEBSITE';