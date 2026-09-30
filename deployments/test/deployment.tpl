apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${BRANCH_NAME}-gpfd-dev-deployment
  labels:
    app: ${BRANCH_NAME}-gpfd-test
    branch: ${BRANCH_NAME}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${BRANCH_NAME}-gpfd-test
      branch: ${BRANCH_NAME}
  template:
    metadata:
      labels:
        app: ${BRANCH_NAME}-gpfd-test
        branch: ${BRANCH_NAME}
    spec:
      serviceAccountName: laa-get-payments-finance-data-dev-service
      securityContext:
        fsGroup: 999
      volumes:
        - name: postgres-data
          emptyDir: {}
      containers:
        - name: gpfd-api-container-dev
          image: ${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}
          ports:
            - containerPort: 8080
          env:
            - name: GPFD_URL
              value: ${GPFD_URL}
            - name: SPRING_PROFILES_ACTIVE
              value: "dev,mockauth"
            - name: SPRING_FLYWAY_CONNECT_RETRIES
              value: "30"
            - name: GPFD_SECURITY_MOCK_AUTH_ENABLED
              value: "true"
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
            - name: S3_TEMPLATE_STORE
              value: laa-get-payments-finance-data-dev-file-store
            - name: S3_REPORT_STORE
              value: laa-get-payments-finance-data-dev-report-store
            # Disposable branch Postgres sidecar, not a copy of live dev data, creates schema and report definitions on startup
            - name: RDS_DB_HOST
              value: localhost
            - name: RDS_DB_NAME
              value: glad
            - name: TRACKING_DB_USERNAME
              valueFrom:
                secretKeyRef:
                  name: ${BRANCH_NAME}-gpfd-db-credentials
                  key: username
            - name: TRACKING_DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: ${BRANCH_NAME}-gpfd-db-credentials
                  key: password
          securityContext:
            capabilities:
              drop:
              - ALL
            runAsNonRoot: true
            allowPrivilegeEscalation: false
            seccompProfile:
              type: RuntimeDefault
        - name: postgres
          image: postgres:18
          ports:
            - containerPort: 5432
          env:
            - name: POSTGRES_DB
              value: glad
            - name: POSTGRES_USER
              valueFrom:
                secretKeyRef:
                  name: ${BRANCH_NAME}-gpfd-db-credentials
                  key: username
            - name: POSTGRES_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: ${BRANCH_NAME}-gpfd-db-credentials
                  key: password
          volumeMounts:
            - name: postgres-data
              mountPath: /var/lib/postgresql
          readinessProbe:
            exec:
              command: ["pg_isready", "-h", "localhost", "-p", "5432", "-U", "$(POSTGRES_USER)"]
            initialDelaySeconds: 5
            periodSeconds: 5
          livenessProbe:
            exec:
              command: ["pg_isready", "-h", "localhost", "-p", "5432", "-U", "$(POSTGRES_USER)"]
            initialDelaySeconds: 10
            periodSeconds: 10
          resources:
            requests:
              cpu: 100m
              memory: 256Mi
            limits:
              cpu: 500m
              memory: 512Mi
          securityContext:
            runAsUser: 999
            runAsNonRoot: true
            allowPrivilegeEscalation: false
            capabilities:
              drop:
                - ALL
            seccompProfile:
              type: RuntimeDefault
