#!/usr/bin/env python3
"""Shared chrome + helpers for building the InteractiveStuff static site.

The generated output in /docs is plain static HTML — you can edit those files
directly. This module only exists so the header, footer and small HTML
helpers aren't copy-pasted across eight pages.
"""

import html

# ---------------------------------------------------------------- constants
LOGO = "https://cdn.modrinth.com/data/KDfqMm8K/1983deb62ee8b3c0f8719d00bdf77f9fb32c6efd.png"
TITLECARD = "https://cdn.modrinth.com/data/KDfqMm8K/images/2714b211092aad9f171b34830bcf641bb069ca6e.png"
DESC_BANNER = "https://cdn.modrinth.com/data/cached_images/4e984962684598311a8697c7ffc65bf79e2d6ede_0.webp"
IMPORTANT_BANNER = "https://cdn.modrinth.com/data/cached_images/d00f8ef6f1c482fbc5e2e46d7c68f293b5d3b683_0.webp"
FABRIC_API = "https://cdn.modrinth.com/data/cached_images/2602ab90d17f5e3a23cd6db7b96f205cbbcb577d.png"
COLLECTION = "https://cdn.modrinth.com/data/cached_images/4153e318aba16a71f04313e31243cacc9a0a9366.png"
KOFI = "https://cdn.modrinth.com/data/cached_images/557df5e572d7235f5cee118a2935a850411c834c.png"

MODRINTH = "https://modrinth.com/mod/interactivestuff"
CURSEFORGE = "https://www.curseforge.com/minecraft/mc-mods/interactivestuff"
DISCORD = "https://discord.gg/8Ap2gGaKbw"
GITHUB = "https://github.com/omardotcontent/InteractiveStuff"
WIKI = "https://github.com/omardotcontent/InteractiveStuff/wiki"
KOFI_URL = "https://ko-fi.com/omardotcontent"
COLLECTION_URL = "https://modrinth.com/collection/DRSHO9SM"

SHIELD_DISCORD = ("https://img.shields.io/discord/1444791753529102428?style=for-the-badge&logo=Discord"
                  "&logoColor=white&label=Discord&color=728ADA")
SHIELD_CF = ("https://img.shields.io/curseforge/dt/1405017?style=for-the-badge&logo=curseforge"
             "&logoColor=white&label=curseforge&color=F16436")
SHIELD_MR = ("https://img.shields.io/modrinth/dt/interactivestuff?style=for-the-badge&logo=modrinth"
             "&logoColor=white&label=Modrinth&color=1CD96A")
SHIELD_GH = "https://img.shields.io/github/followers/omardotcontent?style=for-the-badge&logo=github&label=Github"


# ------------------------------------------------------------------ helpers
def code(src, lang="vyn", label=".vyn", copy=True):
    """A syntax-highlighted, copyable code block."""
    body = html.escape(src.strip("\n").rstrip())
    cls = "code-block" if copy else "code-block nocopy"
    return (
        f'<div class="{cls}">'
        '<div class="code-head"><span class="dots"><i></i><i></i><i></i></span>'
        f'<span class="file">{html.escape(label)}</span></div>'
        f'<pre><code class="language-{lang}">{body}</code></pre></div>'
    )


def note(text, kind="", title=""):
    """kind: '', 'warn', 'danger', 'info'"""
    cls = "note" + (f" {kind}" if kind else "")
    head = f'<span class="note-title">{html.escape(title)}</span>' if title else ""
    return f'<div class="{cls}">{head}<p>{text}</p></div>'


def btn(href, label, kind="ghost", size="", icon=""):
    cls = f"btn btn-{kind}" + (f" btn-{size}" if size else "")
    ext = ' target="_blank" rel="noopener"' if href.startswith("http") else ""
    return f'<a class="{cls}" href="{href}"{ext}>{icon}{html.escape(label)}</a>'


def icon(name, size=18):
    paths = {
        "download": '<path d="M12 3v12"/><path d="m7 12 5 5 5-5"/><path d="M4 21h16"/>',
        "book": '<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>',
        "spark": '<path d="M12 3v4M12 17v4M3 12h4M17 12h4M6.3 6.3l2.8 2.8M14.9 14.9l2.8 2.8M17.7 6.3l-2.8 2.8M9.1 14.9l-2.8 2.8"/>',
        "chest": '<path d="M3 8h18v11a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M3 8 5 3h14l2 5"/><path d="M12 8v12"/><path d="M10 12h4"/>',
        "wave": '<path d="M2 12c2-4 4-4 6 0s4 4 6 0 4-4 6 0"/>',
        "block": '<path d="M12 2 3 7v10l9 5 9-5V7z"/><path d="m12 22 9-5V7"/><path d="m3 7 9 5 9-5"/>',
        "flame": '<path d="M12 2s5 5 5 9a5 5 0 0 1-10 0c0-2 1-3 1-3s1 1 1 2c0-3 3-6 3-8z"/>',
        "piston": '<path d="M5 21h14"/><path d="M7 21V9h10v12"/><path d="M9 9V5h6v4"/><path d="M12 2v3"/>',
        "leaf": '<path d="M11 20A7 7 0 0 1 4 13c0-6 7-11 16-11 0 9-3 18-9 18z"/><path d="M4 20c2-6 6-9 12-10"/>',
        "sensor": '<circle cx="12" cy="12" r="3"/><path d="M7.8 7.8a6 6 0 0 0 0 8.4"/><path d="M16.2 16.2a6 6 0 0 0 0-8.4"/><path d="M4.9 4.9a10 10 0 0 0 0 14.2"/><path d="M19.1 19.1a10 10 0 0 0 0-14.2"/>',
        "music": '<path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/>',
        "torch": '<path d="M9 2h6v4H9z"/><path d="M10 6h4l-1 4h5l-4 12H8L4 10h5z"/>',
        "frame": '<rect x="3" y="3" width="18" height="18" rx="1"/><path d="M3 9h18M3 15h18M9 3v18M15 3v18"/>',
        "key": '<circle cx="8" cy="15" r="4"/><path d="m10.8 12.2 8.2-8.2 2 2-2 2 1.5 1.5-2.5 2.5-1.5-1.5-2 2"/>',
        "eye": '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/>',
        "wrench": '<path d="M14.7 6.3a4 4 0 0 0 5 5l-9.4 9.4a2.8 2.8 0 0 1-4-4z"/><path d="m14.7 6.3 3-3 4 4-3 3"/>',
        "shield": '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>',
    }
    d = paths.get(name, "")
    return (f'<svg viewBox="0 0 24 24" width="{size}" height="{size}" fill="none" stroke="currentColor" '
            f'stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">{d}</svg>')


def section_head(eyebrow, title, text):
    return (
        '<div class="section-head center">'
        f'<span class="eyebrow">{html.escape(eyebrow)}</span>'
        f'<h2 class="display">{title}</h2>'
        f'<p class="lead">{text}</p></div>'
    )


# ------------------------------------------------------------------- chrome
HEAD = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>__TITLE__</title>
<meta name="description" content="__DESC__">
<meta name="theme-color" content="#0b100c">
<meta property="og:type" content="website">
<meta property="og:title" content="__TITLE__">
<meta property="og:description" content="__DESC__">
<meta property="og:image" content="__TITLECARD__">
<meta name="twitter:card" content="summary_large_image">
<meta name="twitter:title" content="__TITLE__">
<meta name="twitter:description" content="__DESC__">
<meta name="twitter:image" content="__TITLECARD__">
<link rel="icon" href="__LOGO__">
<link rel="apple-touch-icon" href="__LOGO__">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Press+Start+2P&family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/css/style.css">
</head>
<body>
"""

NAV = """<header class="nav">
  <div class="wrap">
    <a class="brand" href="index.html">
      <img src="__LOGO__" alt="InteractiveStuff logo">
      <span>Interactive<em>Stuff</em></span>
    </a>
    <nav class="nav-links" id="nav-links">
      <a href="index.html">Home</a>
      <a href="features.html">Features</a>
      <a href="gallery.html">Gallery</a>
      <a href="install.html">Install</a>
      <a href="docs.html">Docs</a>
      <a href="api.html">API</a>
      <a href="physics.html">Physics</a>
      <a href="faq.html">FAQ</a>
    </nav>
    <div class="nav-actions">
      <button class="icon-btn nav-toggle" aria-label="Open menu" aria-expanded="false" aria-controls="nav-links">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M3 6h18M3 12h18M3 18h18"/></svg>
      </button>
      <button class="icon-btn" data-theme-toggle aria-label="Switch to light mode"></button>
      <a class="btn btn-primary btn-sm" href="__MODRINTH__" target="_blank" rel="noopener">Download</a>
    </div>
  </div>
</header>
<main>
"""

FOOTER = """</main>
<footer class="footer">
  <div class="wrap">
    <div class="footer-grid">
      <div>
        <a class="brand" href="index.html">
          <img src="__LOGO__" alt="InteractiveStuff logo">
          <span>Interactive<em>Stuff</em></span>
        </a>
        <p class="small muted">The First-Person Item Interactions Mod. A client-side Fabric mod for
        Minecraft 1.21.10+ that makes the items in your hand part of the world.</p>
        <div class="socials">
          <a href="__DISCORD__" target="_blank" rel="noopener" title="Discord" aria-label="Discord">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M20.32 4.37a19.79 19.79 0 0 0-4.89-1.52.07.07 0 0 0-.08.04c-.21.37-.44.86-.6 1.25a18.27 18.27 0 0 0-5.49 0c-.17-.39-.4-.88-.62-1.25a.08.08 0 0 0-.08-.04 19.74 19.74 0 0 0-4.88 1.52.07.07 0 0 0-.04.03C.53 9.05-.32 13.58.1 18.06a.08.08 0 0 0 .03.05 19.9 19.9 0 0 0 5.99 3.03.08.08 0 0 0 .09-.03c.46-.63.87-1.3 1.22-1.99a.08.08 0 0 0-.04-.11c-.65-.24-1.27-.55-1.87-.89a.08.08 0 0 1 0-.13l.37-.29a.07.07 0 0 1 .08-.01 14.2 14.2 0 0 0 12.06 0 .07.07 0 0 1 .08.01l.37.3a.08.08 0 0 1 0 .12c-.6.35-1.22.65-1.87.9a.08.08 0 0 0-.04.1c.36.7.78 1.36 1.23 1.99a.08.08 0 0 0 .08.03 19.84 19.84 0 0 0 6-3.03.08.08 0 0 0 .03-.05c.5-5.18-.84-9.68-3.55-13.66a.06.06 0 0 0-.03-.03zM8.02 15.33c-1.18 0-2.16-1.08-2.16-2.42 0-1.33.96-2.42 2.16-2.42 1.21 0 2.18 1.1 2.16 2.42 0 1.34-.96 2.42-2.16 2.42zm7.97 0c-1.18 0-2.15-1.08-2.15-2.42 0-1.33.95-2.42 2.15-2.42 1.21 0 2.18 1.1 2.16 2.42 0 1.34-.95 2.42-2.16 2.42z"/></svg>
          </a>
          <a href="__MODRINTH__" target="_blank" rel="noopener" title="Modrinth" aria-label="Modrinth">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"><path d="M12 2.5 21 7.5v9L12 21.5 3 16.5v-9z"/><path d="M8 16V8l4 5 4-5v8"/></svg>
          </a>
          <a href="__CURSEFORGE__" target="_blank" rel="noopener" title="CurseForge" aria-label="CurseForge">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"><path d="M3 9h18l-2 4H5z"/><path d="M7 13h10v3.5H7z"/><path d="M9.5 16.5h5V21h-5z"/></svg>
          </a>
          <a href="__GITHUB__" target="_blank" rel="noopener" title="GitHub" aria-label="GitHub">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 .3a12 12 0 0 0-3.8 23.4c.6.1.8-.3.8-.6v-2c-3.3.7-4-1.6-4-1.6-.6-1.4-1.4-1.8-1.4-1.8-1-.7.1-.7.1-.7 1.2.1 1.8 1.2 1.8 1.2 1.1 1.9 2.8 1.3 3.5 1 .1-.8.4-1.3.8-1.6-2.7-.3-5.5-1.3-5.5-5.9 0-1.3.5-2.4 1.2-3.2-.1-.3-.5-1.5.1-3.2 0 0 1-.3 3.3 1.2a11.5 11.5 0 0 1 6 0C17.5 4.7 18.5 5 18.5 5c.6 1.7.2 2.9.1 3.2.8.8 1.2 1.9 1.2 3.2 0 4.6-2.8 5.6-5.5 5.9.4.4.8 1.1.8 2.2v3.3c0 .3.2.7.8.6A12 12 0 0 0 12 .3"/></svg>
          </a>
          <a href="__KOFI__" target="_blank" rel="noopener" title="Ko-fi" aria-label="Ko-fi">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"><path d="M4 8h13v6a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5z"/><path d="M17 9h2a2.5 2.5 0 0 1 0 5h-2"/><path d="M7 2v2M11 2v2M15 2v2"/></svg>
          </a>
        </div>
      </div>
      <div>
        <h4>The Mod</h4>
        <a href="index.html">Home</a>
        <a href="features.html">Features</a>
        <a href="gallery.html">Gallery</a>
        <a href="install.html">Install</a>
        <a href="faq.html">FAQ</a>
      </div>
      <div>
        <h4>Documentation</h4>
        <a href="docs.html">Getting Started</a>
        <a href="api.html">Vyn API Reference</a>
        <a href="physics.html">Physics Library</a>
        <a href="__WIKI__" target="_blank" rel="noopener">Official Wiki ↗</a>
        <a href="__GITHUB__" target="_blank" rel="noopener">Source Code ↗</a>
      </div>
      <div>
        <h4>Community</h4>
        <a href="__DISCORD__" target="_blank" rel="noopener">Discord</a>
        <a href="__MODRINTH__" target="_blank" rel="noopener">Modrinth</a>
        <a href="__CURSEFORGE__" target="_blank" rel="noopener">CurseForge</a>
        <a href="__COLLECTION__" target="_blank" rel="noopener">Recommended Packs</a>
        <a href="__KOFI__" target="_blank" rel="noopener">Support on Ko-fi</a>
      </div>
    </div>
    <div class="footer-bot">
      <span>© 2026 Omar Mohamed. All Rights Reserved.</span>
      <span>GET IN TOUCH — <a href="mailto:omar@merakistudios.dev">omar@merakistudios.dev</a></span>
    </div>
  </div>
</footer>
<button id="to-top" aria-label="Back to top">
  <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M12 19V5M5 12l7-7 7 7"/></svg>
</button>
<script src="assets/js/site.js"></script>
__EXTRA_JS__
</body>
</html>
"""


def page(title, desc, body, extra_js=""):
    """Assemble a full HTML document."""
    head = (HEAD.replace("__TITLE__", html.escape(title))
                .replace("__DESC__", html.escape(desc))
                .replace("__LOGO__", LOGO)
                .replace("__TITLECARD__", TITLECARD))
    nav = NAV.replace("__LOGO__", LOGO).replace("__MODRINTH__", MODRINTH)
    foot = (FOOTER.replace("__LOGO__", LOGO)
                  .replace("__MODRINTH__", MODRINTH)
                  .replace("__CURSEFORGE__", CURSEFORGE)
                  .replace("__DISCORD__", DISCORD)
                  .replace("__GITHUB__", GITHUB)
                  .replace("__WIKI__", WIKI)
                  .replace("__KOFI__", KOFI_URL)
                  .replace("__COLLECTION__", COLLECTION_URL)
                  .replace("__EXTRA_JS__", extra_js))
    return head + nav + body + foot
