# Get Legal Aid Data (GLAD) / Get Payments and Finance Data (GPFD)

(Formerly known as 'Pay For Legal Aid' (PFLA))

## About this repository

This is an API that gets financial reports data from the MOJFIN database in the modernisation platform,
and returns it to the user in the form of a CSV or Excel data stream. The current user flow is:

1. A user clicks on a URL hyperlink inside a Sharepoint site
2. This will send a request to the GPFD API with a request parameter
   The five endpoints are:
   /csv/{id} (returns the CSV data stream for the requested report),
   /excel/{id} (returns the Excel data stream for the requested report),
   /reports/ (returns a list of the available reports and some information about them).
   /reports/{id} (returns metadata about the requested report)
   /reports/{id}/file (returns a pre-generated CSV, for specific reports only)
3. The API will authenticate the user against MOJ Microsoft Entra, and if the user has an account
   within the MOJ 'tenant' they will be authenticated and allowed to interact with the API. 
    - The service will redirect the user's browser to Microsoft Entra/Active Directory for them to enter their
      Microsoft account details
    - This authentication is handled by the `spring-cloud-azure-starter-active-directory` library in the POM
    - Some endpoints require further role-based checks.
4. The service makes calls to various tables in the MOJFIN DEV database, to obtain metadata about the financial
   reports and gather the actual reports data on generated reports.
5. A response is sent back to the user, and either displayed in their browser as JSON (metadata endpoints) or downloaded (report data endpoints)

## Database

GPFD currently uses two sets of MOJFIN database user credentials, defined in the application properties:

1. 'read-only', which is used to access reports data through db views created in MOJFIN
2. 'write' which is used to read and write to the custom GPFD tables created in MOJFIN

There are multiple GPFD tables - see the [Database Design page](https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/5481922635/Database+Design)

The GPFD table definitions and data are stored in the [data repository](https://github.com/ministryofjustice/payforlegalaid-data)

There is also an H2 database which some unit tests run against, the schema of which is defined in `test/resources`, as Liquibase changelogs

## Technology Stack

- Java
- Maven
- Spring Boot
    - Spring Security
    - Spring Web
- Docker
- Kubernetes
- GitHub Actions
- Microsoft Entra
- Liquibase
- [OpenAPI](https://github.com/ministryofjustice/payforlegalaid-openapi)

## Architecture

Architectural designs can be found here:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4492689485/Architecture+and+Design

Context around the AWS cloud/kubernetes setup at LAA and the GPFD app in particular can be found here:  
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4428398672/Cloud+Platform+Setup

## Environments
More information can be found in the following confluence page:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4736516940/GPFD+Environments

## Running The App
### Locally
Locally you can install the app and run it
- `mvn clean install`
- `java -jar -Dspring.profiles.active=local target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar`

This makes use of a local h2 database. You can create one via running the acceptance tests.

You will need to populate the `src/main/resources` folder with any template files you need. These are the `.xlsx` files that
hold a skeleton of the report in. E.g. if you want to test the Third Party Report you should place a copy of the Third Party Report template in the folder.
For more details on how to get this template visit the [Confluence](https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/5803409516/How+to+create+a+template#How-do-I-get-a-template-to-use-on-my-local-system)

### Scanning Snyk tools
- `snyk test --policy-path=.snyk`

### Access on development
- There is a shared password-manager group account for the GPFD team, ask the team for details on how to request access. 
This group has a number of test users which can be used to access the Dev environment - these accounts have been registered with the DEV environment's active directory, and so will pass the SSO authentication.

## Tests

There are unit tests which use mocked services and an H2 database. Config for the latter is located in the
test/resources folder.

Acceptance tests are located [here](https://github.com/ministryofjustice/payforlegalaid-tests).

## CI/CD

GitHub Actions is used for CI/CD.

## Azure Active Directory SSO

The code for authenticating via SSO is located in the pom library `spring-cloud-azure-starter-active-directory`,
and the config for this is located in the application config.

More information can be found in the following confluence page:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4500652730/Azure+Active+Directory+SSO+-+Setup

And the official docs:
https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-azure-active-directory-developer-guide?tabs=SpringCloudAzure4x
