apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: gpfd-prod-service-monitor
  namespace: ${NAMESPACE}
spec:
  selector:
    matchLabels:
      app: gpfd-prod-service # this needs to match the label in the service under metadata:labels:app
  endpoints:
    - port: http # this is the port name you grabbed from your running service
      interval: 15s
      path: /actuator/prometheus # this is the endpoint exposed by springboot app
