apiVersion: apps/v1
kind: Deployment
metadata:
  name: gpfd-prod-deployment
  labels:
    app: gpfd-prod
spec:
  replicas: 2
  selector:
    matchLabels:
      app: gpfd-prod
  template:
    metadata:
      labels:
        app: gpfd-prod
    spec:
      serviceAccountName: laa-get-payments-finance-data-prod-service
      containers:
        - name: gpfd-api-container-prod
          image: ${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: AZURE_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: client-secret-prod
            - name: AZURE_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: client-id-prod
            - name: AZURE_TENANT_ID
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: tenant-id-prod
            - name: MOJFIN_DEV_READ_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: mojfin-dev-read-username
            - name: MOJFIN_DEV_READ_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: mojfin-dev-read-password
            - name: MOJFIN_DEV_WRITE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: mojfin-dev-write-username
            - name: MOJFIN_DEV_WRITE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: mojfin-dev-write-password
            - name: MOJFIN_DB_URL
              valueFrom:
                secretKeyRef:
                  name: gpfd-prod-secret-01
                  key: mojfin-db-url
          securityContext:
            capabilities:
              drop:
              - ALL
            runAsNonRoot: true
            allowPrivilegeEscalation: false
            seccompProfile:
              type: RuntimeDefault
