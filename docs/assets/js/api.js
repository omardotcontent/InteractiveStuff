/* ==========================================================================
   InteractiveStuff — API reference renderer (api.html)
   Renders window.IS_API (api-data.js) into a searchable, filterable reference.
   ========================================================================== */
(function () {
  "use strict";

  var data = window.IS_API;
  var mount = document.getElementById("api-mount");
  if (!data || !mount) return;

  var search = document.getElementById("api-search");
  var chipsBox = document.getElementById("api-chips");
  var countEl = document.getElementById("api-count");
  var sideBox = document.getElementById("api-side");

  function esc(s) {
    return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
  }
  function slug(s) {
    return String(s).toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
  }

  /* ---------- Build the model ---------- */
  var sections = [];

  data.types.forEach(function (t) {
    var groups = t.groups.map(function (g) {
      return { title: g.title, items: g.items.map(function (it) { return { item: it, sec: t.id, secName: t.name }; }) };
    });
    if (t.ctor) {
      groups.unshift({
        title: "Constructor",
        items: [{ item: t.ctor, sec: t.id, secName: t.name, isCtor: true }]
      });
    }
    sections.push({
      id: t.id,
      name: t.name,
      varName: t.varName,
      blurb: t.blurb,
      note: t.note || "",
      kind: "type",
      groups: groups
    });
  });

  [["functions", "Global Functions", "Available globally in every script, no type instance required."],
   ["statements", "Statements", "Control-flow statements provided by the Vyn language."],
   ["events", "Events", "Special tasks Vyn calls automatically. Define them in your script with task."]
  ].forEach(function (triple) {
    sections.push({
      id: "global-" + triple[0],
      name: triple[1],
      varName: "",
      blurb: triple[2],
      kind: "global",
      groups: [{ title: "", items: (data.globals[triple[0]] || []).map(function (it) {
        return { item: it, sec: "global-" + triple[0], secName: triple[1] };
      }) }]
    });
  });

  /* ---------- Sidebar ---------- */
  if (sideBox) {
    var html = '<h4>Types</h4>';
    sections.filter(function (s) { return s.kind === "type"; }).forEach(function (s) {
      html += '<a href="#' + s.id + '">' + esc(s.name) + "</a>";
    });
    html += "<h4>Globals</h4>";
    sections.filter(function (s) { return s.kind === "global"; }).forEach(function (s) {
      html += '<a href="#' + s.id + '">' + esc(s.name) + "</a>";
    });
    html += '<h4>More</h4><a href="docs.html">Getting Started</a><a href="physics.html">Physics Library</a><a href="https://github.com/omardotcontent/InteractiveStuff/wiki" target="_blank" rel="noopener">Official Wiki ↗</a>';
    sideBox.innerHTML = html;
  }

  /* ---------- Chips ---------- */
  var active = "all";
  if (chipsBox) {
    var c = '<button class="chip active" data-sec="all">All</button>';
    sections.forEach(function (s) {
      c += '<button class="chip" data-sec="' + s.id + '">' + esc(s.name) + "</button>";
    });
    chipsBox.innerHTML = c;
    chipsBox.addEventListener("click", function (e) {
      var b = e.target.closest(".chip");
      if (!b) return;
      active = b.getAttribute("data-sec");
      chipsBox.querySelectorAll(".chip").forEach(function (x) { x.classList.toggle("active", x === b); });
      render();
    });
  }

  /* ---------- Render ---------- */
  var query = "";

  function matches(entry, sec, q) {
    if (!q) return true;
    var hay = [entry.s, entry.d, entry.r, sec.name, sec.id, entry.p.map(function (p) { return p.n + " " + p.t + " " + p.d; }).join(" ")].join(" ").toLowerCase();
    return q.split(/\s+/).every(function (term) { return hay.indexOf(term) !== -1; });
  }

  function entryHtml(wrapped, openAll) {
    var it = wrapped.item;
    var id = slug(wrapped.sec) + "-" + slug(it.s);
    var h = '<div class="entry' + (openAll ? " open" : "") + '" id="' + id + '" data-search="' +
      esc((it.s + " " + it.d).toLowerCase()) + '">';
    h += '<button class="entry-head" type="button" aria-expanded="' + (openAll ? "true" : "false") + '">';
    h += '<svg class="entry-caret" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>';
    h += '<span class="entry-sig">' + esc(it.s) + "</span>";
    if (it.r) h += '<span class="entry-ret">' + esc(it.r) + "</span>";
    h += "</button>";
    h += '<div class="entry-body">';
    if (it.d) h += "<p>" + esc(it.d) + "</p>";
    if (it.p && it.p.length) {
      h += '<div class="entry-meta"><div><b>Parameters</b></div></div><ul class="param-list">';
      it.p.forEach(function (p) {
        h += "<li><code>" + esc(p.n) + "</code> — " + esc(p.t) + (p.d ? " — " + esc(p.d) : "") + "</li>";
      });
      h += "</ul>";
    }
    if (it.r) {
      h += '<div class="entry-meta"><div><b>Returns</b> <code>' + esc(it.r) + "</code></div></div>";
    }
    if (it.e) {
      h += '<div class="code-block"><div class="code-head"><span class="dots"><i></i><i></i><i></i></span><span class="file">.vyn</span></div><pre><code class="language-vyn">' + esc(it.e) + "</code></pre></div>";
    }
    h += "</div></div>";
    return h;
  }

  function render() {
    var q = query.trim().toLowerCase();
    var openAll = q.length > 0;
    var total = 0;
    var visibleSections = 0;
    var html = "";

    sections.forEach(function (s) {
      if (active !== "all" && s.id !== active) return;
      var groupsHtml = "";
      var secCount = 0;
      s.groups.forEach(function (g) {
        var itemsHtml = "";
        g.items.forEach(function (w) {
          if (!matches(w.item, s, q)) return;
          itemsHtml += entryHtml(w, openAll);
          secCount++;
          total++;
        });
        if (itemsHtml) {
          groupsHtml += g.title ? '<div class="api-group-title">' + esc(g.title) + "</div>" : "";
          groupsHtml += itemsHtml;
        }
      });
      if (!groupsHtml) return;
      visibleSections++;
      html += '<section class="api-type" id="' + s.id + '">';
      html += '<div class="api-type-head"><h2>' + esc(s.name) + "</h2>";
      if (s.varName) html += '<span class="var-name">' + esc(s.varName) + "</span>";
      html += "</div>";
      html += "<p>" + esc(s.blurb) + "</p>";
      if (s.note) html += '<div class="note info"><span class="note-title">Note</span><p>' + esc(s.note) + "</p></div>";
      html += groupsHtml;
      html += "</section>";
    });

    if (!total) {
      html = '<div class="empty-state"><h3 class="display">Nothing found</h3><p>No function matches “' + esc(query) + '”. Try a shorter term, or press <kbd>Esc</kbd> to clear.</p></div>';
    }
    mount.innerHTML = html;

    if (countEl) {
      countEl.textContent = total
        ? total + " result" + (total === 1 ? "" : "s") + " in " + visibleSections + " section" + (visibleSections === 1 ? "" : "s")
        : "";
    }

    /* Expand / collapse */
    mount.querySelectorAll(".entry-head").forEach(function (head) {
      head.addEventListener("click", function () {
        var entry = head.parentElement;
        var open = entry.classList.toggle("open");
        head.setAttribute("aria-expanded", String(open));
      });
    });

    /* Re-run the shared helpers for freshly injected markup */
    if (window.ISHighlight) window.ISHighlight(mount);
    if (window.ISAddAnchors) window.ISAddAnchors(mount);
  }

  /* ---------- Search wiring ---------- */
  if (search) {
    var timer;
    search.addEventListener("input", function () {
      clearTimeout(timer);
      timer = setTimeout(function () { query = search.value; render(); }, 110);
    });
    search.addEventListener("keydown", function (e) {
      if (e.key === "Escape") { search.value = ""; query = ""; render(); search.blur(); }
    });
  }
  document.addEventListener("keydown", function (e) {
    if (e.key === "/" && document.activeElement !== search && search) {
      var tag = (document.activeElement.tagName || "").toLowerCase();
      if (tag === "input" || tag === "textarea") return;
      e.preventDefault();
      search.focus();
      search.select();
    }
  });

  /* ---------- Deep links ---------- */
  function applyHash() {
    var h = decodeURIComponent((location.hash || "").replace(/^#/, ""));
    if (!h) return;
    var el = document.getElementById(h);
    if (!el) return;
    if (el.classList.contains("entry")) {
      el.classList.add("open");
      var head = el.querySelector(".entry-head");
      if (head) head.setAttribute("aria-expanded", "true");
    }
    setTimeout(function () { el.scrollIntoView({ behavior: "smooth", block: "start" }); }, 60);
  }

  render();
  window.addEventListener("hashchange", applyHash);
  applyHash();
})();
