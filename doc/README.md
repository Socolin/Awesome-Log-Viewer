# Awesome Log Viewer — Documentation

Awesome Log Viewer is a JetBrains IDE plugin (Rider, IntelliJ, and other IntelliJ-based
IDEs) that displays the logs and telemetry produced by your application in a structured,
filterable, color-coded view — instead of as raw lines scrolling by in the console.

Start here:

- **[Overview & General Settings](overview.md)** — what the plugin does, the log view, the
  toolbar, filtering, and the global settings shared by every module.

Then read the page for the log source(s) you use:

- **[Application Insights module](module-application-insights.md)** — capture and view Azure
  Application Insights telemetry (requests, dependencies, exceptions, metrics, …).
- **[OpenTelemetry module](module-opentelemetry.md)** — capture and view OpenTelemetry
  traces, metrics, and logs (OTLP).
- **[Custom / Console module](module-custom-console.md)** — parse console output from
  Serilog, Microsoft.Extensions.Logging, NLog, or any custom log format you define.

## Premium features

Some capabilities require a license:

- **Structured / Hierarchical views** — display logs as a tree following parent/child
  relationships instead of a flat list.
- **Waterfall view** — a timeline column visualizing how log entries overlap in time.

See the [Overview](overview.md#premium-features) for details.
