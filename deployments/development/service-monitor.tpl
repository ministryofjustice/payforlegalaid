apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: gpfd-dev-service-monitor
  namespace: ${NAMESPACE}
spec:
  selector:
    matchLabels:
      app: gpfd-dev-service # this needs to match the label in the service under metadata:labels:app
  namespaceSelector:
    matchNames:
      - ${NAMESPACE}
  endpoints:
    - port: http # this is the port name you grabbed from your running service
      interval: 15s
      path: /actuator/metrics # this is the endpoint exposed by springboot app
