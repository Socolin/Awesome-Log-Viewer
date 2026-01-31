# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

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
