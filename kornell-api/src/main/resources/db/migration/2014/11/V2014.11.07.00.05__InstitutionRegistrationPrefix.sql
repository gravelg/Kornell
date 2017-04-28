DROP TABLE IF EXISTS `InstitutionRegistrationPrefix`;

CREATE  TABLE IF NOT EXISTS `InstitutionRegistrationPrefix` (
  `prefix` CHAR(29) NOT NULL ,
  `institutionUUID` CHAR(36) NOT NULL,
  PRIMARY KEY (`institutionUUID`, `prefix`) ,
  INDEX `a_idx` (`institutionUUID` ASC));