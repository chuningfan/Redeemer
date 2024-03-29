IF NOT EXISTS(SELECT TOP 1 1 FROM sys.tables t WITH(NOLOCK)
WHERE SCHEMA_NAME(schema_id) = 'dbo' AND OBJECT_NAME(object_id) ='QRTZ_PAUSED_TRIGGER_GRPS' AND type = 'U')
BEGIN
     CREATE TABLE [dbo].[QRTZ_PAUSED_TRIGGER_GRPS] ( 
     [SCHED_NAME]     NVARCHAR(120)         NOT NULL,
     [TRIGGER_GROUP]  NVARCHAR(200)         NOT NULL,
     [CREATED_BY]         NVARCHAR(225)        NULL,
	 [CREATED_DT]         DATETIME                 NULL,
	 [MODIFIED_BY]        NVARCHAR(225)        NULL,
	 [MODIFIED_DT]        DATETIME                 NULL
)
     
END

IF NOT EXISTS(SELECT TOP 1 1 FROM sys.tables t WITH(NOLOCK) 
JOIN sys.indexes i ON t.object_id = i.object_id AND i.is_primary_key = 1 WHERE SCHEMA_NAME(t.schema_id) = 'dbo' AND OBJECT_NAME(t.object_id) ='QRTZ_PAUSED_TRIGGER_GRPS' AND t.type = 'U')
BEGIN
     ALTER TABLE dbo.QRTZ_PAUSED_TRIGGER_GRPS ADD CONSTRAINT [PK__QRTZ_PAU__696155E97CA55A29]  PRIMARY KEY CLUSTERED  ([SCHED_NAME] ASC, [TRIGGER_GROUP] ASC)WITH (PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
     PRINT 'Created primary key PK__QRTZ_PAU__696155E97CA55A29 on table dbo.QRTZ_PAUSED_TRIGGER_GRPS'
END
