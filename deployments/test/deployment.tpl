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
            - name: GPFD_SECURITY_MOCK_AUTH_ENABLED
              value: "true"
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
          securityContext:
            capabilities:
              drop:
              - ALL
            runAsNonRoot: true
            allowPrivilegeEscalation: false
            seccompProfile:
              type: RuntimeDefault
