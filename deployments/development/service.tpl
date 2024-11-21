apiVersion: v1
kind: Service
metadata:
  name: gpfd-dev-service
spec:
  selector:
    app: gpfd-dev
  ports:
    - name: https
      port: 8443
      targetPort: 8443

