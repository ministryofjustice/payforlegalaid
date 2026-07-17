# Changelog

## [2.0.1](https://github.com/ministryofjustice/payforlegalaid/compare/v2.0.0...v2.0.1) (2026-07-17)


### Bug Fixes

* **LPF-000:** Bump actions/checkout from 3 to 7 ([#563](https://github.com/ministryofjustice/payforlegalaid/issues/563)) ([3dbebaf](https://github.com/ministryofjustice/payforlegalaid/commit/3dbebafc1121650b9d377c1f11784ac818158e3a))

## [2.0.0](https://github.com/ministryofjustice/payforlegalaid/compare/v1.2.0...v2.0.0) (2026-07-16)


### ⚠ BREAKING CHANGES

* **LPF-1514:** Implement glad SBOM ([#552](https://github.com/ministryofjustice/payforlegalaid/issues/552))

### Build System

* **LPF-1514:** Implement glad SBOM ([#552](https://github.com/ministryofjustice/payforlegalaid/issues/552)) ([968cd84](https://github.com/ministryofjustice/payforlegalaid/commit/968cd84a6fe5f72f3844dc2aaeecc295b6a6263b))

## [1.2.0](https://github.com/ministryofjustice/payforlegalaid/compare/v1.1.1...v1.2.0) (2026-07-15)


### Features

* **LPF-1530:** Update to include REP002 ([#554](https://github.com/ministryofjustice/payforlegalaid/issues/554)) ([43c98b2](https://github.com/ministryofjustice/payforlegalaid/commit/43c98b2607b60cae142b1a03540f406e8ac0b572))

## [1.1.1](https://github.com/ministryofjustice/payforlegalaid/compare/v1.1.0...v1.1.1) (2026-07-02)


### Bug Fixes

* **LPF-1368:** Fixed test config issues (PFLA tests) ([#531](https://github.com/ministryofjustice/payforlegalaid/issues/531)) ([4a10c09](https://github.com/ministryofjustice/payforlegalaid/commit/4a10c09f2f7def5f19a0206c05ef9948f20b547c))

## [1.1.0](https://github.com/ministryofjustice/payforlegalaid/compare/v1.0.1...v1.1.0) (2026-06-19)


### Features

* **LPF-1453:** add metrics for mojfin connection ([#520](https://github.com/ministryofjustice/payforlegalaid/issues/520)) ([a3f91b2](https://github.com/ministryofjustice/payforlegalaid/commit/a3f91b280c0a71556951771f1b1a1f7646a40391))


### Bug Fixes

* **LPF-0000:** Fixed CSRF vulnerability ([#521](https://github.com/ministryofjustice/payforlegalaid/issues/521)) ([3dd581c](https://github.com/ministryofjustice/payforlegalaid/commit/3dd581c19dec9fb9f17ad434f81b738f31956cec))

## [1.0.1](https://github.com/ministryofjustice/payforlegalaid/compare/v1.0.0...v1.0.1) (2026-05-29)


### Bug Fixes

* fix dockerfile versioning ([#501](https://github.com/ministryofjustice/payforlegalaid/issues/501)) ([74daf11](https://github.com/ministryofjustice/payforlegalaid/commit/74daf11f2132ecf076adf833d6401ba738253a49))

## 1.0.0 (2026-05-29)


### Features

* Add structured logging ([#489](https://github.com/ministryofjustice/payforlegalaid/issues/489)) ([0cbd4e5](https://github.com/ministryofjustice/payforlegalaid/commit/0cbd4e56db96443d99ce9f4e155eb304a2d0f866))
* **LPF-1122:** Implement thanos ([#487](https://github.com/ministryofjustice/payforlegalaid/issues/487)) ([a6dea82](https://github.com/ministryofjustice/payforlegalaid/commit/a6dea8239d3d03d99db60aee660be8eb6b34446a))
* **LPF-1365:** Implement API pattern target configuration for CSRF Protection ([#474](https://github.com/ministryofjustice/payforlegalaid/issues/474)) ([d9ccbd9](https://github.com/ministryofjustice/payforlegalaid/commit/d9ccbd9a952162627c368dcb1aa1289a71e441e3))
* **LPF-1378:** implement release version tagging automation ([#478](https://github.com/ministryofjustice/payforlegalaid/issues/478)) ([914a314](https://github.com/ministryofjustice/payforlegalaid/commit/914a314c04c076f9968e7b998b13f1c514c404cd))


### Bug Fixes

* bump version of tomcat embed core to version without high vulnerability ([#260](https://github.com/ministryofjustice/payforlegalaid/issues/260)) ([8ed47f2](https://github.com/ministryofjustice/payforlegalaid/commit/8ed47f263e07c658f8cafb755fe5eecd9f3121aa))
* **LPF-000:** Fix workflows jar version ([#495](https://github.com/ministryofjustice/payforlegalaid/issues/495)) ([0aeecdf](https://github.com/ministryofjustice/payforlegalaid/commit/0aeecdf50fd6e06d77e8f7c4cf4d545f5284a2e5))
* **LPF-1378:** Removed pre-commit hook for commit enforcement ([#491](https://github.com/ministryofjustice/payforlegalaid/issues/491)) ([f5a7987](https://github.com/ministryofjustice/payforlegalaid/commit/f5a7987185395c58bb9e037ce092964b8300c5f3))
* Oracle health check in docker compose stops app running too early ([#486](https://github.com/ministryofjustice/payforlegalaid/issues/486)) ([2758577](https://github.com/ministryofjustice/payforlegalaid/commit/27585771856c34f4fd38ccc9b132be7ebd049c23))
* pom.xml to reduce vulnerabilities ([#199](https://github.com/ministryofjustice/payforlegalaid/issues/199)) ([f60beb6](https://github.com/ministryofjustice/payforlegalaid/commit/f60beb623c4891ba053a45f35fb93943656dd626))
* upgrade com.h2database:h2 from 2.2.224 to 2.3.232 ([#112](https://github.com/ministryofjustice/payforlegalaid/issues/112)) ([79912dd](https://github.com/ministryofjustice/payforlegalaid/commit/79912dd09653a710b407e7a221016c7475af1a43))
* upgrade com.microsoft.graph:microsoft-graph from 5.79.0 to 5.80.0 ([#115](https://github.com/ministryofjustice/payforlegalaid/issues/115)) ([62f5421](https://github.com/ministryofjustice/payforlegalaid/commit/62f5421485c9ef7d73139daa7a3f50abeff56ff8))
* upgrade com.oracle.database.jdbc:ojdbc10 from 19.22.0.0 to 19.25.0.0 ([#98](https://github.com/ministryofjustice/payforlegalaid/issues/98)) ([312172c](https://github.com/ministryofjustice/payforlegalaid/commit/312172cf021ba086e3e0f322d0a5587a7014fdc5))
* upgrade com.oracle.database.jdbc:ojdbc11 from 23.8.0.25.04 to 23.9.0.25.07 ([#286](https://github.com/ministryofjustice/payforlegalaid/issues/286)) ([9a92039](https://github.com/ministryofjustice/payforlegalaid/commit/9a9203986438d6e4e2267fa20f59c5a3d840122c))
* upgrade commons-io:commons-io from 2.16.0 to 2.18.0 ([#127](https://github.com/ministryofjustice/payforlegalaid/issues/127)) ([de8d445](https://github.com/ministryofjustice/payforlegalaid/commit/de8d445343408b8bcac61625c8a11cca63b6fddb))
* upgrade io.swagger.core.v3:swagger-annotations from 2.2.10 to 2.2.27 ([#95](https://github.com/ministryofjustice/payforlegalaid/issues/95)) ([e07b7d5](https://github.com/ministryofjustice/payforlegalaid/commit/e07b7d575e0193fbe1742afe56679fc30e8d765d))
* upgrade org.apache.commons:commons-csv from 1.10.0 to 1.12.0 ([#113](https://github.com/ministryofjustice/payforlegalaid/issues/113)) ([3cabaa6](https://github.com/ministryofjustice/payforlegalaid/commit/3cabaa6db736b041458aa5d8a248b7e8f3fb2d0f))
* upgrade org.apache.commons:commons-csv from 1.12.0 to 1.13.0 ([#139](https://github.com/ministryofjustice/payforlegalaid/issues/139)) ([ee5b127](https://github.com/ministryofjustice/payforlegalaid/commit/ee5b127f4187508c1fdabb010871f3686f9b15a8))
* upgrade org.apache.poi:poi from 5.4.0 to 5.4.1 ([#200](https://github.com/ministryofjustice/payforlegalaid/issues/200)) ([7b2e585](https://github.com/ministryofjustice/payforlegalaid/commit/7b2e585b4fe5e4654269811876b8aedafe308ae3))
* upgrade org.immutables:value from 2.11.0 to 2.11.1 ([#282](https://github.com/ministryofjustice/payforlegalaid/issues/282)) ([5361db6](https://github.com/ministryofjustice/payforlegalaid/commit/5361db615dbafdebc7157855e48c5716c043767f))
* upgrade org.modelmapper:modelmapper from 3.1.1 to 3.2.2 ([#111](https://github.com/ministryofjustice/payforlegalaid/issues/111)) ([72c15f5](https://github.com/ministryofjustice/payforlegalaid/commit/72c15f52b2d9129f9afa3cd9337db8ed7fea0f5b))
* upgrade org.slf4j:slf4j-api from 2.0.16 to 2.0.17 ([#174](https://github.com/ministryofjustice/payforlegalaid/issues/174)) ([4c3b18c](https://github.com/ministryofjustice/payforlegalaid/commit/4c3b18cb8b5f2997fb214286424ee8597433ebfa))
* upgrade org.slf4j:slf4j-api from 2.0.9 to 2.0.16 ([#97](https://github.com/ministryofjustice/payforlegalaid/issues/97)) ([0841079](https://github.com/ministryofjustice/payforlegalaid/commit/0841079c55d41c72ede3dc50791cce7ee262a248))
* upgrade org.springdoc:springdoc-openapi-starter-webmvc-ui from 2.8.6 to 2.8.8 ([#261](https://github.com/ministryofjustice/payforlegalaid/issues/261)) ([b916e85](https://github.com/ministryofjustice/payforlegalaid/commit/b916e8514201d4536eb2046882e349e7721d6dba))
* upgrade org.springdoc:springdoc-openapi-starter-webmvc-ui from 2.8.8 to 2.8.9 ([#264](https://github.com/ministryofjustice/payforlegalaid/issues/264)) ([a8e56ce](https://github.com/ministryofjustice/payforlegalaid/commit/a8e56ce447eccacceeb015b6eab86d15acedad5a))
* upgrade org.yaml:snakeyaml from 2.2 to 2.3 ([#114](https://github.com/ministryofjustice/payforlegalaid/issues/114)) ([c891b82](https://github.com/ministryofjustice/payforlegalaid/commit/c891b824ca17fdf7c7fa5eb4d705bf79366cfb22))
* upgrade org.yaml:snakeyaml from 2.3 to 2.4 ([#166](https://github.com/ministryofjustice/payforlegalaid/issues/166)) ([f74fb4d](https://github.com/ministryofjustice/payforlegalaid/commit/f74fb4d60d795625766fc5b2f96d9dd48dd26f22))
