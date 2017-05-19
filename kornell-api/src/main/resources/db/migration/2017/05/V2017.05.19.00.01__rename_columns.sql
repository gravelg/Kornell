SET @sql = (SELECT 
  IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name = 'Role'
          AND column_name = 'course_classUUID'
    ) > 0,
    "ALTER TABLE Role CHANGE course_classUUID courseClassUUID char(36);",
    "SELECT 0"
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;



SET @sql = (SELECT 
  IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name = 'Enrollment'
          AND column_name = 'classUUID'
    ) > 0,
    "ALTER TABLE Enrollment CHANGE classUUID courseClassUUID char(36);",
    "SELECT 0"
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;

