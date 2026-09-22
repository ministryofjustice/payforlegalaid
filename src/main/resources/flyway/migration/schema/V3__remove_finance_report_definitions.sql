-- Remove finance report definitions retired with MOJFIN.

DELETE FROM glad.field_attributes
WHERE report_query_id IN (
    SELECT id
    FROM glad.report_queries
    WHERE report_id IN (
        'b36f9bbb-1178-432c-8f99-8090e285f2d3',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf3',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf4',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf6',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf7',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf8',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf9',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbfa',
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbfb',
        'f46b4d3d-c100-429a-bf9a-223305dbdbfb',
        'eee30b23-2c8d-4b4b-bb11-8cd67d07915c',
        'a017241a-359f-4fdb-a0cd-7f28f1946ef1',
        '7073dd13-e325-4863-a05c-a049a815d1f7',
        'e6823193-f5b0-451b-8965-e4d4914980da',
        '56328b13-254d-435d-813a-5863f94b996d',
        '7bda9aa4-6129-4c71-bd12-7d4e46fdd882',
        '516cdbff-5fa8-4050-b5e6-7edf71daf679',
        '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba',
        '90af8289-2c07-4b65-8f37-6b4659920207'
    )
);

DELETE FROM glad.report_queries
WHERE report_id IN (
    'b36f9bbb-1178-432c-8f99-8090e285f2d3',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf3',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf4',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf6',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf7',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf8',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf9',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbfa',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbfb',
    'f46b4d3d-c100-429a-bf9a-223305dbdbfb',
    'eee30b23-2c8d-4b4b-bb11-8cd67d07915c',
    'a017241a-359f-4fdb-a0cd-7f28f1946ef1',
    '7073dd13-e325-4863-a05c-a049a815d1f7',
    'e6823193-f5b0-451b-8965-e4d4914980da',
    '56328b13-254d-435d-813a-5863f94b996d',
    '7bda9aa4-6129-4c71-bd12-7d4e46fdd882',
    '516cdbff-5fa8-4050-b5e6-7edf71daf679',
    '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba',
    '90af8289-2c07-4b65-8f37-6b4659920207'
);

DELETE FROM glad.report_roles
WHERE report_id IN (
    'b36f9bbb-1178-432c-8f99-8090e285f2d3',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf3',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf4',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf6',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf7',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf8',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf9',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbfa',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbfb',
    'f46b4d3d-c100-429a-bf9a-223305dbdbfb',
    'eee30b23-2c8d-4b4b-bb11-8cd67d07915c',
    'a017241a-359f-4fdb-a0cd-7f28f1946ef1',
    '7073dd13-e325-4863-a05c-a049a815d1f7',
    'e6823193-f5b0-451b-8965-e4d4914980da',
    '56328b13-254d-435d-813a-5863f94b996d',
    '7bda9aa4-6129-4c71-bd12-7d4e46fdd882',
    '516cdbff-5fa8-4050-b5e6-7edf71daf679',
    '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba',
    '90af8289-2c07-4b65-8f37-6b4659920207'
);

DELETE FROM glad.reports
WHERE id IN (
    'b36f9bbb-1178-432c-8f99-8090e285f2d3',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf3',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf4',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf6',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf7',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf8',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbf9',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbfa',
    'f46b4d3d-c100-429a-bf9a-6c3305dbdbfb',
    'f46b4d3d-c100-429a-bf9a-223305dbdbfb',
    'eee30b23-2c8d-4b4b-bb11-8cd67d07915c',
    'a017241a-359f-4fdb-a0cd-7f28f1946ef1',
    '7073dd13-e325-4863-a05c-a049a815d1f7',
    'e6823193-f5b0-451b-8965-e4d4914980da',
    '56328b13-254d-435d-813a-5863f94b996d',
    '7bda9aa4-6129-4c71-bd12-7d4e46fdd882',
    '516cdbff-5fa8-4050-b5e6-7edf71daf679',
    '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba',
    '90af8289-2c07-4b65-8f37-6b4659920207'
);
