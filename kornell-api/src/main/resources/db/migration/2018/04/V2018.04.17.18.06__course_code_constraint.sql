SET @sql = (SELECT IF(
    (select count(*) from information_schema.table_constraints 
		where table_schema = 'ebdb'
		and table_name = 'Course'
		and constraint_name = 'code'
    ) = 0,
    "SELECT 0",
    "alter table Course drop index code;"
));
PREPARE stmt FROM @sql;