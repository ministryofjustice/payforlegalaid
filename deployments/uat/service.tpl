apiVersion: v1
kind: Service
metadata:
  name: gpfd-uat-service
  labels:
    app: gpfd-uat-service
spec:
  selector:
    app: gpfd-uat
  ports:
    - name: http
      port: 8080
      targetPort: 8080



  # This is a generic file that creates a Kubernetes service. A service exposes the network to pods from a Kubernetes
  # deployment. In this case, an ingress provides external access to our application,
  # which points to the service defined in this file, which points to pods in a deployment.
