# Crypto Arena 🏟️

[![CI](https://github.com/xcyber-code/crypto-arena/actions/workflows/ci.yml/badge.svg)](https://github.com/xcyber-code/crypto-arena/actions/workflows/ci.yml)
[![Coverage](https://codecov.io/gh/xcyber-code/crypto-arena/branch/main/graph/badge.svg)](https://codecov.io/gh/xcyber-code/crypto-arena)

A high-performance cryptocurrency trading platform built with **Java 25** and modern tooling.

## 🚀 Features

- **Multi-module Gradle project** with Kotlin DSL
- **Java 25** with preview features enabled
- **JMH Benchmarks** for performance testing
- **JCStress** for concurrency testing
- **Allure Reports** for beautiful test documentation
- **OWASP Dependency Check** for security scanning
- **SpotBugs & Checkstyle** for code quality

## 📦 Modules

| Module | Description |
|--------|-------------|
| `common` | Shared utilities and models |
| `crypto-arena-engine` | Core trading engine |
| `crypto-arena-streamer` | Real-time market data streaming |
| `crypto-arena-registry` | Service registry and discovery |
| `crypto-arena-benchmarks` | JMH performance benchmarks |
| `crypto-arena-stress` | JCStress concurrency tests |

## 🛠️ Requirements

- **Java 25** (with preview features)
- **Gradle 9.2+** (included via wrapper)

## 🏗️ Build

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Run quality checks (tests + coverage + static analysis)
./gradlew qualityCheck

# Run security scan
./gradlew securityCheck

# Run all checks
./gradlew fullCheck
```

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Generate Allure report (requires Allure CLI)
allure serve */allure-results
```

## 📊 Benchmarks

```bash
# Run JMH benchmarks
./gradlew :crypto-arena-benchmarks:jmh

# Run JCStress concurrency tests
./gradlew :crypto-arena-stress:jcstress

# Quick JCStress run
./gradlew :crypto-arena-stress:jcstressQuick
```

## 📈 Reports

Live reports are published on GitHub Pages:

- [📊 Allure Test Report](https://xcyber-code.github.io/crypto-arena/allure/)
- [⚡ JMH Benchmarks](https://xcyber-code.github.io/crypto-arena/benchmarks/jmh/)
- [🔥 JCStress Tests](https://xcyber-code.github.io/crypto-arena/benchmarks/jcstress/)

## 🔒 Security

```bash
# Run OWASP dependency check
./gradlew dependencyCheckAnalyze
```

## 📝 CI/CD

GitHub Actions workflows are configured for:

- **CI** - Build, test, coverage, static analysis
- **PR Check** - Fast validation for pull requests
- **Benchmarks** - Weekly performance regression tests
- **Release** - Semantic versioning and automated releases

## 📄 License

MIT License
