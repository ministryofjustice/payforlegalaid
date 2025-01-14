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
    - name: secretmanager-rules
      rules:
      - alert: SecretsManagerPutSecretValue
        expr: secretsmanager_put_secret_value_sum{exported_job="secretsmanager", secret_id="arn:aws:secretsmanager:eu-west-2:754256621582:secret:live-laa-get-payments-finance-data-dev-d4def64b8869d886-jJRZ1c"} > 0
        for: 1m
        labels:
          severity: ${ALERT_SEVERITY}
        annotations:
          message: |
            {{ $labels.secret_id }} has had {{ $value }} PutSecretValue operations recently.
            {{ $labels.user_arn }} has had {{ $value }} PutSecretValue operations recently.
          runbook_url: https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/5297832119/Runbooks
    - name: kubernetes-rules
      rules:
      - alert: KubeQuota-Exceeded
        annotations:
          message: Namespace {{ $labels.namespace }} is using {{ printf "%0.0f" $value }}% of its {{ $labels.resource }} quota.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubequotaexceeded
        expr: |-
          100 * kube_resourcequota{job="kube-state-metrics", type="used"}
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          / ignoring(instance, job, type)
          (kube_resourcequota{job="kube-state-metrics", type="hard"} > 0)
          > 90
        for: 15m
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubePodCrashLooping
        annotations:
          message: Pod {{ $labels.namespace }}/{{ $labels.pod }} ({{ $labels.container }}) is restarting {{ printf "%.2f" $value }} times / 5 minutes.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubepodcrashlooping
        expr: |-
          rate(kube_pod_container_status_restarts_total{job="kube-state-metrics"}[15m])
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          * 60 * 5 > 0
        for: 1h
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubePodNotReady
        annotations:
          message: Pod {{ $labels.namespace }}/{{ $labels.pod }} has been in a non-ready
            state for longer than an hour.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubepodnotready
        expr: |-
          sum by (namespace, pod) (kube_pod_status_phase{job="kube-state-metrics", phase=~"Pending|Unknown"})
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          > 0
        for: 1h
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubeDeploymentGenerationMismatch
        annotations:
          message: Deployment generation for {{ $labels.namespace }}/{{ $labels.deployment }} does not match, this indicates that the Deployment has failed but has
            not been rolled back.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubedeploymentgenerationmismatch
        expr: |-
          kube_deployment_status_observed_generation{job="kube-state-metrics"}
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          !=
          kube_deployment_metadata_generation{job="kube-state-metrics"}
        for: 15m
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubeDeploymentReplicasMismatch
        annotations:
          message: Deployment {{ $labels.namespace }}/{{ $labels.deployment }} has not
            matched the expected number of replicas for longer than an hour.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubedeploymentreplicasmismatch
        expr: |-
          kube_deployment_spec_replicas{job="kube-state-metrics"}
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          != kube_deployment_status_replicas_available{job="kube-state-metrics"}
        for: 1h
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubeCronJobRunning
        annotations:
          message: CronJob {{ $labels.namespace }}/{{ $labels.cronjob }} is taking more
            than 1h to complete.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubecronjobrunning
        expr: |-
          time() - kube_cronjob_next_schedule_time{job="kube-state-metrics"}
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          > 3600
        for: 1h
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubeJobCompletion
        annotations:
          message: Job {{ $labels.namespace }}/{{ $labels.job_name }} is taking more
            than one hour to complete.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubejobcompletion
        expr: |-
          kube_job_spec_completions{job="kube-state-metrics"} - kube_job_status_succeeded{job="kube-state-metrics"}
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          > 0
        for: 1h
        labels:
          severity: ${ALERT_SEVERITY}
      - alert: KubeJobFailed
        annotations:
          message: Job {{ $labels.namespace }}/{{ $labels.job_name }} failed to complete.
          runbook_url: https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubejobfailed
        expr: |-
          kube_job_status_failed{job="kube-state-metrics"}
          * on (namespace)
          group_left()
          kube_namespace_annotations{annotation_cloud_platform_out_of_hours_alert="true"}
          > 0
        for: 1h
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