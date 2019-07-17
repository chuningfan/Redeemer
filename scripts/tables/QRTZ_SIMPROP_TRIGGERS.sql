IF NOT EXISTS(SELECT TOP 1 1 FROM sys.tables t WITH(NOLOCK)
WHERE SCHEMA_NAME(schema_id) = 'dbo' AND OBJECT_NAME(object_id) ='QRTZ_SIMPROP_TRIGGERS' AND type = 'U')
BEGIN
     CREATE TABLE [dbo].[QRTZ_SIMPROP_TRIGGERS] ( 
     [SCHED_NAME]     NVARCHAR(120)        NOT NULL,
     [TRIGGER_NAME]   NVARCHAR(200)        NOT NULL,
     [TRIGGER_GROUP]  NVARCHAR(200)        NOT NULL,
     [STR_PROP_1]     NVARCHAR(512)        NULL,
     [STR_PROP_2]     NVARCHAR(512)        NULL,
     [STR_PROP_3]     NVARCHAR(512)        NULL,
     [INT_PROP_1]     INT                 NULL,
     [INT_PROP_2]     INT                 NULL,
     [LONG_PROP_1]    BIGINT              NULL,
     [LONG_PROP_2]    BIGINT              NULL,
     [DEC_PROP_1]     NUMERIC(13,4)       NULL,
     [DEC_PROP_2]     NUMERIC(13,4)       NULL,
     [BOOL_PROP_1]    BIT                 NULL,
     [BOOL_PROP_2]    BIT                 NULL,
     [TIME_ZONE_ID]   NVARCHAR(80)         NULL,
     [CREATED_BY]         NVARCHAR(225)        NULL,
	 [CREATED_DT]         DATETIME                 NULL,
	 [MODIFIED_BY]        NVARCHAR(225)        NULL,
	 [MODIFIED_DT]        DATETIME                 NULL
)
    
END

IF NOT EXISTS(SELECT TOP 1 1 FROM sys.tables t WITH(NOLOCK) 
JOIN sys.indexes i ON t.object_id = i.object_id AND i.is_primary_key = 1 WHERE SCHEMA_NAME(t.schema_id) = 'dbo' AND OBJECT_NAME(t.object_id) ='QRTZ_SIMPROP_TRIGGERS' AND t.type = 'U')
BEGIN
     ALTER TABLE dbo.QRTZ_SIMPROP_TRIGGERS ADD CONSTRAINT [PK__QRTZ_SIM__20F4F1016BDB5416]  PRIMARY KEY CLUSTERED  ([SCHED_NAME] ASC, [TRIGGER_NAME] ASC, [TRIGGER_GROUP] ASC)WITH (PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
END