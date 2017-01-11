DROP TABLE ActomEntryChangedEvent;
CREATE TABLE `ActomEntryChangedEvent` (
  `uuid` char(36) NOT NULL,
  `enrollment_uuid` char(36) DEFAULT NULL,
  `actomKey` varchar(127) DEFAULT NULL,
  `entryKey` varchar(127) DEFAULT NULL,
  `entryValue` varchar(127) DEFAULT NULL,
  `ingestedAt` char(29) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  KEY `fk_actomentrychangedevent_enrollment_enrollmentUUID_idx` (`enrollment_uuid`)
)
alter table ebdb.ActomEntries modify column entryValue varchar(255);
