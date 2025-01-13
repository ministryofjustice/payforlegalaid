apiVersion: apps/v1
kind: Deployment
metadata:
  name: gpfd-dev-deployment
  labels:
    app: gpfd-dev
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gpfd-dev
  template:
    metadata:
      labels:
        app: gpfd-dev
    spec:
      serviceAccountName: laa-get-payments-finance-data-dev-service
      containers:
        - name: gpfd-api-container-dev
          image: ${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}
          ports:
            - containerPort: 8443
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "dev"
            - name: AZURE_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: client-secret
            - name: AZURE_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: client-id
            - name: AZURE_TENANT_ID
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: tenant-id
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
            - name: SSL-DEV-KEY-STORE-PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: ssl-dev-key-store-password
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
          securityContext:
            capabilities:
              drop:
              - ALL
            allowPrivilegeEscalation: false
