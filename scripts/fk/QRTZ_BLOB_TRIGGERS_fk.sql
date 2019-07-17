IF NOT EXISTS(SELECT TOP 1 1
				FROM sys.foreign_key_columns fk WITH(NOLOCK)
				JOIN sys.tables t WITH(NOLOCK) ON fk.parent_object_id = t.object_id 
				JOIN sys.columns c WITH(NOLOCK) ON fk.parent_object_id = c.object_id AND fk.parent_column_id = c.column_id
				JOIN sys.tables t2 WITH(NOLOCK) ON fk.referenced_object_id = t2.object_id AND SCHEMA_NAME(t2.schema_id) ='dbo' AND OBJECT_NAME(t2.object_id) ='QRTZ_TRIGGERS' AND t2.[type] = 'U' 
WHERE SCHEMA_NAME(t.schema_id) = 'dbo' AND OBJECT_NAME(t.object_id) ='QRTZ_BLOB_TRIGGERS' AND t.[type] = 'U') 
BEGIN
	 ALTER TABLE [dbo].[QRTZ_BLOB_TRIGGERS] ADD CONSTRAINT [FK__QRTZ_BLOB_TRIGGE__4D7EDF75] FOREIGN KEY ([SCHED_NAME], [TRIGGER_NAME], [TRIGGER_GROUP]) REFERENCES [dbo].[QRTZ_TRIGGERS]([SCHED_NAME], [TRIGGER_NAME], [TRIGGER_GROUP])
END