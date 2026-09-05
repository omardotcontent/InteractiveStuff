from common import *


def method(name, blurb, steps):
    lis = "".join(f"<li>{s}</li>" for s in steps)
    return (f'<div class="card fade-up"><h3>{name}</h3>'
            f'<p class="muted small">{blurb}</p>'
            f'<ol class="steps" style="margin-top:18px">{lis}</ol></div>')


TITLE = "Install — InteractiveStuff"
DESC = ("How to install InteractiveStuff: requirements, Fabric Loader setup, enabling the bundled "
        "resource pack, and troubleshooting.")
EXTRA_JS = ""

BODY = """
<section class="section" style="padding-top:56px">
  <div class="wrap">
    {head}
    <div class="grid grid-2" style="gap:36px;align-items:start">
      <div>
        <h3 class="display">1 · Check the requirements</h3>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Component</th><th>Requirement</th></tr></thead>
            <tbody>
              <tr><td><strong>Minecraft</strong></td><td>1.21.10, 1.21.11 or 26.2</td></tr>
              <tr><td>Mod loader</td><td>Fabric Loader <strong>0.18.4</strong> or newer</td></tr>
              <tr><td><strong>Fabric API</strong></td><td><span class="pill req">required</span></td></tr>
              <tr><td>VynAPI</td><td><span class="pill new">bundled</span> — included in the mod jar</td></tr>
              <tr><td>Mod Menu</td><td><span class="pill opt">optional</span> adds the config button</td></tr>
              <tr><td>YACL</td><td><span class="pill opt">optional</span> powers the config screen</td></tr>
              <tr><td>Side</td><td>Client only — the server does not need it (and can't run it)</td></tr>
            </tbody>
          </table>
        </div>
        <img src="{IMPORTANT}" alt="Important — requires Fabric API" loading="lazy"
             style="border-radius:12px;border:1px solid var(--line);max-width:340px">
      </div>
      <div>
        <h3 class="display">2 · Pick your launcher</h3>
        <p class="muted">Any Fabric-capable launcher works. Pick whichever you already use.</p>
        <div class="grid" style="gap:16px">
          {m1}
          {m2}
          {m3}
        </div>
      </div>
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap">
    <div class="grid grid-2" style="gap:40px;align-items:start">
      <div>
        <span class="eyebrow">Step 3</span>
        <h2 class="display">The resource pack</h2>
        <p>InteractiveStuff registers a <strong>built-in resource pack</strong> called
        <code>interactivestuff:interactive_resourcepack</code>. It ships <strong>enabled by default</strong>,
        so there is nothing to switch on — the thirteen built-in interactions work the moment you load into
        a world.</p>
        <p class="muted">If you ever disabled it, re-enable it from
        <em>Options → Resource Packs → Available</em> (the "packs bundled with mods" section on newer
        versions).</p>
        {note_order}
        {note_fp}
      </div>
      <div>
        <span class="eyebrow">Step 4</span>
        <h2 class="display">Configure it</h2>
        <p>With <strong>Mod Menu</strong> and <strong>YACL</strong> installed you get a config screen with
        three groups:</p>
        <ul>
          <li><strong>Scripted Packs</strong> — settings for resource pack scripts, including debug mode,
          matrix editing and colour changing.</li>
          <li><strong>Interactive Hits</strong> — hit reactions and their cooldown.</li>
          <li><strong>Config</strong> — open the config file's directory, or reload the configuration.</li>
        </ul>
        <p class="muted small">Every one of those values is readable from Vyn through the
        <a href="api.html#config"><code>config</code></a> type, so your scripts can respect the player's
        choices.</p>
        <div class="code-block">
          <div class="code-head"><span class="dots"><i></i><i></i><i></i></span><span class="file">respect_config.vyn</span></div>
          <pre><code class="language-vyn">task onItemUpdate takes itemRendered do
    check config.isResourcePackMatrixEditingEnabled() do
        itemRendered.translate(0, 0.1, 0)
    end
    check config.isResourcePackColorChangingEnabled() do
        itemRendered.setColor(255, 100, 0)
    end
end</code></pre>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="section">
  <div class="wrap">
    <div class="grid grid-2" style="gap:40px;align-items:start">
      <div>
        <span class="eyebrow">Check it works</span>
        <h2 class="display">Smoke test</h2>
        <ol>
          <li>Load a creative world and switch to <strong>first person</strong>.</li>
          <li>Grab a <strong>sapling</strong> — you should see a wood stem with leaves tinted to the biome
          you're standing in.</li>
          <li>Grab a <strong>torch</strong> and jump in water — it should go out with an extinguish sound.</li>
          <li>Grab a <strong>chest</strong> and jump off something tall — the lid should fly open.</li>
        </ol>
        <p class="muted">If none of that happens, the bundled resource pack is probably disabled or ordered
        below a pack that overrides it.</p>
      </div>
      <div>
        <span class="eyebrow">Troubleshooting</span>
        <h2 class="display">Common problems</h2>
        <div class="faq-item"><button class="faq-q" type="button">The mod won't load at all
          <span class="faq-caret">+</span></button>
          <div class="faq-a"><p>Almost always a missing <strong>Fabric API</strong>, or a version mismatch.
          Check the launcher log for <code>requires fabric-api</code>. Make sure your Fabric API build
          matches your Minecraft version.</p></div>
        </div>
        <div class="faq-item"><button class="faq-q" type="button">Nothing animates in my hand
          <span class="faq-caret">+</span></button>
          <div class="faq-a"><p>Check <em>Options → Resource Packs</em>: the InteractiveStuff pack must be
          in the <strong>selected</strong> column and, if you use other packs that import its scripts, it
          must sit <strong>below</strong> them. Also confirm you're in first person — that's where the
          effects are designed for.</p></div>
        </div>
        <div class="faq-item"><button class="faq-q" type="button">My own pack's script never runs
          <span class="faq-caret">+</span></button>
          <div class="faq-a"><p>Scripts must live at <code>assets/minecraft/scripts/*.vyn</code> in your
          pack (older tutorials say <code>assets/interactivestuff/scripts/</code> — that path changed).
          Turn on <strong>Resource Pack Debug Mode</strong> and add a <code>debugText("hello")</code> inside
          a <code>task onTick</code> to confirm the file is being read.</p></div>
        </div>
        <div class="faq-item"><button class="faq-q" type="button">Can I use this on a server?
          <span class="faq-caret">+</span></button>
          <div class="faq-a"><p>Yes — InteractiveStuff is <strong>client-side</strong>, so it works on any
          server without being installed there. It just won't do anything for other players unless they
          install it too.</p></div>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap">
    <div class="grid grid-2" style="gap:36px;align-items:center">
      <div class="card" id="version-box" hidden>
        <span class="eyebrow">Live from Modrinth</span>
        <h3 class="display">Latest release</h3>
        <div class="table-scroll" style="margin-bottom:0">
          <table>
            <tbody>
              <tr><td>Version</td><td><strong data-v-name>—</strong></td></tr>
              <tr><td>Game versions</td><td data-v-games>—</td></tr>
              <tr><td>Published</td><td data-v-date>—</td></tr>
              <tr><td>Channel</td><td data-v-type>—</td></tr>
            </tbody>
          </table>
        </div>
        <p class="muted small mb-0 mt-2">Fetched at page load. All releases are currently on the beta
        channel while 0.7 is in development.</p>
      </div>
      <div class="center">
        <span class="eyebrow">Downloads</span>
        <h2 class="display">Grab the latest</h2>
        <div class="btn-row center mt-2">
          <a class="btn btn-primary btn-lg" href="{MODRINTH}" target="_blank" rel="noopener">{i_dl} Modrinth</a>
          <a class="btn btn-orange btn-lg" href="{CURSEFORGE}" target="_blank" rel="noopener">{i_cf} CurseForge</a>
        </div>
        <p class="muted small mt-3">Need help? Ask on <a href="{DISCORD}" target="_blank" rel="noopener">Discord</a>
        or open an issue on <a href="{GITHUB}" target="_blank" rel="noopener">GitHub</a>.</p>
      </div>
    </div>
  </div>
</section>
""".format(
    head=section_head("Install", "Two minutes, three steps",
                      "InteractiveStuff is a client-side Fabric mod. Install it like any other Fabric mod, "
                      "make sure the bundled resource pack is enabled, and you're done."),
    IMPORTANT=IMPORTANT_BANNER,
    MODRINTH=MODRINTH, CURSEFORGE=CURSEFORGE, DISCORD=DISCORD, GITHUB=GITHUB,
    i_dl=icon("download"), i_cf=icon("flame"),

    m1=method("Modrinth App", "The fastest route — it handles Fabric and dependencies for you.",
              ["Download and install the <a href=\"https://modrinth.com/app\" target=\"_blank\" rel=\"noopener\">Modrinth App</a>.",
               "Create a new instance (or pick an existing one) on Minecraft 1.21.10 or newer.",
               "Search for <strong>InteractiveStuff</strong> and install it — Fabric API is pulled in automatically."]),
    m2=method("CurseForge / Prism / ATL", "Standard mod-folder install.",
              ["Download the jar from <a href=\"%s\" target=\"_blank\" rel=\"noopener\">CurseForge</a> or Modrinth." % CURSEFORGE,
               "Make sure <strong>Fabric API</strong> is installed for your Minecraft version.",
               "Drop <code>interactivestuff-&lt;version&gt;.jar</code> into your instance's <code>mods</code> folder."]),
    m3=method("Manual", "For vanilla launcher users with Fabric already set up.",
              ["Install <a href=\"https://fabricmc.net/use/installer/\" target=\"_blank\" rel=\"noopener\">Fabric Loader</a> 0.18.4+.",
               "Download <strong>Fabric API</strong> and InteractiveStuff.",
               "Put both jars in <code>.minecraft/mods</code> and launch the Fabric profile."]),

    note_order=note("Always keep the <strong>InteractiveStuff resource pack at the bottom</strong> of the "
                    "selected-packs list when another pack imports its script libraries — otherwise that "
                    "pack can't see them.", "warn", "Pack order matters"),
    note_fp=note("First-person mode is strongly recommended for the best experience. This is a "
                 "first-person <em>item interactions</em> mod, not a first-person animation mod.",
                 "info", "Perspective"),
)
