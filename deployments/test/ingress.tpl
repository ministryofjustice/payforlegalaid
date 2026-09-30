apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${BRANCH_NAME}-gpfd-dev-ingress
  annotations:
    nginx.ingress.kubernetes.io/backend-protocol: http
    nginx.ingress.kubernetes.io/affinity: "cookie"
    nginx.ingress.kubernetes.io/whitelist-source-range: "${IP_LIST}"
  labels:
    branch: ${BRANCH_NAME}
spec:
  ingressClassName: default
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
