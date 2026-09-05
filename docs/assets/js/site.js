/* ==========================================================================
   InteractiveStuff — shared site behaviour
   Vanilla JS, no dependencies.
   ========================================================================== */
(function () {
  "use strict";

  /* ---------------- Theme ---------------- */
  var THEME_KEY = "is-theme";
  function applyTheme(t) {
    document.documentElement.setAttribute("data-theme", t);
    var btn = document.querySelector("[data-theme-toggle]");
    if (btn) {
      btn.setAttribute("aria-label", t === "dark" ? "Switch to light mode" : "Switch to dark mode");
      btn.innerHTML = t === "dark" ? ICON.sun : ICON.moon;
    }
  }
  (function initTheme() {
    var saved = null;
    try { saved = localStorage.getItem(THEME_KEY); } catch (e) {}
    if (!saved) {
      saved = window.matchMedia && window.matchMedia("(prefers-color-scheme: light)").matches ? "light" : "dark";
    }
    applyTheme(saved);
  })();

  var ICON = {
    sun: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>',
    moon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z"/></svg>'
  };

  document.addEventListener("click", function (e) {
    var t = e.target.closest("[data-theme-toggle]");
    if (!t) return;
    var cur = document.documentElement.getAttribute("data-theme") === "light" ? "light" : "dark";
    var next = cur === "light" ? "dark" : "light";
    applyTheme(next);
    try { localStorage.setItem(THEME_KEY, next); } catch (err) {}
  });

  /* ---------------- Mobile nav ---------------- */
  var toggle = document.querySelector(".nav-toggle");
  var links = document.querySelector(".nav-links");
  if (toggle && links) {
    toggle.addEventListener("click", function () {
      var open = links.classList.toggle("open");
      toggle.setAttribute("aria-expanded", String(open));
    });
    links.addEventListener("click", function (e) {
      if (e.target.tagName === "A") {
        links.classList.remove("open");
        toggle.setAttribute("aria-expanded", "false");
      }
    });
  }

  /* ---------------- Active nav link ---------------- */
  (function markActive() {
    var here = location.pathname.split("/").pop() || "index.html";
    document.querySelectorAll(".nav-links a").forEach(function (a) {
      var href = a.getAttribute("href");
      if (href === here) {
        a.classList.add("active");
        a.setAttribute("aria-current", "page");
      }
    });
  })();

  /* ---------------- Docs sidebar scroll-spy ---------------- */
  var sideLinks = Array.prototype.slice.call(document.querySelectorAll(".docs-side a[href^='#']"));
  var spyTargets = sideLinks.map(function (a) { return document.getElementById(a.getAttribute("href").slice(1)); });
  function spy() {
    if (!sideLinks.length) return;
    var best = -1, bestTop = -Infinity, y = window.scrollY + 140;
    spyTargets.forEach(function (el, i) {
      if (!el) return;
      var top = el.getBoundingClientRect().top + window.scrollY;
      if (top <= y && top > bestTop) { bestTop = top; best = i; }
    });
    sideLinks.forEach(function (a, i) { a.classList.toggle("active", i === best); });
  }

  /* ---------------- Sticky nav shadow + back to top ---------------- */
  var nav = document.querySelector(".nav");
  var toTop = document.getElementById("to-top");
  function onScroll() {
    if (nav) nav.classList.toggle("scrolled", window.scrollY > 8);
    if (toTop) toTop.classList.toggle("show", window.scrollY > 500);
    spy();
  }
  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();
  if (toTop) toTop.addEventListener("click", function () { window.scrollTo({ top: 0, behavior: "smooth" }); });

  /* ---------------- Anchor links on headings ---------------- */
  function addAnchors(root) {
    (root || document).querySelectorAll(".docs-main h2[id], .docs-main h3[id], .api-type h2[id]").forEach(function (h) {
      if (h.querySelector(".anchor-link")) return;
      var a = document.createElement("a");
      a.className = "anchor-link";
      a.href = "#" + h.id;
      a.textContent = "#";
      a.setAttribute("aria-label", "Link to this section");
      h.appendChild(a);
    });
  }
  addAnchors(document);
  window.ISAddAnchors = addAnchors;

  /* ---------------- Code blocks: highlight + copy ---------------- */
  var KW = ["make", "check", "do", "end", "otherwise", "task", "takes", "reply", "lock", "cycle",
    "from", "to", "escape", "wait", "new", "nothing", "true", "false"];
  var GLOBALS = ["player", "world", "modLoader", "config", "isp", "debugText", "getDelta",
    "importScript", "excludeScript", "runPhysicsEngine", "angleX", "angleZ", "random", "time",
    "sin", "cos", "replace", "size"];
  var kwRe = new RegExp("^(" + KW.join("|") + ")$");
  var glRe = new RegExp("^(" + GLOBALS.join("|") + ")$");

  function esc(s) {
    return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
  }

  function highlightVyn(src) {
    var out = "";
    var re = /(~[^\n]*)|("(?:[^"\\]|\\.)*")|(\b\d+(?:\.\d+)?\b)|([A-Za-z_][A-Za-z0-9_]*)/g;
    var last = 0, m;
    while ((m = re.exec(src)) !== null) {
      out += esc(src.slice(last, m.index));
      last = re.lastIndex;
      if (m[1]) {
        out += '<span class="tok-com">' + esc(m[1]) + "</span>";
      } else if (m[2]) {
        out += '<span class="tok-str">' + esc(m[2]) + "</span>";
      } else if (m[3]) {
        out += '<span class="tok-num">' + esc(m[3]) + "</span>";
      } else {
        var w = m[4];
        var after = src.slice(re.lastIndex, re.lastIndex + 1);
        var cls = "";
        if (kwRe.test(w)) cls = "tok-kw";
        else if (after === "(") cls = "tok-fn";
        else if (/^[A-Z]/.test(w)) cls = "tok-type";
        else if (glRe.test(w)) cls = "tok-gl";
        out += cls ? '<span class="' + cls + '">' + esc(w) + "</span>" : esc(w);
      }
    }
    out += esc(src.slice(last));
    return out;
  }

  function highlightJson(src) {
    return esc(src)
      .replace(/&quot;([^&]*?)&quot;(\s*:)/g, '<span class="tok-fn">&quot;$1&quot;</span>$2')
      .replace(/:\s*&quot;([^&]*?)&quot;/g, ': <span class="tok-str">&quot;$1&quot;</span>')
      .replace(/\b(-?\d+(?:\.\d+)?)\b/g, '<span class="tok-num">$1</span>');
  }

  function setupCodeBlocks(root) {
  (root || document).querySelectorAll(".code-block").forEach(function (block) {
    var code = block.querySelector("code");
    if (!code) return;
    if (!code.getAttribute("data-hl")) {
      var lang = (code.className.match(/language-([\w-]+)/) || [, "vyn"])[1];
      var raw = code.textContent.replace(/\s+$/, "");
      if (lang === "json") code.innerHTML = highlightJson(raw);
      else if (lang === "vyn") code.innerHTML = highlightVyn(raw);
      code.setAttribute("data-hl", "1");
    }

    var btn = document.createElement("button");
    btn.className = "copy-btn";
    btn.type = "button";
    btn.textContent = "Copy";
    btn.addEventListener("click", function () {
      var text = code.textContent;
      var done = function () {
        btn.textContent = "Copied!";
        btn.classList.add("ok");
        setTimeout(function () { btn.textContent = "Copy"; btn.classList.remove("ok"); }, 1600);
      };
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(done, fallback);
      } else { fallback(); }
      function fallback() {
        var ta = document.createElement("textarea");
        ta.value = text;
        ta.setAttribute("readonly", "");
        ta.style.position = "fixed";
        ta.style.opacity = "0";
        document.body.appendChild(ta);
        ta.select();
        try { document.execCommand("copy"); done(); } catch (e) {}
        document.body.removeChild(ta);
      }
    });
    var head = block.querySelector(".code-head");
    if (head) head.appendChild(btn);
    else block.insertBefore(btn, code.parentNode);
  });
  }
  setupCodeBlocks(document);
  window.ISHighlight = setupCodeBlocks;

  /* ---------------- FAQ accordion ---------------- */
  document.querySelectorAll(".faq-q").forEach(function (q) {
    q.addEventListener("click", function () {
      q.parentElement.classList.toggle("open");
      q.setAttribute("aria-expanded", String(q.parentElement.classList.contains("open")));
    });
  });

  /* ---------------- Fade-up on scroll ---------------- */
  var fadeEls = document.querySelectorAll(".fade-up");
  if ("IntersectionObserver" in window && fadeEls.length) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (en) {
        if (en.isIntersecting) { en.target.classList.add("in"); io.unobserve(en.target); }
      });
    }, { rootMargin: "0px 0px -60px 0px", threshold: 0.05 });
    fadeEls.forEach(function (el) { io.observe(el); });
  } else {
    fadeEls.forEach(function (el) { el.classList.add("in"); });
  }

  /* ---------------- Lightbox (gallery) ---------------- */
  var lb = document.getElementById("lightbox");
  if (lb) {
    document.querySelectorAll(".shot").forEach(function (shot) {
      shot.addEventListener("click", function () {
        var img = shot.querySelector("img");
        var cap = shot.querySelector(".cap");
        lb.querySelector("img").src = img.getAttribute("data-full") || img.src;
        lb.querySelector("img").alt = img.alt || "";
        lb.querySelector(".lb-cap").innerHTML =
          "<b>" + (cap ? cap.querySelector("b").textContent : "") + "</b>" +
          (cap && cap.querySelector("span") ? "<br>" + cap.querySelector("span").textContent : "");
        lb.classList.add("open");
        document.body.style.overflow = "hidden";
      });
    });
    var close = function () { lb.classList.remove("open"); document.body.style.overflow = ""; };
    lb.querySelector(".lb-close").addEventListener("click", close);
    lb.addEventListener("click", function (e) { if (e.target === lb) close(); });
    document.addEventListener("keydown", function (e) { if (e.key === "Escape") close(); });
  }

  /* ---------------- Live stats from the Modrinth API ---------------- */
  var statNodes = document.querySelectorAll("[data-stat]");
  if (statNodes.length) {
    fetch("https://api.modrinth.com/v2/project/interactivestuff", { headers: { Accept: "application/json" } })
      .then(function (r) { if (!r.ok) throw new Error("bad status"); return r.json(); })
      .then(function (d) {
        var map = {
          downloads: d.downloads,
          followers: d.followers,
          versions: (d.game_versions || []).join(" / "),
          updated: new Date(d.updated).toISOString().slice(0, 10),
          license: (d.license && (d.license.name || d.license.id)) || "ARR"
        };
        statNodes.forEach(function (n) {
          var key = n.getAttribute("data-stat");
          if (map[key] === undefined || map[key] === null) return;
          var val = map[key];
          if (key === "downloads" || key === "followers") {
            animateNumber(n, val, key === "downloads");
          } else {
            n.textContent = val;
          }
          n.setAttribute("data-live", "true");
        });
      })
      .catch(function () { /* offline / blocked — keep the static numbers in the HTML */ });
  }

  function animateNumber(node, target, comma) {
    var from = parseInt(String(node.textContent).replace(/[^\d]/g, ""), 10) || 0;
    if (!isFinite(target)) return;
    if (from === target) { node.textContent = fmt(target, comma); return; }
    var start = performance.now(), dur = 900;
    function step(now) {
      var p = Math.min(1, (now - start) / dur);
      var eased = 1 - Math.pow(1 - p, 3);
      node.textContent = fmt(Math.round(from + (target - from) * eased), comma);
      if (p < 1) requestAnimationFrame(step);
    }
    requestAnimationFrame(step);
  }
  function fmt(n, comma) {
    return comma ? n.toLocaleString("en-US") : String(n);
  }

  /* ---------------- Version list from Modrinth ---------------- */
  var vb = document.getElementById("version-box");
  if (vb) {
    fetch("https://api.modrinth.com/v2/project/interactivestuff/version")
      .then(function (r) { return r.ok ? r.json() : Promise.reject(); })
      .then(function (list) {
        var v = (list || [])[0];
        if (!v) return;
        var set = function (sel, txt) { var el = vb.querySelector(sel); if (el) el.textContent = txt; };
        set("[data-v-name]", v.name || v.version_number);
        set("[data-v-games]", (v.game_versions || []).join(", "));
        set("[data-v-date]", new Date(v.date_published).toISOString().slice(0, 10));
        set("[data-v-type]", v.version_type || "");
        vb.hidden = false;
      })
      .catch(function () {});
  }
})();
