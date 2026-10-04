# Custom / Console module

This module (internally the *Simple Console* processor) reads your application's **console
output** and turns each line into a structured log entry. It ships with parsers for common
.NET logging frameworks and lets you define your own format with a regex or by reading JSON.

Settings live under **Settings / Preferences → Tools → Awesome Log Viewer → Simple
Console**.

## Built-in formats

Three console processors are provided out of the box:

| Processor | Recognized severities | Filterable properties |
| --- | --- | --- |
| **.NET: Serilog** | VRB, DBG, INF, WRN, ERR, FTL | Severity |
| **.NET: Microsoft.Extensions.Logging** | dbug, info, warn, fail, crit | Severity, Category |
| **.NET: NLog** | TRACE, DEBUG, INFO, WARN, ERROR, FATAL | Severity, Category |

All three handle multi-line messages (stack traces, indented continuation lines) out of the
box. They are a good starting point — duplicate one or add a new processor to fit your own
output.

## Adding a custom processor

Use **Add Console Processor…** on the Simple Console settings page. A processor has:

### General

- **Display name** — the name shown on the log tab.
- **Id** — a unique identifier for the processor.
- The [shared Activation settings](overview.md#shared-per-source-settings) (Enable when
  running / debugging).
- **Output source** — read the **Standard output** (stdout/stderr), the **Debug output**, or
  both.

### Log parsing

Choose a **Parsing Method**:

#### Regex (default)

- **Log start pattern** — *"A Regex used to match the first line of a log. The capture groups
  will be available as properties."* Each **named capture group** (`(?<name>…)`) becomes a
  property of the entry. A few names are special:
  - `message` — **the text shown in the main column of the log table.** ⚠️ If you do not
    capture a group called `message`, the rows in the table look empty — even though the
    detail panel and the filters still work (see [Why are my rows blank?](#why-are-my-rows-blank-in-the-table)).
  - `severity` — mapped to a severity level, which drives the **Severity** column and the row
    color (see the [severity mapping](#severity-mapping) below).
  - `category` — the logger/category name.
  - `id` / `parentId` — used to build the structured (tree) view *(premium)*.
  - any other name — kept as a property: visible in the detail panel and usable as a filter
    if you add it under **Filtering Properties**.

  Whole-line matching: each console line is matched **in its entirety** against the pattern.
  This means the `^` (start) and `$` (end) anchors are optional — the pattern already has to
  cover the whole line — and a pattern can only ever match a *single* line. To capture
  several lines (e.g. a stack trace), use **Multiline** below rather than trying to make one
  pattern span line breaks.

- **Multiline** — when enabled, a second **Next lines log pattern** describes continuation
  lines (for example indented stack-trace lines). Each following line is matched (again, in
  its entirety) against this pattern and, on a match, its captured groups are appended to the
  current entry; the first line that does **not** match ends the entry. As with the start
  pattern, `^`/`$` are optional and the pattern applies to one line at a time.

The pattern is validated as you type (*"Should be a valid Regex …"*).

> **Example.** For lines like `2026-05-10 14:03:11 [ 12] INFO  - Started up`:
> ```
> (?<date>\d+-\d+-\d+ \d+:\d+:\d+) \[\s*(?<threadId>\d+)\] (?<severity>\w+)\s*- (?<message>.+)
> ```
> The table shows `Started up` (from `message`), colors the row by `INFO` (from `severity`),
> and `date`, `threadId`, `severity` are all available in the detail panel.

##### Severity mapping

Severity strings are matched case-insensitively to the six standard levels:

| Level | Recognized values |
| --- | --- |
| Trace | `vrb`, `trace` |
| Debug | `dbug`, `dbg`, `debug` |
| Info | `info`, `inf`, `information` |
| Warning | `warn`, `warning` |
| Error | `error`, `fail`, `err`, `severe` |
| Critical | `crit`, `critical`, `fatal`, `ftl` |

#### JSON

Use JSON mode when your application already writes **structured logs, one complete JSON
object per console line** (so-called JSON-lines). For example:

```
{"severity":"INFO","message":"Started up","category":"Startup","threadId":12}
```

There is no pattern to write: the plugin parses each line as JSON and turns **every primitive
field** (string, number, boolean) into a property automatically. The field names play the
same special roles as the regex groups — `message` fills the table's main column, `severity`
drives the severity column/color, and `id` / `parentId` build the structured view. The
object is pretty-printed in the detail panel.

So the choice is simply: **Use regex** when your app prints plain text lines you want to
pick apart with a pattern, and **Use JSON** when your app can emit one JSON object per line
(no regex needed). One line that is not valid JSON is skipped.

### Advanced

- **Remove ANSI code before processing** — strip terminal color escape codes before matching
  (recommended when your framework colorizes its output). Enabled by default.
- **Structured Logs** *(premium)* — display entries as a collapsible **tree** instead of a
  flat list, nesting each entry under its parent. It works by reading the `id` and `parentId`
  properties you capture: an entry whose `parentId` equals another entry's `id` becomes its
  child. See [Structured / hierarchical view](overview.md#structured-logs-premium) for what it
  is and when it helps. Leave it off if your logs have no parent/child relationship.
- **Filtering Properties** — *"One property per line, using the format `property: Display
  Name` where properties are values captured by the regex groups."* This is how you turn a
  captured group into a named, filterable column, e.g.:

  ```
  severity: Severity Level
  category: Logger Category
  threadId: Thread ID
  ```

### Environment Variables

> *"Environment variables added when starting a program. This can allow you to control the
> console format depending on whether the plugin is present or not."*

These variables do **not** change how the plugin parses logs. They are injected into your
program's environment when it starts (for .NET and Java run configurations), so your *own*
code can detect that it is running under Awesome Log Viewer and adjust the format it prints —
typically to emit a layout that is easy for your pattern to match, or plain JSON for JSON
mode.

For example, you might set a variable here:

```
LOG_FORMAT=json
```

and have your logging configuration switch to a single-line JSON layout when `LOG_FORMAT` is
present. When you run without the plugin, the variable is absent and your app keeps its
normal human-readable output. If you don't need this, leave the field empty.

