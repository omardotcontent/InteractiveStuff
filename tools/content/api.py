from common import *

TITLE = "Vyn API Reference — InteractiveStuff"
DESC = ("The complete Vyn API reference: every type and function available to InteractiveStuff resource "
        "pack scripts, with parameters, return types and copyable examples.")
EXTRA_JS = ('<script src="assets/js/api-data.js"></script>\n'
            '<script src="assets/js/api.js"></script>')

BODY = """
<section class="section" style="padding-top:44px">
  <div class="wrap">
    <div class="docs-layout">

      <aside class="docs-side" id="api-side"></aside>

      <div class="docs-main">
        <span class="eyebrow">Reference</span>
        <h2 class="display mt-0">Vyn API</h2>
        <p class="lead">Every type, function, statement and event available to a Vyn script. Press
        <kbd>/</kbd> to search, or click any entry to expand its parameters and examples.</p>

        <div class="api-toolbar">
          <div class="search-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
            <input id="api-search" type="search" placeholder="Search functions, types, parameters…"
                   autocomplete="off" spellcheck="false" aria-label="Search the API">
          </div>
          <div class="chips" id="api-chips"></div>
          <div class="result-count" id="api-count"></div>
        </div>

        <div id="api-mount"></div>

        <div class="note info">
          <span class="note-title">Source of truth</span>
          <p>This reference mirrors the
          <a href="__WIKI__" target="_blank" rel="noopener">official wiki</a>. The
          <code>ISP</code> type is documented from the mod source because it isn't on the wiki yet, even
          though the bundled pack relies on it. If the two ever disagree, trust the mod.</p>
        </div>
      </div>
    </div>
  </div>
</section>
""".replace("__WIKI__", WIKI)
