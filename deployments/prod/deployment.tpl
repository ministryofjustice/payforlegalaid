apiVersion: apps/v1
kind: Deployment
metadata:
  name: gpfd-prod-deployment
  labels:
    app: gpfd-prod
spec:
  replicas: 1
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
            - containerPort: 8443
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
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
            - name: MOJFIN_prod_READ_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-prod-read-username
            - name: MOJFIN_prod_READ_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-prod-read-password
            - name: SSL-prod-KEY-STORE-PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: ssl-prod-key-store-password
            - name: MOJFIN_prod_WRITE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-prod-write-username
            - name: MOJFIN_prod_WRITE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01
                  key: mojfin-prod-write-password

