# Pay For Legal Aid

[//]: ([![repo standards badge]&#40;https://img.shields.io/badge/dynamic/json?color=blue&style=for-the-badge&logo=github&label=MoJ%20Compliant&query=%24.result&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv1%2Fcompliant_public_repositories%2Fmodernisation-platform-configuration-management&#41;]&#40;https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-github-repositories.html#modernisation-platform-configuration-management "Link to report"&#41;)

## About this repository

This is the repo that contains the code for the Pay For Leg

## Technology Stack

- Java
- Maven
- Springboot
    - Spring Security
    - Spring Web
- Docker
- Kubernetes
- Azure AD




## Future Phases

Future phases may include code to connect to sharepoint, as well as using POJO models for each different MOJFIN report
type (in order to manipulate and create actual .csv files from the csv data stream we are using now). An early draft of 
this code can be found in the following branch:    

LPF-209-sharepoint-branch-3
https://github.com/ministryofjustice/payforlegalaid/commits/LPF-209-sharepoint-branch-3

The code to write to a csv FILE on the local docker container/k8s is commented out in the report service. 
It can be used to get you started, but isn't currently working, probably because of docker container/k8s permissions.
Part of this code is the reportModels POJO classes, which map specific reports to java objects.