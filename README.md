# Get Payments and Finance Data

(Formerly known as 'Pay For Legal Aid')

## About this repository

This is an API that gets financial reports data from the MOJFIN database in the modernisation platform,
and returns it to the user in the form of a CSV data stream. There is, at the time of writing, a DEV environment and a
skeleton UAT env. There is a UI planned for future phases, but in the current MVP state the user flow is:

1. A user clicks on a URL hyperlink inside a sharepoint site, e.g. in
   DEV: https://mojodevl.sharepoint.com/sites/FinanceSysReference-DEV (you will need a DEV active directory user/account
   to authenticate into this site.)
2. This will send a request to the GPFD API with a request parameter,
   e.g. : https://laa-get-payments-finance-data-dev.apps.live.cloud-platform.service.justice.gov.uk/csv/1
   The three endpoints are:
   /csv/{id} (returns the csv data stream for the requested report),
   /report/{id} (returns metadata about the requested report)
   /reports/{id} (returns a list of the available reports and some information about them)
3. The API will authenticate the user against MOJ Active Directory/Microsoft Entra, and if the user has an account
   within that MOJ 'tenant' they will be authenticated and allowed to interact with the API. Role based access is
   yet to be added, but the panda (security) team has advised that it should be in place before the service gets
   into the PROD environment
    - The service will redirect the user's browser to microsoft Entra/Active Directory for them to enter their
      microsoft
      account details (needs to be a DEV A.D. account since the service is currently only in DEV), but if they have
      already authenticated in M.S. SSO within their browser then they will not see a log-in screen, they will just be
      given access to the service.
    - This authentication is handled by the spring-cloud-azure-starter-active-directory library in the POM, and the
      azure AD config in the application-dev.yml

4. The service makes calls to various tables in the MOJFIN DEV database, to obtain metadata about the financial
   reports, gather the actual reports data, and to insert a record into the tracking table every time a report is
   requested
5. A response is sent back to the user, and displayed in their browser (either reports data or metadata, depending on
   the endpoint called)

## Database

GPFD currently uses two sets of MOJFIN database user credentials, defined in the application-dev.yml:

1. 'read-only', which is used to access reports data through db views created in MOJFIN
2. 'write' which is used to read and write to the custom GPFD tables created in the DEV MOJFIN

There are two custom GPFD tables:

1. A table for tracking reports
   Every time a report is requested by a user, their name is logged, along with information about the report, for
   data transparency/GDPR purposes.

2. A table for mapping SQL queries to the CSV reports
   This table contains metadata about each CSV report available for download through the GPFD service, such as the
   report name, description, owner, excel report it belongs to, and the SQL string used to download the report. GPFD
   takes this SQL (a simple select statement in the format: 'SELECT * FROM [db_username].[selected_report]') and then
   runs it to fetch data from the relevant db view in MOJFIN

A guide on how to gain access to the MOJFIN database, which GPFD uses, can be found here:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4495507630/Connecting+to+MOJFIN+DB

- There is also a H2 database which some unit tests run against, the schema of which is defined in
  test/resources/schema.sql, and some data from data.sql is used to populate it.
- There are resources/DDL_[...].sql files which can be used to create a GPFD user and the relevant GPFD custom tables,
  when deploying to any new future environments.

## Technology Stack

- Java
- Maven
- Springboot
    - Spring Security
    - Spring Web
- Docker
- Kubernetes
- GitHub Actions
- Azure Active Directory/Microsoft Entra

## Architecture

Architectural designs can be found here:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4492689485/Architecture+and+Design

Context around the AWS cloud/kubernetes setup at LAA and the GPFD app in particular can be found here:  
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4428398672/Cloud+Platform+Setup

## Environments

Currently, there is a working DEV environment, a skeleton/in-progress UAT environment, but no PROD environment.
More information can be found in the following confluence page:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4736516940/GPFD+Environments

## Running The App

- `mvn clean install`

- `java -jar -Dspring.profiles.active=local target/pay-for-legal-aid-0.0.1-SNAPSHOT.jar`

- This application is in an MVP state and currently does not have a local run setup, but some of the unit tests run
  against an H2 database, which could be used to create a local run configuration.

- There is currently a gpfd-ssl-keystore.p12 and custom ssl key used in the application (despite cloud platform
  providing its own TLS protection on the platform level), because a https url is required by the Active Directory
  server /app registration. A cloud platform-managed TLS cert should be used in the test environments in future, and there is a task in the
  backlog for this. It is mandatory in PROD.

- There is a shared password-manager group account for the GPFD team, ask the manager of the team for access. This group has a number of test users which can be used to access the Dev environment - these accounts have been registered with the DEV environment's active directory, and so will pass the SSO authentication.

## Tests

There are unit tests which use mocked services and an H2 database. Config for the latter is located in the
test/resources folder.

## CI/CD

GitHub actions is used for CICD, with two jobs, one which runs the tests on a push to any branch, and another which
runs the tests AND deploys the code to the DEV environment, when a PR is merged into the main branch.

## Azure Active Directory SSO

The code for authenticating via SSO is located in the pom library "spring-cloud-azure-starter-active-directory",
and the config for this is located in the application-dev.yml

More information can be found in the following confluence page:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4500652730/Azure+Active+Directory+SSO+-+Setup

And the official docs:
https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-azure-active-directory-developer-guide?tabs=SpringCloudAzure4x

## Future Phases

Future phases may include a connection to file storage of CSVs or Excel files sharepoint, as well as using POJO models
for each different MOJFIN report-type (in order to manipulate and create actual .csv files from the csv data stream we
are using now). An early draft of
this code can be found in the following branch:

LPF-209-sharepoint-branch-3
https://github.com/ministryofjustice/payforlegalaid/commits/LPF-209-sharepoint-branch-3

The code to write to a csv FILE on the local docker container/k8s is commented out in the report service on this branch.
It can be used to get you started, but isn't currently working, probably because of docker container/k8s permissions.
Part of this code is the reportModels POJO classes, which map specific reports to java objects.
