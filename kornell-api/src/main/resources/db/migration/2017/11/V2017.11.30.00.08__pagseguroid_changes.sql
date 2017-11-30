SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='pagseguroId'
    ) > 0,
    "alter table CourseClass change pagseguroId ecommerceIdentifier varchar(20);",
    "SELECT 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='ecommerceIdentifier'
    ) > 0,
    "SELECT 0;",
    "update CourseClass set ecommerceIdentifier = substring(replace(uuid(), '-', ''), 1,20) where ecommerceIdentifier is null or ecommerceIdentifier = '';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;