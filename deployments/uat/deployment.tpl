apiVersion: apps/v1
kind: Deployment
metadata:
  name: gpfd-uat-deployment
  labels:
    app: gpfd-uat
spec:
  replicas: 2
  selector:
    matchLabels:
      app: gpfd-uat
  template:
    metadata:
      labels:
        app: gpfd-uat
    spec:
      serviceAccountName: laa-get-payments-finance-data-uat-service
      containers:
        - name: gpfd-api-container-uat
          image: ${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "uat"
            - name: AZURE_CLIENT_SECRET_UAT
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: client-secret-uat
            - name: AZURE_CLIENT_ID_UAT
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: client-id-uat
            - name: AZURE_TENANT_ID_UAT
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: tenant-id-uat
            - name: MOJFIN_DEV_READ_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: mojfin-dev-read-username
            - name: MOJFIN_DEV_READ_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: mojfin-dev-read-password
            - name: MOJFIN_DEV_WRITE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: mojfin-dev-write-username
            - name: MOJFIN_DEV_WRITE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-uat-secret-01
                  key: mojfin-dev-write-password
          securityContext:
            capabilities:
              drop:
              - ALL
            runAsNonRoot: true
            allowPrivilegeEscalation: false
            seccompProfile:
              type: RuntimeDefault
