apiVersion: v1
kind: Service
metadata:
  name: gpfd-prod-service
spec:
  selector:
    app: gpfd-prod
  ports:
    - name: https
      port: 8443
      targetPort: 8443

