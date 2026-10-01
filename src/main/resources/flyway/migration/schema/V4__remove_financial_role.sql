-- Remove the retired Financial role after its report mappings have been removed.
DELETE FROM glad.report_roles
WHERE role_id = 3;

DELETE FROM glad.roles
WHERE role_id = 3;