apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${BRANCH_NAME}-gpfd-dev-ingress
  annotations:
    external-dns.alpha.kubernetes.io/set-identifier: ${BRANCH_NAME}-gpfd-dev-ingress-${NAMESPACE}-green
    external-dns.alpha.kubernetes.io/aws-weight: "100"
    nginx.ingress.kubernetes.io/backend-protocol: http
    nginx.ingress.kubernetes.io/affinity: "cookie"
  labels:
    branch: ${BRANCH_NAME}
spec:
  ingressClassName: default
  tls:
    - hosts:
        - test-laa-get-payments-finance-data.cloud-platform.service.justice.gov.uk
  rules:
    - host: test-laa-get-payments-finance-data.cloud-platform.service.justice.gov.uk
      http:
        paths:
          - path: /
            pathType: ImplementationSpecific
            backend:
              service:
                name: gpfd-dev-service
                port:
                  number: 8080
