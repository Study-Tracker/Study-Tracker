ALTER TABLE storage_drive_folders
    ADD details JSON;

ALTER TABLE storage_drives
    ADD details JSON;


-- Update S3 bucket records
UPDATE storage_drives
SET details = d.data::jsonb
FROM (
         select
             sd.id,
             sd.display_name,
             format('{"bucketId": %s, "bucketName": "%s", "awsIntegrationId": %s, "storageDriveId": %s, "type": "StorageDriveDetails.S3BucketDetails"}',
                 sb.id, sb.name, sb.aws_integration_id, sb.storage_drive_id) as data
         from
             storage_drives sd
                 join s3_buckets sb
                     on sd.id = sb.storage_drive_id
     ) as d
WHERE storage_drives.id = d.id;


-- Update Egnyte drive records
UPDATE storage_drives
SET details = d.data::jsonb
FROM (
         select
             sd.id,
             sd.display_name,
             format('{"egnyteDriveId": %s, "egnyteIntegrationId": %s, "name": "%s", "storageDriveId": %s, "type": "StorageDriveDetails.EgnyteDriveDetails"}',
                 ed.id, ed.egnyte_integration_id, ed.name, ed.storage_drive_id) as data
         from
             storage_drives sd
                 join egnyte_drives ed
                     on sd.id = ed.storage_drive_id
     ) as d
WHERE storage_drives.id = d.id;


-- Update OneDrive drive records
UPDATE storage_drives
SET details = d.data::jsonb
FROM (
         select
             sd.id,
             sd.display_name,
             format('{"onedriveDriveId": %s, "msGraphIntegrationId": %s, "name": "%s", "storageDriveId": %s, "driveId": "%s", "webUrl": "%s", "type": "StorageDriveDetails.OneDriveDriveDetails"}',
                 od.id, od.msgraph_integration_id, od.name, od.storage_drive_id, od.drive_id, od.web_url) as data
         from
             storage_drives sd
                 join onedrive_drives od
                     on sd.id = od.storage_drive_id
     ) as d
WHERE storage_drives.id = d.id;


-- Update local drive records
UPDATE storage_drives
SET details = d.data::jsonb
FROM (
         select
             sd.id,
             sd.display_name,
             format('{"localDriveId": %s, "name": "%s", "organizationId": %s, "type": "StorageDriveDetails.LocalDriveDetails"}',
                 ld.id, ld.name, ld.organization_id) as data
         from
             storage_drives sd
                 join local_drives ld
                     on sd.id = ld.storage_drive_id
     ) as d
WHERE storage_drives.id = d.id;



-- Update S3 bucket folder records
UPDATE storage_drive_folders
SET details = d.data::jsonb
FROM (
         select
             sdf.id,
             sdf.name,
             format(
                     '{"storageDriveFolderId": %s, "s3BucketId": %s, "key": "%s", "eTag": "%s", "type": "StorageDriveFolderDetails.S3FolderDetails"}',
                     sbf.storage_drive_folder_id, sbf.s3_bucket_id, sbf.key, sbf.e_tag
                 ) as data
         from
             storage_drive_folders sdf
                 join s3_bucket_folders sbf on sdf.id = sbf.storage_drive_folder_id
     ) as d
WHERE storage_drive_folders.id = d.id;


-- Update Egnyte folder records
UPDATE storage_drive_folders
SET details = d.data::jsonb
FROM (
         select
             sdf.id,
             sdf.name,
             format(
                     '{"storageDriveFolderId": %s, "egnyteDriveId": %s, "folderId": "%s", "webUrl": "%s", "type": "StorageDriveFolderDetails.EgnyteFolderDetails"}',
                     edf.storage_drive_folder_id, edf.egnyte_drive_id, edf.folder_id, edf.web_url
                 ) as data
         from
             storage_drive_folders sdf
                 join egnyte_drive_folders edf on sdf.id = edf.storage_drive_folder_id
     ) as d
WHERE storage_drive_folders.id = d.id;


-- Update OneDrive folder records
UPDATE storage_drive_folders
SET details = d.data::jsonb
FROM (
         select
             sdf.id,
             sdf.name,
             format(
                     '{"storageDriveFolderId": %s, "onedriveDriveId": %s, "folderId": "%s", "webUrl": "%s", "path": "%s", "type": "StorageDriveFolderDetails.OneDriveFolderDetails"}',
                     odf.storage_drive_folder_id, odf.onedrive_drive_id, odf.folder_id, odf.web_url, odf.path
                 ) as data
         from
             storage_drive_folders sdf
                 join onedrive_folders odf on sdf.id = odf.storage_drive_folder_id
     ) as d
WHERE storage_drive_folders.id = d.id;


-- Update local folder records
UPDATE storage_drive_folders
SET details = d.data::jsonb
FROM (
         select
             sdf.id,
             sdf.name,
             format(
                     '{"storageDriveFolderId": %s, "localDriveId": %s, "type": "StorageDriveFolderDetails.LocalDriveFolderDetails"}',
                     ldf.storage_drive_folder_id, ldf.local_drive_id
                 ) as data
         from
             storage_drive_folders sdf
                 join local_drive_folders ldf on sdf.id = ldf.storage_drive_folder_id
     ) as d
WHERE storage_drive_folders.id = d.id;


ALTER TABLE egnyte_drive_folders
    DROP CONSTRAINT fk_egnyte_drive_folders_on_egnyte_drive;

ALTER TABLE egnyte_drive_folders
    DROP CONSTRAINT fk_egnyte_drive_folders_on_storage_drive_folder;

ALTER TABLE egnyte_drives
    DROP CONSTRAINT fk_egnyte_drives_on_egnyte_integration;

ALTER TABLE egnyte_drives
    DROP CONSTRAINT fk_egnyte_drives_on_storage_drive;

ALTER TABLE file_storage_locations
    DROP CONSTRAINT fk_file_storage_locations_on_integration_instance;

ALTER TABLE file_store_folders
    DROP CONSTRAINT fk_file_store_folders_on_file_storage_location;

ALTER TABLE integration_instances
    DROP CONSTRAINT fk_integration_instances_on_integration_definition;

ALTER TABLE integration_configuration_schema_fields
    DROP CONSTRAINT fk_integrationconfigurationschemafield_on_integrationdefinition;

ALTER TABLE integration_instance_configuration_values
    DROP CONSTRAINT fk_integrationinstanceconfigurationvalue_on_integrationinstance;

ALTER TABLE local_drive_folders
    DROP CONSTRAINT fk_local_drive_folders_on_local_drive;

ALTER TABLE local_drive_folders
    DROP CONSTRAINT fk_local_drive_folders_on_storage_drive_folder;

ALTER TABLE local_drives
    DROP CONSTRAINT fk_local_drives_on_organization;

ALTER TABLE local_drives
    DROP CONSTRAINT fk_local_drives_on_storage_drive;

ALTER TABLE onedrive_drives
    DROP CONSTRAINT fk_onedrive_drives_on_msgraph_integration;

ALTER TABLE onedrive_drives
    DROP CONSTRAINT fk_onedrive_drives_on_storage_drive;

ALTER TABLE onedrive_folders
    DROP CONSTRAINT fk_onedrive_folders_on_onedrive_drive;

ALTER TABLE onedrive_folders
    DROP CONSTRAINT fk_onedrive_folders_on_storage_drive_folder;

ALTER TABLE s3_bucket_folders
    DROP CONSTRAINT fk_s3_bucket_folders_on_s3_bucket;

ALTER TABLE s3_bucket_folders
    DROP CONSTRAINT fk_s3_bucket_folders_on_storage_drive_folder;

ALTER TABLE s3_buckets
    DROP CONSTRAINT fk_s3_buckets_on_aws_integration;

ALTER TABLE s3_buckets
    DROP CONSTRAINT fk_s3_buckets_on_storage_drive;

DROP TABLE egnyte_drive_folders CASCADE;

DROP TABLE egnyte_drives CASCADE;

DROP TABLE file_storage_locations CASCADE;

DROP TABLE file_store_folders CASCADE;

DROP TABLE integration_configuration_schema_fields CASCADE;

DROP TABLE integration_definitions CASCADE;

DROP TABLE integration_instance_configuration_values CASCADE;

DROP TABLE integration_instances CASCADE;

DROP TABLE local_drive_folders CASCADE;

DROP TABLE local_drives CASCADE;

DROP TABLE onedrive_drives CASCADE;

DROP TABLE onedrive_folders CASCADE;

DROP TABLE s3_bucket_folders CASCADE;

DROP TABLE s3_buckets CASCADE;