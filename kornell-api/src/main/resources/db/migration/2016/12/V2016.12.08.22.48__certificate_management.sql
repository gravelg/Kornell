CREATE TABLE IF NOT EXISTS CertificateDetails (
	uuid char(36) NOT NULL,
	bgImage text NOT NULL,
	certificateType char(36) NOT NULL,
	entityType char(16) NOT NULL,
	entityUUID char(36) NOT NULL,
	PRIMARY KEY (`uuid`)
);