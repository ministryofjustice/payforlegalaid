
INSERT INTO GPFD.REPORT_OUTPUT_TYPES(ID,EXTENSION,DESCRIPTION)
VALUES
    ('6ebd27ac-4d83-485d-a4fd-3e45f9a53484', 'csv', 'Comma Separated Text'),
    ('bd098666-94e4-4b0e-822c-8e5dfb04c908','xlsx','Excel Document')
;

INSERT INTO GPFD.REPORTS(ID,NAME,TEMPLATE_SECURE_DOCUMENT_ID,REPORT_CREATION_DATE,DESCRIPTION,NUM_DAYS_TO_KEEP,FILE_NAME,ACTIVE,REPORT_OUTPUT_TYPE,REPORT_OWNER_ID,REPORT_OWNER_NAME,REPORT_OWNER_EMAIL)
VALUES
    ('b36f9bbb-1178-432c-8f99-8090e285f2d3',
     'CCMS Invoice Analysis (CIS to CCMS)',
     '00000000-0000-0000-0000-000000000000',
     '2025-02-15 00:00:00',
     'Summary of invoices in CIS and CCMS by original source IT system',
     30,
     'CCMS_invoice_analysis',
     'Y',
     'bd098666-94e4-4b0e-822c-8e5dfb04c908',
     '00000000-0000-0000-0000-000000000001',
     'Chancey Mctavish',
     'owneremail@email.com'
    ),
    (
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1',
        'CCMS Invoice Analysis (CIS to CCMS)',
        '00000000-0000-0000-0000-000000000000',
        '2025-02-15 00:00:00',
        'Summary of invoices in CIS and CCMS by original source IT system',
        30,
        'CCMS_invoice_analysis',
        'Y',
        '6ebd27ac-4d83-485d-a4fd-3e45f9a53484',
        '00000000-0000-0000-0000-000000000001',
        'Chancey Mctavish',
        'owneremail@email.com'
    ),
    (
        'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5',
        'CIS to CCMS payment value Not Defined',
        '00000000-0000-0000-0000-000000000000',
        '2025-02-15 00:00:00',
        'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme',
        30,
        'CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED',
        'Y',
        '6ebd27ac-4d83-485d-a4fd-3e45f9a53484',
        '00000000-0000-0000-0000-000000000003',
        'William Moran',
        'William.Moran@Justice.gov.uk'
    ),
    (
        '0d4da9ec-b0b3-4371-af10-f375330d85d3',
        'CIS to CCMS payment value Defined',
        '00000000-0000-0000-0000-000000000000',
        '2025-02-15 00:00:00',
        'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme',
        30,
        'CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED',
        'Y',
        '6ebd27ac-4d83-485d-a4fd-3e45f9a53484',
        '00000000-0000-0000-0000-000000000003',
        'William Moran',
        'William.Moran@Justice.gov.uk'
    )
;

INSERT INTO GPFD.REPORTS_TRACKING
      (ID, NAME, REPORT_ID, CREATION_DATE, REPORT_DOWNLOADED_BY, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL)
      VALUES (
              '00000000-0000-0000-0001-000000000001',
              'Test Report Name',
              'b36f9bbb-1178-432c-8f99-8090e285f2d3',
              '2025-02-15 00:00:00',
              '00000000-0000-0000-0003-000000000001',
              '00000000-0000-0000-0004-000000000001',
              '00000000-0000-0000-0005-000000000001',
              '00000000-0000-0000-0006-000000000001',
              'test template URL',
              'test report file URL'
             );