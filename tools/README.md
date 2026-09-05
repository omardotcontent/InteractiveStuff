# Website tooling

The InteractiveStuff website lives in [`docs/`](../docs) as **plain static HTML, CSS and JS** — there is no
build step, no framework and no dependencies. GitHub Pages (or any static host) serves that folder as-is.

This directory only exists so the nav, footer and a few HTML snippets aren't copy-pasted across nine pages.
It is a convenience, not a requirement.

## Layout

```
docs/                    ← published site (edit these files directly if you like)
├── index.html           Home
├── features.html        Every built-in interaction
├── gallery.html         Screenshots & brand assets
├── install.html         Requirements, install steps, troubleshooting
├── docs.html            Getting started with Vyn
├── api.html             Searchable Vyn API reference (231 entries)
├── physics.html         Physics Library
├── faq.html             FAQ
├── 404.html             Not found
├── assets/
│   ├── css/style.css
│   └── js/
│       ├── site.js      Nav, theme, code highlighting, live Modrinth stats
│       ├── api-data.js  The API reference dataset
│       └── api.js       Renders + searches that dataset
└── .nojekyll

tools/
├── build_site.py        Regenerates the HTML in docs/
├── common.py            Shared header/footer + HTML helpers
└── content/             One module per page (title, description, body)
```

## Rebuilding

```bash
python3 tools/build_site.py
```

Requires Python 3.8+. No packages to install.

> **Heads up:** rebuilding overwrites `docs/*.html`. If you've edited the generated HTML by hand, either
> stop using the generator or move your change into the matching `tools/content/*.py` module first.

## Adding a page

1. Create `tools/content/mypage.py` exporting `TITLE`, `DESC`, `BODY` (and optionally `EXTRA_JS`).
2. Import it in `build_site.py` and add `("mypage.html", mypage)` to `PAGES`.
3. Add the link to `NAV` in `tools/common.py` (and the footer if it belongs there).
4. Run the build.

## Live data

`assets/js/site.js` calls the public Modrinth API for the project to refresh download counts, follower
count, supported game versions and the latest version. It's a progressive enhancement: if the request is
blocked or you're offline, the static numbers baked into the HTML stay on screen.

## Publishing to GitHub Pages

Copy [`tools/github-pages-workflow.yml`](github-pages-workflow.yml) to `.github/workflows/pages.yml`, then
in the repo go to **Settings → Pages → Source** and pick **GitHub Actions**. Every push that touches `docs/`
re-deploys.

(The workflow ships as a plain file rather than being committed to `.github/workflows/` on purpose — adding
files there needs a token with the `workflows` scope, which the automation here doesn't have.)

Any other static host works the same way: point it at the `docs/` folder. Netlify and Cloudflare Pages need
no configuration at all.

## Custom domain

Drop a `CNAME` file (just the hostname, e.g. `interactivestuff.dev`) into `docs/` and point your DNS at
GitHub Pages. Then update `SITE_URL` in `build_site.py` and re-run so `sitemap.xml` picks up the change.

## Content sources

- Mod description, badges and gallery: the [Modrinth project](https://modrinth.com/mod/interactivestuff).
- API reference: the [official wiki](https://github.com/omardotcontent/InteractiveStuff/wiki).
  The `ISP` type is documented from `src/main/java/.../scripts/variables/ISP.java` because it isn't on the
  wiki yet.
- Built-in interactions: the `.vyn` files in
  `src/main/resources/resourcepacks/interactive_resourcepack/assets/minecraft/scripts/`.

© 2026 Omar Mohamed. All Rights Reserved.
