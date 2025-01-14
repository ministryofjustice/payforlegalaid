apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: gpfd-prod-ingress
  annotations:
    external-dns.alpha.kubernetes.io/set-identifier: gpfd-prod-ingress-${NAMESPACE}-green
    external-dns.alpha.kubernetes.io/aws-weight: "100"
    nginx.ingress.kubernetes.io/backend-protocol: http
    nginx.ingress.kubernetes.io/affinity: "cookie"
spec:
  ingressClassName: default
  tls:
    - hosts:
        - ${NAMESPACE}.apps.live.cloud-platform.service.justice.gov.uk
    - hosts:
      - 'get-legal-aid-data.service.justice.gov.uk'
      secretName: tls-certificate
  rules:
    - host: get-legal-aid-data.service.justice.gov.uk
      http:
        paths:
          - path: /
            pathType: ImplementationSpecific
            backend:
              service:
                name: gpfd-prod-service
                port:
                  number: 8080
    - host: 'get-legal-aid-data.service.justice.gov.uk'
      http:
        paths:
        - path: /
          pathType: ImplementationSpecific
          backend:
            service:
              name: gpfd-prod-service
              port:
                number: 8080

# This is a generic file that creates a Kubernetes ingress in our namespace.
#  An ingress provides access to our application via the service described in the 'service.tpl'.


