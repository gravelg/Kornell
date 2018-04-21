SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='ChatThread' 
          AND column_name='lastSentAt'
    ) > 0,
    "SELECT 0;",
    "alter table ChatThread add lastSentAt timestamp not null default CURRENT_TIMESTAMP;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


update ChatThread as t left join (select max(ctm.sentAt) as latestMessage, ct.uuid as uuid
from ChatThread ct
left join ChatThreadMessage ctm on ctm.chatThreadUUID = ct.uuid
group by ct.uuid) as c on t.uuid = c.uuid set t.lastSentAt = CASE
    when c.latestMessage is null then now()
    else c.latestMessage
end