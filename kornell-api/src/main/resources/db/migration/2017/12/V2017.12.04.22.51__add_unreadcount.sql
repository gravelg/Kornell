SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='ChatThreadParticipant' 
          AND column_name='unreadCount'
    ) > 0,
    "SELECT 0;",
    "alter table ChatThreadParticipant add unreadCount smallint unsigned not null default 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='ChatThreadParticipant' 
          AND column_name='chatThreadName'
    ) > 0,
    "alter table ChatThreadParticipant drop column chatThreadName;",
    "SELECT 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

update ChatThreadParticipant as tp left join (select count(distinct tm.uuid) as unreadMessages, tp.uuid as uuid
from ChatThreadMessage tm
join ChatThread t on t.uuid = tm.chatThreadUUID
join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
where (tp.lastReadAt < tm.sentAt or tp.lastReadAt is null) group by tp.uuid) as c on tp.uuid = c.uuid set tp.unreadCount = c.unreadMessages where c.unreadMessages is not null;