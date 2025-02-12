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
            message: Namespace {{ $labels.namespace }} is using {{ printf "%0.0f" $value }}% of its {{ $labels.resource }} quota.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubequotaexceeded
            summary: Namespace quota has exceeded the limits.
          expr: kube_resourcequota{job="kube-state-metrics",namespace="${NAMESPACE}",type="used"} / ignoring (instance, job, type) (kube_resourcequota{job="kube-state-metrics",namespace="${NAMESPACE}",type="hard"} > 0) > 0.9
          for: 15m
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubePodCrashLooping
          annotations:
            message: Pod {{ $labels.namespace }}/{{ $labels.pod }} ({{ $labels.container }}) is restarting {{ printf "%.2f" $value }} times / 5 minutes.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubepodcrashlooping
            summary: Pod has been restarting above limits for 1 hour.
          expr: rate(kube_pod_container_status_restarts_total{job="kube-state-metrics",namespace="${NAMESPACE}"}[15m]) * 60 * 5 > 0
          for: 1h
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubePodNotReady
          annotations:
            message: Pod {{ $labels.namespace }}/{{ $labels.pod }} has been in a non-ready state for longer than an hour.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubepodnotready
            summary: Pod has been in a non-ready state for more than 1 hour.
          expr: sum by (namespace, pod) (kube_pod_status_phase{job="kube-state-metrics",namespace="${NAMESPACE}",phase=~"Pending|Unknown"}) > 0
          for: 1h
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubeDeploymentGenerationMismatch
          annotations:
            message: Deployment generation for {{ $labels.namespace }}/{{ $labels.deployment}} does not match, this indicates that the Deployment has failed but has not been rolled back.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubedeploymentgenerationmismatch
            summary: Deployment generation mismatch due to possible roll-back.
          expr: (kube_deployment_status_observed_generation{job="kube-state-metrics",namespace="${NAMESPACE}"} != kube_deployment_metadata_generation{job="kube-state-metrics",namespace="${NAMESPACE}"})
          for: 15m
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: KubeDeploymentReplicasMismatch
          annotations:
            message: Deployment {{ $labels.namespace }}/{{ $labels.deployment }} has not matched the expected number of replicas for longer 15 minutes.
            runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/blob/master/runbook.md#alert-name-kubedeploymentreplicasmismatch
            summary: Deployment has not matched the expected number of replicas.
          expr: (kube_deployment_spec_replicas{job="kube-state-metrics",namespace="${NAMESPACE}"} != kube_deployment_status_replicas_available{job="kube-state-metrics",namespace="${NAMESPACE}"})
          for: 15m
          labels:
            severity: ${ALERT_SEVERITY}
        - alert: "Kubernetes Container OOM Killer"
          annotations:
            message: The Prometheus server uses a lot of memory and has ocassionally OOMKilled bringing down prometheus. Bump the node group monitoring instance size
            runbook_url: https://github.com/ministryofjustice/cloud-platform-infrastructure/commit/3dc05e588c9115c7aa44c2a9b5e26feff985f965
            summary: Kubernetes container OOM killed (instance {{ $labels.instance }})
          expr: sum_over_time(kube_pod_container_status_last_terminated_reason{reason="OOMKilled", namespace="${NAMESPACE}"}[5m]) > 0
          for: 0m
          labels:
            severity: ${ALERT_SEVERITY}
    - name: application-rules
      rules:
      - alert: 5xxErrorResponses
        annotations:
          message: Ingress {{ $labels.exported_namespace }}/{{ $labels.ingress }} is serving 5xx responses.
          summary: 5xx server errors.
        expr: avg by (ingress, exported_namespace) (rate(nginx_ingress_controller_requests{exported_namespace="${NAMESPACE}",status=~"5.*"}[1m]) > 0)
        for: 1m
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: 4xxErrorResponses
        annotations:
          message: Ingress {{ $labels.exported_namespace }}/{{ $labels.ingress }} is serving 4xx responses.
          summary: 4xx client errors.
        expr: avg by (ingress, exported_namespace) (rate(nginx_ingress_controller_requests{exported_namespace="${NAMESPACE}",status=~"4.*"}[1m]) > 0)
        for: 1m
        labels:
          severity: ${ALERT_SEVERITY}