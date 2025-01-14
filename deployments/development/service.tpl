apiVersion: v1
kind: Service
metadata:
  name: gpfd-dev-service
  labels:
    app: gpfd-dev-service
spec:
  selector:
    app: gpfd-dev
  ports:
    - name: http
      port: 8080
      targetPort: 8080

