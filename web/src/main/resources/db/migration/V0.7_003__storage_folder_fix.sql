insert into study_storage_folders (storage_folder_id, study_id)
select s.storage_folder_id, s.id
from studies s
where s.storage_folder_id not in (select ssf.storage_folder_id from study_storage_folders ssf);

insert into assay_storage_folders (storage_folder_id, assay_id)
select a.storage_folder_id, a.id
from assays a
where a.storage_folder_id not in (select asf.storage_folder_id from assay_storage_folders asf);