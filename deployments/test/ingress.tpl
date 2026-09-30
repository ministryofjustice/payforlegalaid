apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${BRANCH_NAME}-gpfd-dev-ingress
  annotations:
    external-dns.alpha.kubernetes.io/set-identifier: ${BRANCH_NAME}-gpfd-dev-ingress-${NAMESPACE}-green
    external-dns.alpha.kubernetes.io/aws-weight: "100"
    nginx.ingress.kubernetes.io/backend-protocol: http
    nginx.ingress.kubernetes.io/affinity: "cookie"
    nginx.ingress.kubernetes.io/limit-rps: "10"
    nginx.ingress.kubernetes.io/configuration-snippet: |
      limit_req_status 429;
    nginx.ingress.kubernetes.io/enable-modsecurity: "true"
    nginx.ingress.kubernetes.io/modsecurity-snippet: |
      SecRuleEngine On
    nginx.ingress.kubernetes.io/enable-owasp-core-rules: "true"
    nginx.ingress.kubernetes.io/whitelist-source-range: "${IP_LIST}"
  labels:
    branch: ${BRANCH_NAME}
spec:
  ingressClassName: modsec-non-prod
  tls:
    - hosts:
        - ${BRANCH_HOSTNAME}
  rules:
    - host: ${BRANCH_HOSTNAME}
      http:
        paths:
          - path: /
            pathType: ImplementationSpecific
            backend:
              service:
                name: ${BRANCH_NAME}-gpfd-dev-service
                port:
                  number: 8080
