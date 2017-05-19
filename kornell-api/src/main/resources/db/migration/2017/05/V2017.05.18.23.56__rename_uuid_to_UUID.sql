SET @sql = (SELECT 
  IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name = 'ActomEntries'
          AND column_name = 'enrollment_uuid'
    ) > 0,
    "ALTER TABLE ActomEntries CHANGE enrollment_uuid enrollmentUUID char(36);",
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
          AND table_name = 'ActomEntryChangedEvent'
          AND column_name = 'enrollment_uuid'
    ) > 0,
    "ALTER TABLE ActomEntryChangedEvent CHANGE enrollment_uuid enrollmentUUID char(36);",
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
          AND table_name = 'CourseClass'
          AND column_name = 'courseVersion_uuid'
    ) > 0,
    "ALTER TABLE CourseClass CHANGE courseVersion_uuid courseVersionUUID char(36);",
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
          AND table_name = 'CourseClass'
          AND column_name = 'institution_uuid'
    ) > 0,
    "ALTER TABLE CourseClass CHANGE institution_uuid institutionUUID char(36);",
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
          AND table_name = 'CourseVersion'
          AND column_name = 'course_uuid'
    ) > 0,
    "ALTER TABLE CourseVersion CHANGE course_uuid courseUUID char(36);",
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
          AND column_name = 'person_uuid'
    ) > 0,
    "ALTER TABLE Enrollment CHANGE person_uuid personUUID char(36);",
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
          AND column_name = 'class_uuid'
    ) > 0,
    "ALTER TABLE Enrollment CHANGE class_uuid classUUID char(36);",
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
          AND table_name = 'EnrollmentStateChanged'
          AND column_name = 'person_uuid'
    ) > 0,
    "ALTER TABLE EnrollmentStateChanged CHANGE person_uuid personUUID char(36);",
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
          AND table_name = 'EnrollmentStateChanged'
          AND column_name = 'enrollment_uuid'
    ) > 0,
    "ALTER TABLE EnrollmentStateChanged CHANGE enrollment_uuid enrollmentUUID char(36);",
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
          AND table_name = 'Password'
          AND column_name = 'person_uuid'
    ) > 0,
    "ALTER TABLE Password CHANGE person_uuid personUUID char(36);",
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
          AND table_name = 'Role'
          AND column_name = 'institution_uuid'
    ) > 0,
    "ALTER TABLE Role CHANGE institution_uuid institutionUUID char(36);",
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
          AND table_name = 'Role'
          AND column_name = 'course_class_uuid'
    ) > 0,
    "ALTER TABLE Role CHANGE course_class_uuid course_classUUID char(36);",
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
          AND table_name = 'Role'
          AND column_name = 'person_uuid'
    ) > 0,
    "ALTER TABLE Role CHANGE person_uuid personUUID char(36);",
    "SELECT 0"
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
