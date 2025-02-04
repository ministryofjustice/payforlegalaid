kind: NetworkPolicy
apiVersion: networking.k8s.io/v1
metadata:
  name: allow-prometheus-scraping
  namespace: ${NAMESPACE}
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          component: monitoring
