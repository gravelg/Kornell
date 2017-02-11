CREATE TABLE IF NOT EXISTS PostbackConfig (
	uuid char(36) NOT NULL,
	institutionUUID char(36) NOT NULL,
	postbackType varchar(255) NOT NULL,
	contents text NOT NULL, 
	PRIMARY KEY (`uuid`)
);
