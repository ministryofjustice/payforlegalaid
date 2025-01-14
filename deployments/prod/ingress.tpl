apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: gpfd-uat-ingress
  annotations:
    external-dns.alpha.kubernetes.io/set-identifier: gpfd-uat-ingress-${NAMESPACE}-green
    external-dns.alpha.kubernetes.io/aws-weight: "100"
    nginx.ingress.kubernetes.io/backend-protocol: https
    nginx.ingress.kubernetes.io/affinity: "cookie"
spec:
  ingressClassName: default
  tls:
    - hosts:
        - ${NAMESPACE}.apps.live.cloud-platform.service.justice.gov.uk
  rules:
    - host: ${NAMESPACE}.apps.live.cloud-platform.service.justice.gov.uk
      http:
        paths:
          - path: /
            pathType: ImplementationSpecific
            backend:
              service:
                name: gpfd-uat-service
                port:
                  number: 8443


# This is a generic file that creates a Kubernetes ingress in our namespace.
#  An ingress provides access to our application via the service described in the 'service.tpl'.


