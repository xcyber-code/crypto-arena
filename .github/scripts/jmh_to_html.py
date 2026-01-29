#!/usr/bin/env python3
"""
Convert JMH JSON results to a styled HTML report.
Usage: python3 jmh_to_html.py <input_dir> <output_dir>
"""

import json
import html
import os
import sys
from pathlib import Path
from typing import Optional


def find_json_file(input_dir: str) -> Optional[str]:
    """Find the first JSON file in the input directory."""
    for root, _, files in os.walk(input_dir):
        for f in files:
            if f.endswith(".json"):
                return os.path.join(root, f)
    return None


def generate_html(benchmarks: list) -> str:
    """Generate HTML content from benchmark data."""
    rows = []
    for b in benchmarks:
        name = html.escape(b.get("benchmark", ""))
        mode = html.escape(b.get("mode", ""))
        pm = b.get("primaryMetric", {})
        score = pm.get("score", 0)
        unit = html.escape(pm.get("scoreUnit", ""))
        error = pm.get("scoreError", 0)
        params = b.get("params", {})
        params_str = ", ".join(f"{k}={v}" for k, v in params.items()) if params else "-"
        rows.append((name, params_str, mode, f"{score:.2f}", unit, f"± {error:.2f}" if error else "-"))

    table_rows = ""
    for name, params, mode, score, unit, error in rows:
        table_rows += f"""        <tr>
          <td>{name}</td>
          <td>{params}</td>
          <td><span class="mode">{mode}</span></td>
          <td class="score">{score}</td>
          <td>{unit}</td>
          <td class="error">{error}</td>
        </tr>
"""

    return f'''<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>JMH Benchmark Results - Crypto Arena</title>
  <style>
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{ font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); min-height: 100vh; color: #fff; padding: 40px 20px; }}
    .container {{ max-width: 1200px; margin: 0 auto; }}
    h1 {{ font-size: 2rem; margin-bottom: 8px; }}
    .subtitle {{ color: #8892b0; margin-bottom: 30px; }}
    .back-link {{ color: #007bff; text-decoration: none; display: inline-block; margin-bottom: 20px; }}
    .back-link:hover {{ text-decoration: underline; }}
    table {{ width: 100%; border-collapse: collapse; background: rgba(255,255,255,0.05); border-radius: 12px; overflow: hidden; }}
    th, td {{ padding: 14px 16px; text-align: left; border-bottom: 1px solid rgba(255,255,255,0.1); }}
    th {{ background: rgba(255,255,255,0.1); font-weight: 600; color: #ccd6f6; }}
    tr:hover {{ background: rgba(255,255,255,0.03); }}
    .score {{ font-weight: bold; color: #64ffda; }}
    .error {{ color: #8892b0; font-size: 0.9em; }}
    .mode {{ background: rgba(100,255,218,0.1); padding: 4px 8px; border-radius: 4px; font-size: 0.85em; }}
    footer {{ margin-top: 40px; text-align: center; color: #8892b0; font-size: 0.85rem; }}
  </style>
</head>
<body>
  <div class="container">
    <a href="../" class="back-link">← Back to Reports</a>
    <h1>⚡ JMH Benchmark Results</h1>
    <p class="subtitle">Java Microbenchmark Harness performance measurements</p>
    <table>
      <thead>
        <tr>
          <th>Benchmark</th>
          <th>Parameters</th>
          <th>Mode</th>
          <th>Score</th>
          <th>Unit</th>
          <th>Error</th>
        </tr>
      </thead>
      <tbody>
{table_rows}      </tbody>
    </table>
    <footer>
      <p>Generated from JMH results • {len(rows)} benchmark(s) executed</p>
    </footer>
  </div>
</body>
</html>
'''


def main():
    if len(sys.argv) != 3:
        print("Usage: python3 jmh_to_html.py <input_dir> <output_dir>")
        sys.exit(1)

    input_dir = sys.argv[1]
    output_dir = sys.argv[2]

    # Create output directory
    Path(output_dir).mkdir(parents=True, exist_ok=True)

    # Find JSON file
    json_file = find_json_file(input_dir)
    if not json_file:
        print(f"No JSON file found in {input_dir}")
        # Create placeholder
        placeholder = "<html><body><h1>No JMH Results</h1><p>No benchmark JSON results available.</p></body></html>"
        Path(output_dir, "index.html").write_text(placeholder)
        sys.exit(0)

    print(f"Found JMH JSON: {json_file}")

    # Load JSON
    with open(json_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    # Handle both array format and object format
    benchmarks = data if isinstance(data, list) else data.get("benchmarks", [])

    # Generate HTML
    html_content = generate_html(benchmarks)

    # Write output
    output_file = Path(output_dir, "index.html")
    output_file.write_text(html_content, encoding="utf-8")
    print(f"✅ Generated JMH HTML report with {len(benchmarks)} benchmarks: {output_file}")


if __name__ == "__main__":
    main()
