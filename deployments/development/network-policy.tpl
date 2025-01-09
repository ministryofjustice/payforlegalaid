kind: NetworkPolicy
apiVersion: networking.k8s.io/v1
metadata:
  name: allow-prometheus-scraping
  namespace: ${NAMESPACE}
spec:
  podSelector:
    matchLabels:
      app: gpfd-dev-service
  policyTypes:
    - Ingress
  ingress:
    - from:
        - namespaceSelector:
            matchLabels:
              component: monitoring
