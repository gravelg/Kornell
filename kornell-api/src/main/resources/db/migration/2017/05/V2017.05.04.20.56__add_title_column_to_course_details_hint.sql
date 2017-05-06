DROP TABLE IF EXISTS CourseDetailsHint;
CREATE TABLE IF NOT EXISTS CourseDetailsHint (
	uuid char(36) NOT NULL,
	title varchar(255) NOT NULL,
	text text NOT NULL,
	entityType char(16) NOT NULL,
	entityUUID char(36) NOT NULL,
	`index` tinyint NOT NULL,
	fontAwesomeClassName char(255),
	PRIMARY KEY (`uuid`)
);