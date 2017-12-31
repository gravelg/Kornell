CREATE TABLE IF NOT EXISTS Track (
	uuid char(36) NOT NULL,
	institutionUUID char(36) NOT NULL,
	name char(255) NOT NULL,
	PRIMARY KEY (`uuid`),
	FOREIGN KEY (institutionUUID) REFERENCES Institution(uuid)
);

CREATE TABLE IF NOT EXISTS TrackEnrollment (
	uuid char(36) NOT NULL,
	personUUID char(36) NOT NULL,
	trackUUID char(36) NOT NULL,
	PRIMARY KEY (`uuid`),
	FOREIGN KEY (personUUID) REFERENCES Person(uuid),
	FOREIGN KEY (trackUUID) REFERENCES Track(uuid)
);

CREATE TABLE IF NOT EXISTS TrackItem (
	uuid char(36) NOT NULL,
	courseVersionUUID char(36) NOT NULL,
	parentUUID char(36),
	`order` tinyint(4) NOT NULL DEFAULT 0,
	havingPreRequirements tinyint(1) NOT NULL DEFAULT 0,
	startDate timestamp,
	PRIMARY KEY (`uuid`),
	FOREIGN KEY (courseVersionUUID) REFERENCES CourseVersion(uuid),
	FOREIGN KEY (parentUUID) REFERENCES TrackItem(uuid)
);