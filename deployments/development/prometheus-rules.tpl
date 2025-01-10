apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  namespace: ${NAMESPACE}
  labels:
    role:
  name: prometheus-custom-rules-secretsmanager
spec:
groups:
  - name: application-rules
  rules:
  - alert: SecretsManagerPutSecretValue
    expr: secretsmanager_put_secret_value_sum{exported_job="secretsmanager", secret_id="arn:aws:secretsmanager:eu-west-2:754256621582:secret:<your-secret-arn>"} > 0
    for: 1m
    labels:
      severity: <severity>
    annotations:
      message: |
        {{ $labels.secret_id }} has had {{ $value }} PutSecretValue operations recently.
        {{ $labels.user_arn }} has had {{ $value }} PutSecretValue operations recently.
      runbook_url: <runbook_url>
      dashboard_url: <dashboard_url>
