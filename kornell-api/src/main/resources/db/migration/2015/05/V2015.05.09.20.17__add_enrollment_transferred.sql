create table if not exists EnrollmentTransferred (
	uuid char(36),
	eventFiredAt char(29),
	personUUID char(36),
	enrollmentUUID char(36),
	fromCourseClassUUID char(36),
	toCourseClassUUID char(36),
	primary key (uuid)
);
