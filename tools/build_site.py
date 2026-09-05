#!/usr/bin/env python3
"""Build the InteractiveStuff static website.

    python3 tools/build_site.py

Writes plain static HTML into /docs. There is no runtime build step — GitHub
Pages (or any static host) serves /docs as-is. This script only exists so the
header, footer and nav aren't copy-pasted across nine pages.

If you'd rather edit the generated HTML directly, that works too — just don't
re-run this script afterwards, or your edits will be overwritten.
"""

import os
import sys
from datetime import date

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
OUT = os.path.join(ROOT, "docs")
sys.path.insert(0, HERE)

# Used for sitemap.xml only. Change this if you deploy to a custom domain.
SITE_URL = "https://omardotcontent.github.io/InteractiveStuff"

from common import page  # noqa: E402
from content import home, features, gallery, install, docs, api, physics, faq, notfound  # noqa: E402

PAGES = [
    ("index.html", home),
    ("features.html", features),
    ("gallery.html", gallery),
    ("install.html", install),
    ("docs.html", docs),
    ("api.html", api),
    ("physics.html", physics),
    ("faq.html", faq),
    ("404.html", notfound),
]


def main():
    os.makedirs(os.path.join(OUT, "assets", "css"), exist_ok=True)
    os.makedirs(os.path.join(OUT, "assets", "js"), exist_ok=True)

    for filename, mod in PAGES:
        html = page(mod.TITLE, mod.DESC, mod.BODY, getattr(mod, "EXTRA_JS", ""))
        path = os.path.join(OUT, filename)
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(html)
        print(f"  wrote {filename:16} {len(html):>7,} bytes")

    _write_seo_files()

    total = sum(len(page(m.TITLE, m.DESC, m.BODY, getattr(m, "EXTRA_JS", ""))) for _, m in PAGES)
    print(f"\n{len(PAGES)} pages, {total:,} bytes of HTML -> {OUT}")


def _write_seo_files():
    """robots.txt + sitemap.xml, regenerated on every build."""
    # Jekyll on GitHub Pages ignores files starting with underscore; this keeps
    # it from touching anything.
    with open(os.path.join(OUT, ".nojekyll"), "w") as fh:
        fh.write("")

    with open(os.path.join(OUT, "robots.txt"), "w", encoding="utf-8") as fh:
        fh.write("User-agent: *\nAllow: /\n\nSitemap: " + SITE_URL + "/sitemap.xml\n")

    pages = [name for name, _ in PAGES if name != "404.html"]
    urls = []
    for name in pages:
        loc = SITE_URL + "/" if name == "index.html" else f"{SITE_URL}/{name}"
        prio = "1.0" if name == "index.html" else ("0.7" if name in ("api.html", "docs.html") else "0.8")
        urls.append(f"  <url>\n    <loc>{loc}</loc>\n"
                    f"    <lastmod>{date.today().isoformat()}</lastmod>\n"
                    f"    <priority>{prio}</priority>\n  </url>")
    with open(os.path.join(OUT, "sitemap.xml"), "w", encoding="utf-8") as fh:
        fh.write('<?xml version="1.0" encoding="UTF-8"?>\n'
                 '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n'
                 + "\n".join(urls) + "\n</urlset>\n")
    print(f"  wrote {'sitemap.xml':16} {len(pages)} urls (site: {SITE_URL})")


if __name__ == "__main__":
    main()
