apiVersion: apps/v1
kind: Deployment
metadata:
  name: gpfd-dev-deployment
  labels:
    app: gpfd-dev
spec:
  replicas: 2
  selector:
    matchLabels:
      app: gpfd-dev
  template:
    metadata:
      labels:
        app: gpfd-dev
    spec:
      serviceAccountName: example-name #This relates to the IRSA module in our namespace, within the cloud-platform-environments repo
      #      initContainers:
      #        - name: create-directory
      #          image: busybox
      #          command: ["sh", "-c", "mkdir -p /app/csv-files"]
      #          volumeMounts:
      #            - name: csv-storage
      #              mountPath: /app/csv-files
      containers:
        - name: gpfd-api-container-dev
          image: ${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}
          #          volumeMounts:
          #            - name: csv-storage
          #              mountPath: /app/csv-files
          ports:
            - containerPort: 8443
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "dev"                  #TODO - use a configmap once more environments are added
            - name: AZURE_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: gpfd-test-secret-01 #todo - change this to a more descriptive name in a cloud platform PR
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
#      volumes:
#        - name: csv-storage
#          emptyDir: {} #Using ephemeral storage which is shared between pods and is deleted when a pod is shut down


# This is a generic file that creates a Kubernetes deployment in our namespace. A deployment is a collection of pods
# that runs one or more containers. In this application, currently only one container is run in the pod.