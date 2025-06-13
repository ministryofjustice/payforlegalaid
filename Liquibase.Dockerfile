FROM liquibase/liquibase:4.29.2
COPY liquibase-changeset/ /liquibase/
# TODO add filtering to reduce files being checked out to only xmls etc.