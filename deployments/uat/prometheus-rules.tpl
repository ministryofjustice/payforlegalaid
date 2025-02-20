apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  namespace: ${NAMESPACE}
  labels:
    prometheus: prometheus-operator
    role: alert-rules
    release: prometheus-operator
  name: prometheus-custom-rules-laa-gpfd-api
spec:
  groups:
    - name: kubernetes-rules
      rules:
        - alert: KubeQuota-Exceeded
          annotations:
            message: Namespace quota has exceeded the limits.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubequotaexceeded
            dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
          expr: kube_resourcequota{job="kube-state-metrics",namespace="${NAMESPACE}",type="used"} / ignoring (instance, job, type) (kube_resourcequota{job="kube-state-metrics",namespace="${NAMESPACE}",type="hard"} > 0) > 0.9
          for: 15m
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubePodCrashLooping
          annotations:
            message: Pod has been restarting above limits for 1 hour.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubepodcrashlooping
            dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
          expr: rate(kube_pod_container_status_restarts_total{job="kube-state-metrics",namespace="${NAMESPACE}"}[15m]) * 60 * 5 > 0
          for: 1h
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubePodNotReady
          annotations:
            message: Pod has been in a non-ready state for more than 1 hour.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubepodnotready
            dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
          expr: sum by (namespace, pod) (kube_pod_status_phase{job="kube-state-metrics",namespace="${NAMESPACE}",phase=~"Pending|Unknown"}) > 0
          for: 1h
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubeDeploymentGenerationMismatch
          annotations:
            message: Deployment generation mismatch due to possible roll-back.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubedeploymentgenerationmismatch
            dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
          expr: (kube_deployment_status_observed_generation{job="kube-state-metrics",namespace="${NAMESPACE}"} != kube_deployment_metadata_generation{job="kube-state-metrics",namespace="${NAMESPACE}"})
          for: 15m
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubeDeploymentReplicasMismatch
          annotations:
            message: Deployment has not matched the expected number of replicas.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/blob/master/runbook.md#alert-name-kubedeploymentreplicasmismatch
            dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
          expr: (kube_deployment_spec_replicas{job="kube-state-metrics",namespace="${NAMESPACE}"} != kube_deployment_status_replicas_available{job="kube-state-metrics",namespace="${NAMESPACE}"})
          for: 15m
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: "Kubernetes Container OOM Killer"
          annotations:
            message: The Prometheus server uses a lot of memory and has ocassionally OOMKilled bringing down prometheus. Bump the node group monitoring instance size
            runbook_url: https://github.com/ministryofjustice/cloud-platform-infrastructure/commit/3dc05e588c9115c7aa44c2a9b5e26feff985f965
            dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
          expr: sum_over_time(kube_pod_container_status_last_terminated_reason{reason="OOMKilled", namespace="${NAMESPACE}"}[5m]) > 0
          for: 0m
          labels:
            severity: ${ALERT_SEVERITY}
    - name: application-rules
      rules:
      - alert: nginx-SlowResponses
        annotations:
          message: Ingress is serving slow responses over 2 seconds.
          dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
        expr: |-
          avg(rate(nginx_ingress_controller_request_duration_seconds_sum{exported_namespace = "${NAMESPACE}"}[5m])
          /
          rate(nginx_ingress_controller_request_duration_seconds_count{exported_namespace = "${NAMESPACE}"}[5m]) > 0) by (ingress) >2
        for: 1m
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: 5xxErrorResponses
        annotations:
          message: Ingress is serving 5XX responses.
          dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
        expr: sum(rate(nginx_ingress_controller_requests{exported_namespace="${NAMESPACE}", status=~"5.*"}[5m]))*270 > 10
        for: 1m
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: 4xxErrorResponses
        annotations:
          message: Ingress is serving 4XX responses.
          dashboard_url: "https://grafana.live.cloud-platform.service.justice.gov.uk/d/laa-gpfd-uat/87bfec2"
        expr: sum(rate(nginx_ingress_controller_requests{exported_namespace="${NAMESPACE}", status=~"4.*"}[5m]))*270 > 10
        for: 1m
        labels:
          severity: ${ALERT_SEVERITY}