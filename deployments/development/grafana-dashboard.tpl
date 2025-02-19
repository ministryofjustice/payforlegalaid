---
apiVersion: v1
kind: ConfigMap
metadata:
  name: ${NAMESPACE}-dashboard
  namespace: ${NAMESPACE}
  labels:
    grafana_dashboard: ${NAMESPACE}-dashboard
data:
  ${NAMESPACE}-dashboard.json: |
    {
    "annotations": {
      "list": [
        {
          "builtIn": 1,
          "datasource": {
            "type": "datasource",
            "uid": "grafana"
          },
          "enable": true,
          "hide": true,
          "iconColor": "rgba(0, 211, 255, 1)",
          "limit": 100,
          "name": "Annotations & Alerts",
          "showIn": 0,
          "target": {
            "limit": 100,
            "matchAny": false,
            "tags": [],
            "type": "dashboard"
          },
          "type": "dashboard"
        }
      ]
    },
    "editable": true,
    "fiscalYearStartMonth": 0,
    "graphTooltip": 0,
    "id": 187,
    "links": [],
    "panels": [
      {
        "collapsed": false,
        "gridPos": {
          "h": 1,
          "w": 24,
          "x": 0,
          "y": 0
        },
        "id": 53,
        "panels": [],
        "title": "Container memory",
        "type": "row"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 9,
          "w": 7,
          "x": 0,
          "y": 1
        },
        "id": 52,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "round(\n  100 *\n    sum(container_memory_working_set_bytes{image!='', namespace='${NAMESPACE}', pod!='gpfd.*'}) by (container, pod_name)\n      /\n    sum(kube_pod_container_resource_requests{resource=\"memory\", namespace='${NAMESPACE}', pod!='gpfd.*'} > 0) by (container, pod)\n)",
            "fullMetaSearch": false,
            "includeNullMetadata": true,
            "legendFormat": "__auto",
            "range": true,
            "refId": "A",
            "useBackend": false
          }
        ],
        "title": "Percentage of container memory limit",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 9,
          "w": 8,
          "x": 7,
          "y": 1
        },
        "id": 51,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "round(\n  100 *\n    sum(container_memory_working_set_bytes{image!='',namespace='${NAMESPACE}',pod=~\"gpfd.*\"}) by (container, pod)\n      /\n    sum(kube_pod_container_resource_requests{resource='memory', namespace='${NAMESPACE}',pod=~\"gpfd.*\"} > 0) by (container, pod)\n)",
            "fullMetaSearch": false,
            "includeNullMetadata": true,
            "legendFormat": "__auto",
            "range": true,
            "refId": "A",
            "useBackend": false
          }
        ],
        "title": "Percentage of container memory request",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 9,
          "w": 9,
          "x": 15,
          "y": 1
        },
        "id": 50,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "container_memory_usage_bytes{namespace=\"${NAMESPACE}\", pod=~'gpfd.*'}",
            "fullMetaSearch": false,
            "includeNullMetadata": true,
            "legendFormat": "__auto",
            "range": true,
            "refId": "A",
            "useBackend": false
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "container_memory_max_usage_bytes{namespace=\"${NAMESPACE}\", pod=~'gpfd.*'}",
            "fullMetaSearch": false,
            "hide": false,
            "includeNullMetadata": true,
            "instant": false,
            "legendFormat": "__auto",
            "range": true,
            "refId": "B",
            "useBackend": false
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "",
            "fullMetaSearch": false,
            "hide": false,
            "includeNullMetadata": true,
            "instant": false,
            "legendFormat": "__auto",
            "range": true,
            "refId": "C",
            "useBackend": false
          }
        ],
        "title": "Container Memory Usage",
        "type": "timeseries"
      },
      {
        "collapsed": false,
        "gridPos": {
          "h": 1,
          "w": 24,
          "x": 0,
          "y": 10
        },
        "id": 47,
        "panels": [],
        "title": "Container CPU",
        "type": "row"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 8,
          "w": 12,
          "x": 0,
          "y": 11
        },
        "id": 49,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "disableTextWrap": false,
            "editorMode": "code",
            "exemplar": false,
            "expr": "sum(rate(container_cpu_usage_seconds_total{namespace=\"${NAMESPACE}\", pod=~'gpfd.*'}[5m])) by (pod_name, container_name)",
            "fullMetaSearch": false,
            "includeNullMetadata": true,
            "instant": false,
            "interval": "",
            "legendFormat": "__auto",
            "range": true,
            "refId": "A",
            "useBackend": false
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "sum(kube_pod_container_resource_limits{namespace=\"${NAMESPACE}\", pod=~'gpfd.*', resource='cpu'}) by (pod)",
            "fullMetaSearch": false,
            "hide": false,
            "includeNullMetadata": true,
            "instant": false,
            "legendFormat": "limit",
            "range": true,
            "refId": "B",
            "useBackend": false
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "sum(kube_pod_container_resource_requests{namespace='${NAMESPACE}', pod=~'gpfd.*', resource='cpu'}) by (pod)",
            "fullMetaSearch": false,
            "hide": false,
            "includeNullMetadata": true,
            "instant": false,
            "legendFormat": "request",
            "range": true,
            "refId": "C",
            "useBackend": false
          }
        ],
        "title": "CPU: percentage of limit",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "description": "Top 3 CPU Used Per Instance",
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 8,
          "w": 12,
          "x": 12,
          "y": 11
        },
        "id": 48,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "disableTextWrap": false,
            "editorMode": "code",
            "expr": "topk(3, max by (pod, container)(rate(container_cpu_usage_seconds_total{image!='', namespace='${NAMESPACE}',pod=~\"gpfd.*\"}[$__rate_interval]))) / 1",
            "fullMetaSearch": false,
            "includeNullMetadata": true,
            "legendFormat": "__auto",
            "range": true,
            "refId": "A",
            "useBackend": false
          }
        ],
        "title": "CPU Usage Per Instance",
        "type": "timeseries"
      },
      {
        "collapsed": false,
        "gridPos": {
          "h": 1,
          "w": 24,
          "x": 0,
          "y": 19
        },
        "id": 40,
        "panels": [],
        "title": "Api Stats",
        "type": "row"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "thresholds"
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 2,
          "x": 0,
          "y": 20
        },
        "id": 2,
        "options": {
          "minVizHeight": 75,
          "minVizWidth": 75,
          "orientation": "auto",
          "reduceOptions": {
            "calcs": [
              "lastNotNull"
            ],
            "fields": "",
            "values": false
          },
          "showThresholdLabels": false,
          "showThresholdMarkers": true,
          "sizing": "auto"
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "builder",
            "expr": "sum(kube_pod_container_info{namespace='${NAMESPACE}',pod=~'.*gpfd.*'})",
            "legendFormat": "Total",
            "range": true,
            "refId": "A"
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "sum({namespace='${NAMESPACE}',phase='Running',pod=~'.*gpfd.*'} + 0)",
            "hide": false,
            "legendFormat": "Running",
            "range": true,
            "refId": "B"
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "builder",
            "expr": "sum({namespace='${NAMESPACE}',phase='Pending',pod=~'.*gpfd.*'} + 0)",
            "hide": false,
            "legendFormat": "Pending",
            "range": true,
            "refId": "C"
          },
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "sum({namespace='${NAMESPACE}',phase='Failed',pod=~'.*gpfd.*'} + 0)",
            "hide": false,
            "legendFormat": "Failed",
            "range": true,
            "refId": "D"
          }
        ],
        "title": "Pods Info",
        "type": "gauge"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 4,
          "x": 2,
          "y": 20
        },
        "id": 20,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "sum by(instance) (increase(http_server_requests_seconds_count{namespace='${NAMESPACE}', status='401'}[$__rate_interval]))",
            "legendFormat": "__auto",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Unauthorised Requests 401",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 6,
          "x": 6,
          "y": 20
        },
        "id": 46,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "sum by(instance) (increase(http_server_requests_seconds_count{namespace='${NAMESPACE}',status='200'}[$__rate_interval])) ",
            "legendFormat": "__auto",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Successful Requests Per Instance",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 12,
          "x": 12,
          "y": 20
        },
        "id": 4,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "sum by(uri) (increase(http_server_requests_seconds_count{namespace='${NAMESPACE}', uri!~'.*swagger.*|.*health.*', status='200'}[$__rate_interval])) ",
            "legendFormat": "{{uri}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Successful Requests Per Api Endpoints",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            },
            "unit": "s"
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 12,
          "x": 0,
          "y": 30
        },
        "id": 42,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "sum by(uri) (rate(http_server_requests_seconds_sum{namespace='${NAMESPACE}', uri!~'.*swagger.*|.*health.*', status='200'}[1m])) / sum by(uri) (rate(http_server_requests_seconds_count{namespace='${NAMESPACE}', uri!~'.*swagger.*|.*health.*', status='200'}[$__rate_interval]))",
            "legendFormat": "__auto",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Average Response Time Per Uri",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            },
            "unit": "s"
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 12,
          "x": 12,
          "y": 30
        },
        "id": 44,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "max by(uri) (http_server_requests_seconds_max{namespace='${NAMESPACE}', uri!~'.*swagger.*|.*health.*', status='200'}) ",
            "legendFormat": "__auto",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Max Response Time",
        "type": "timeseries"
      },
      {
        "collapsed": false,
        "gridPos": {
          "h": 1,
          "w": 24,
          "x": 0,
          "y": 40
        },
        "id": 36,
        "panels": [],
        "title": "JVM Stats",
        "type": "row"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "thresholds"
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 2,
          "x": 0,
          "y": 41
        },
        "id": 16,
        "options": {
          "minVizHeight": 75,
          "minVizWidth": 75,
          "orientation": "auto",
          "reduceOptions": {
            "calcs": [
              "lastNotNull"
            ],
            "fields": "",
            "values": false
          },
          "showThresholdLabels": false,
          "showThresholdMarkers": true,
          "sizing": "auto",
          "text": {}
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "jvm_memory_max_bytes{namespace='${NAMESPACE}',area='heap',id='G1 Survivor Space'} / 1000000",
            "legendFormat": "{{instance}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Max Survivor",
        "type": "gauge"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "MB",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 6,
          "x": 2,
          "y": 41
        },
        "id": 10,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "jvm_memory_used_bytes{namespace='${NAMESPACE}',area='heap',id='G1 Survivor Space'}/1000000",
            "legendFormat": "{{instance}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Used Survivor Space (MB)",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "thresholds"
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 2,
          "x": 8,
          "y": 41
        },
        "id": 18,
        "options": {
          "minVizHeight": 75,
          "minVizWidth": 75,
          "orientation": "auto",
          "reduceOptions": {
            "calcs": [
              "lastNotNull"
            ],
            "fields": "",
            "values": false
          },
          "showThresholdLabels": false,
          "showThresholdMarkers": true,
          "sizing": "auto"
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "jvm_memory_max_bytes{namespace='${NAMESPACE}',area='heap',id='G1 Eden Space'}/1000000",
            "legendFormat": "{{instance}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Max Eden",
        "type": "gauge"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "MB",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "line"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "#EAB839",
                  "value": ""
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 6,
          "x": 10,
          "y": 41
        },
        "id": 8,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "jvm_memory_used_bytes{namespace='${NAMESPACE}',area='heap',id='G1 Eden Space'}/1000000",
            "legendFormat": "{{instance}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Used Eden Space (MB)",
        "type": "timeseries"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "thresholds"
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 2,
          "x": 16,
          "y": 41
        },
        "id": 14,
        "options": {
          "minVizHeight": 75,
          "minVizWidth": 75,
          "orientation": "auto",
          "reduceOptions": {
            "calcs": [
              "lastNotNull"
            ],
            "fields": "",
            "values": false
          },
          "showThresholdLabels": false,
          "showThresholdMarkers": true,
          "sizing": "auto"
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "jvm_memory_max_bytes{namespace='${NAMESPACE}',area='heap',id='G1 Old Gen'} /1000000",
            "legendFormat": "{{instance}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Max Tenured",
        "type": "gauge"
      },
      {
        "datasource": {
          "type": "prometheus",
          "uid": "prometheus"
        },
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisBorderShow": false,
              "axisCenteredZero": false,
              "axisColorMode": "text",
              "axisLabel": "MB",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "barWidthFactor": 0.6,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "insertNulls": false,
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 10,
          "w": 6,
          "x": 18,
          "y": 41
        },
        "id": 12,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom",
            "showLegend": true
          },
          "tooltip": {
            "mode": "single",
            "sort": "none"
          }
        },
        "pluginVersion": "11.3.0",
        "targets": [
          {
            "datasource": {
              "type": "prometheus",
              "uid": "prometheus"
            },
            "editorMode": "code",
            "expr": "jvm_memory_used_bytes{namespace='${NAMESPACE}',area='heap',id='G1 Old Gen'} /1000000",
            "legendFormat": "{{instance}}",
            "range": true,
            "refId": "A"
          }
        ],
        "title": "Used Tenured Gen (MB)",
        "type": "timeseries"
      }
    ],
    "preload": false,
    "refresh": "",
    "schemaVersion": 40,
    "tags": [
      "api",
      "LAA",
      "LAA Get Payments & Finance Data",
      "GPFD",
      "Get Legal Aid Data",
      "GLAD"
    ],
    "templating": {
      "list": []
    },
    "time": {
      "from": "now-15m",
      "to": "now"
    },
    "timepicker": {
      "refresh_intervals": [
        "10s",
        "30s",
        "1m",
        "5m",
        "15m",
        "30m",
        "1h",
        "2h",
        "1d"
      ]
    },
    "timezone": "browser",
    "title": "LAA GPFD Get Payment & Finance Data ${ENV_NAME} dashboard",
    "uid": "laa-gpfd-${ENV_NAME}",
    "version": 3,
    "weekStart": ""
  }