#!/usr/bin/env python3
"""Build a short Markdown summary from Surefire XML reports.

Used by the CI workflow to write the test results into the GitHub job summary.
"""

import glob
import os
import sys
import xml.etree.ElementTree as ET


def collect(reports_dir):
    rows = []
    for path in sorted(glob.glob(os.path.join(reports_dir, "TEST-*.xml"))):
        root = ET.parse(path).getroot()
        rows.append(
            {
                "suite": root.get("name", os.path.basename(path)),
                "tests": int(root.get("tests", 0)),
                "failures": int(root.get("failures", 0)),
                "errors": int(root.get("errors", 0)),
                "skipped": int(root.get("skipped", 0)),
                "time": float(root.get("time", 0)),
            }
        )
    return rows


def main():
    reports_dir = sys.argv[1] if len(sys.argv) > 1 else "target/surefire-reports"
    rows = collect(reports_dir)

    if not rows:
        print("## Test results\n")
        print("No Surefire reports found in `{}`.".format(reports_dir))
        return

    total = lambda key: sum(row[key] for row in rows)  # noqa: E731

    print("## Test results\n")
    print("| Suite | Tests | Failures | Errors | Skipped | Time (s) |")
    print("| --- | ---: | ---: | ---: | ---: | ---: |")
    for row in rows:
        suite = row["suite"].split(".")[-1]
        print(
            "| {} | {} | {} | {} | {} | {:.2f} |".format(
                suite, row["tests"], row["failures"], row["errors"], row["skipped"], row["time"]
            )
        )

    failures, errors, skipped = total("failures"), total("errors"), total("skipped")
    status = "passed" if failures == 0 and errors == 0 else "failed"
    print(
        "\n**Total: {} tests, {} failures, {} errors, {} skipped - {}.**".format(
            total("tests"), failures, errors, skipped, status
        )
    )


if __name__ == "__main__":
    main()
