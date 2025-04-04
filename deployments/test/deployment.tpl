apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${BRANCH_NAME}-gpfd-dev-deployment
  labels:
    app: gpfd-dev
    branch: ${BRANCH_NAME}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gpfd-dev
  template:
    metadata:
      labels:
        app: gpfd-dev
        branch: ${BRANCH_NAME}
    spec:
      serviceAccountName: laa-get-payments-finance-data-dev-service
      containers:
        - name: gpfd-api-container-dev
          image: ${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}
          ports:
            - containerPort: 8080
          env:
            - name: GPFD_URL
              value: ${GPFD_URL}
            - name: SPRING_PROFILES_ACTIVE
              value: "dev"
            - name: AZURE_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: client-secret-dev
            - name: AZURE_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: client-id-dev
            - name: AZURE_TENANT_ID
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: tenant-id-dev
            - name: MOJFIN_DEV_READ_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-dev-read-username
            - name: MOJFIN_DEV_READ_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-dev-read-password
            - name: MOJFIN_DEV_WRITE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-dev-write-username
            - name: MOJFIN_DEV_WRITE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-dev-write-password
            - name: MOJFIN_DB_URL
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-db-url
          securityContext:
            capabilities:
              drop:
              - ALL
            runAsNonRoot: true
            allowPrivilegeEscalation: false
            seccompProfile:
              type: RuntimeDefault
