# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## 2026.3.2

- Add support for additional columns in the log table #15
  - Custom Console: new `Additional Columns` setting to display any captured property as a column (the NLog and Microsoft.Extensions.Logging presets add a `Category` column)
  - OpenTelemetry: new `Category` column
- Custom Console settings: `Filtering Properties` and `Additional Columns` are now edited in a table instead of a text area
- Fix sorting the wrong column when clicking a header after hiding or moving columns
- Fix the waterfall column not being refreshed when the total duration changes

## 2026.3.1

- Compatibility with Rider 2026.3
- Add option to copy a value with right click from the formatted tab

## 2026.2.1

- Compatibility with Rider 2026.2
- Add [documentation](https://github.com/Socolin/Awesome-Log-Viewer/tree/main/doc)

## 2025.3.2

- Fix UI not appearing in Rider 2025.3.2
- Add option to configure the line height of the rows #5

## 2025.2.2

- Improve messages for OT traces for non http dependencies #10 (Thanks @TonyReg)
- Fix possible null reference in license checker code #6

## 2025.2.1

- Add a new option to forward logs to the originally configured endpoint. This will allow logs to be forwarded if another collector was configured. Example: The .NET Aspire Dashboard for OpenTelemetry or the OpenTelemetry plugin in Rider. It will only works in Rider for now

## 2025.2.0

- Compatibility with Rider 2025.2

## 2025.1.4

- Add new `severity` column

## 2025.1.3

- Initial Release

## 1.0.0
- Initial version
