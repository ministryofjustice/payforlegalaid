apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: gpfd-dev-ingress
  annotations:
    external-dns.alpha.kubernetes.io/set-identifier: gpfd-dev-ingress-${NAMESPACE}-green
    external-dns.alpha.kubernetes.io/aws-weight: "100"
    nginx.ingress.kubernetes.io/backend-protocol: http
    nginx.ingress.kubernetes.io/affinity: "cookie"
spec:
  ingressClassName: default
  tls:
    - hosts:
        - testable-${BRANCH_NAME}-${NAMESPACE}.apps.live.cloud-platform.service.justice.gov.uk
  rules:
    - host: testable-${BRANCH_NAME}-${NAMESPACE}.apps.live.cloud-platform.service.justice.gov.uk
      http:
        paths:
          - path: /
            pathType: ImplementationSpecific
            backend:
              service:
                name: gpfd-dev-service
                port:
                  number: 8080
