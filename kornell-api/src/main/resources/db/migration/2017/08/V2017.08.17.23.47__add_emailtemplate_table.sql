CREATE TABLE IF NOT EXISTS EmailTemplate (
	uuid char(36) NOT NULL,
	templateType char(64) NOT NULL,
	locale varchar(5) NOT NULL,
	title varchar(255) NOT NULL,
	template text NOT NULL,
	PRIMARY KEY (`uuid`)
);
