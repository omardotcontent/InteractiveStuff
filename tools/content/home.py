from common import *


def card(ic, title, text):
    return (f'<div class="card hoverable fade-up"><div class="card-icon">{icon(ic, 24)}</div>'
            f'<h3>{title}</h3><p class="muted small mb-0">{text}</p></div>')


def shot(src, full, title, caption, tag=""):
    t = f'<span class="tag">{tag}</span>' if tag else ""
    return (f'<figure class="shot fade-up" style="margin:0">{t}'
            f'<img src="{src}" data-full="{full}" alt="{title}" loading="lazy">'
            f'<figcaption class="cap"><b>{title}</b><span>{caption}</span></figcaption></figure>')


TITLE = "InteractiveStuff — The First-Person Item Interactions Mod"
DESC = ("InteractiveStuff is a client-side Fabric mod for Minecraft 1.21.10+ that adds immersive, "
        "interactive behaviours to items and blocks in first-person view — animated held items, "
        "reactive note blocks, sculk sensors, lanterns, and a built-in Vyn scripting engine.")
EXTRA_JS = ""

BODY = """
<section class="hero">
  <div class="hero-bg"></div>
  <div class="wrap center">
    <img class="hero-logo" src="{LOGO}" alt="InteractiveStuff logo">
    <span class="eyebrow">Fabric · Client-side · Minecraft 1.21.10+</span>
    <h1 class="display">Interactive<span class="accent">Stuff</span></h1>
    <p class="tagline">The First-Person Item Interactions Mod. Chests that open as you fall, torches that
    drown underwater, saplings tinted by the biome you stand in — and a scripting engine that lets any
    resource pack rewrite all of it without touching Java.</p>
    <div class="btn-row center">
      <a class="btn btn-primary btn-lg" href="{MODRINTH}" target="_blank" rel="noopener">{i_dl} Download on Modrinth</a>
      <a class="btn btn-orange btn-lg" href="{CURSEFORGE}" target="_blank" rel="noopener">{i_cf} CurseForge</a>
      <a class="btn btn-ghost btn-lg" href="docs.html">{i_book} Documentation</a>
    </div>
    <div class="badges">
      <a href="{DISCORD}" target="_blank" rel="noopener"><img src="{SHIELD_DISCORD}" alt="Discord server" loading="lazy"></a>
      <a href="{CURSEFORGE}" target="_blank" rel="noopener"><img src="{SHIELD_CF}" alt="CurseForge downloads" loading="lazy"></a>
      <a href="{MODRINTH}" target="_blank" rel="noopener"><img src="{SHIELD_MR}" alt="Modrinth downloads" loading="lazy"></a>
      <a href="{GITHUB}" target="_blank" rel="noopener"><img src="{SHIELD_GH}" alt="GitHub followers" loading="lazy"></a>
    </div>
    <div class="stats">
      <div class="stat"><b data-stat="downloads">407,614</b><span>Downloads</span></div>
      <div class="stat"><b data-stat="followers">201</b><span>Followers</span></div>
      <div class="stat"><b>0.7.0</b><span>Latest Version</span></div>
      <div class="stat"><b class="sm" data-stat="versions">1.21.10 / 1.21.11 / 26.2</b><span>Game Versions</span></div>
      <div class="stat"><b>Client</b><span>Side</span></div>
    </div>
  </div>
</section>

<div class="grass-divider"></div>

<section class="section">
  <div class="wrap">
    <div class="grid grid-2" style="align-items:center;gap:44px">
      <div>
        <span class="eyebrow">What is it?</span>
        <h2 class="display">Your hands are part of the world now</h2>
        <p>InteractiveStuff is a <strong>client-side</strong> Fabric mod for Minecraft <strong>1.21.10+</strong>
        that adds immersive, interactive behaviours to items and blocks in first-person view — animated held
        items, reactive note blocks, sculk sensors, lanterns, and much more.</p>
        <p>Everything the mod does lives in a bundled resource pack written in <strong>Vyn</strong>, the
        scripting language that ships with InteractiveStuff 0.7. That means the built-in behaviour is
        readable, remixable, and completely overridable — and your own pack can do the same things with a
        single <code>.vyn</code> file.</p>
        <div class="note info"><p><strong>First-person mode is strongly recommended</strong> for the best
        experience. This is not a first-person <em>animation</em> mod — it is a first-person item
        <em>interactions</em> mod.</p></div>
        <div class="btn-row">
          <a class="btn btn-ghost btn-sm" href="features.html">See every feature →</a>
          <a class="btn btn-ghost btn-sm" href="gallery.html">Screenshots →</a>
        </div>
      </div>
      <div>
        <img src="{DESC_BANNER}" alt="InteractiveStuff showcase banner" loading="lazy"
             style="border-radius:14px;border:1px solid var(--line)">
      </div>
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap">
    {head}
    <div class="grid grid-3">
      {card_chest}
      {card_redstone}
      {card_tint}
      {card_sensor}
      {card_physics}
      {card_script}
    </div>
    <div class="center mt-3">
      <a class="btn btn-ghost" href="features.html">All 13 built-in interactions →</a>
    </div>
  </div>
</section>

<section class="section">
  <div class="wrap">
    <div class="grid grid-2" style="align-items:center;gap:44px">
      <div>
        <div class="code-block">
          <div class="code-head"><span class="dots"><i></i><i></i><i></i></span><span class="file">sapling.vyn</span></div>
          <pre><code class="language-vyn">task onItemUpdate takes itemRendered do
    check player.getGameMode() != "spectator" do
        check isItemSapling(itemRendered) do
            make name = replace(itemRendered.getName(), "minecraft:", "")
            itemRendered.setItemModel("interactivestuff:" + name + "_wood")

            make saplingLeaves = new ItemModel("minecraft:oak_sapling")
            saplingLeaves.setItemModel("interactivestuff:" + name + "_leaves")
            saplingLeaves.setTint(0, player.getWorld().getFoliageColor(
                player.getSteppingBlock().getPosition()))
        end
    end
end</code></pre>
        </div>
      </div>
      <div>
        <span class="eyebrow">Vyn scripting</span>
        <h2 class="display">Real scripts. No Java.</h2>
        <p class="lead">InteractiveStuff integrates <a href="https://github.com/Abdelaziz1586/Vyn" target="_blank" rel="noopener">Vyn</a>,
        a scripting engine that lets resource pack creators script item rendering and behaviour any way they
        want. That snippet is the actual, shipped sapling script — the whole file is 16 lines.</p>
        <p>You get typed access to the player, the world, blocks, sounds, positions and the mod's own config,
        plus full transform, colour, lighting and data-component control over any rendered item model.</p>
        <div class="btn-row">
          <a class="btn btn-primary" href="docs.html">Get started</a>
          <a class="btn btn-ghost" href="api.html">Browse the API</a>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap">
    {gallery_head}
    <div class="grid grid-3">
      {shot1}
      {shot2}
      {shot3}
    </div>
    <div class="center mt-3"><a class="btn btn-ghost" href="gallery.html">Open the gallery →</a></div>
  </div>
</section>

<section class="section">
  <div class="wrap">
    <div class="grid grid-2" style="gap:44px">
      <div class="pixel-panel">
        <span class="eyebrow">Before you install</span>
        <h3 class="display">Fabric API is required</h3>
        <p class="muted small">InteractiveStuff is a Fabric mod and will not load without it.</p>
        <a href="https://modrinth.com/mod/fabric-api" target="_blank" rel="noopener">
          <img src="{FABRIC_API}" alt="Fabric API" loading="lazy" style="width:60%;margin:8px auto 18px">
        </a>
        <ul class="small" style="padding-left:1.1em">
          <li><span class="pill req">required</span> <strong>Fabric API</strong></li>
          <li><span class="pill req">required</span> <strong>Fabric Loader</strong> 0.18.4+</li>
          <li><span class="pill opt">optional</span> <strong>Mod Menu</strong> — in-game config entry</li>
          <li><span class="pill opt">optional</span> <strong>YACL</strong> — the config screen itself</li>
        </ul>
        <div class="mt-2"><a class="btn btn-primary btn-sm" href="install.html">Install guide →</a></div>
      </div>
      <div class="pixel-panel">
        <span class="eyebrow">Works great with</span>
        <h3 class="display">Recommended packs &amp; mods</h3>
        <p class="muted small">A curated Modrinth collection of resource packs that play nicely with
        InteractiveStuff — including packs that import its script libraries.</p>
        <a href="{COLLECTION_URL}" target="_blank" rel="noopener">
          <img src="{COLLECTION}" alt="Recommended Modrinth collection" loading="lazy"
               style="width:100%;margin:8px auto 18px;border-radius:10px">
        </a>
        {note_order}
      </div>
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap center">
    <span class="eyebrow">Ready?</span>
    <h2 class="display">Make your inventory come alive</h2>
    <p class="lead">One jar, client-side, no server required.</p>
    <div class="btn-row center mt-2">
      <a class="btn btn-primary btn-lg" href="{MODRINTH}" target="_blank" rel="noopener">{i_dl} Modrinth</a>
      <a class="btn btn-orange btn-lg" href="{CURSEFORGE}" target="_blank" rel="noopener">{i_cf} CurseForge</a>
      <a class="btn btn-blue btn-lg" href="{DISCORD}" target="_blank" rel="noopener">Join the Discord</a>
    </div>
    <p class="muted small mt-3">Published 2 December 2025 · Updated <span data-stat="updated">recently</span> · All Rights Reserved</p>
    <a href="{KOFI_URL}" target="_blank" rel="noopener"><img src="{KOFI}" alt="Support on Ko-fi" loading="lazy" style="width:96px;margin:10px auto 0"></a>
  </div>
</section>
""".format(
    LOGO=LOGO, MODRINTH=MODRINTH, CURSEFORGE=CURSEFORGE, DISCORD=DISCORD, GITHUB=GITHUB,
    SHIELD_DISCORD=SHIELD_DISCORD, SHIELD_CF=SHIELD_CF, SHIELD_MR=SHIELD_MR, SHIELD_GH=SHIELD_GH,
    DESC_BANNER=DESC_BANNER, FABRIC_API=FABRIC_API, COLLECTION=COLLECTION, COLLECTION_URL=COLLECTION_URL,
    KOFI_URL=KOFI_URL, KOFI=KOFI,
    i_dl=icon("download"), i_cf=icon("flame"), i_book=icon("book"),

    head=section_head("Highlights", "Six things it does out of the box",
                      "Every one of these ships in the bundled resource pack — and every one is a plain "
                      "Vyn script you can read, copy or replace."),

    card_chest=card("chest", "Chests that open when you fall",
                    "Chests, ender chests, trapped chests and all four copper variants get a separate lid "
                    "model that swings open proportionally to your fall speed, eased with a spring."),
    card_redstone=card("piston", "Redstone you can feel",
                       "Sneak on a redstone block to extend a piston (with real piston sounds), light a "
                       "redstone lamp to full brightness, or flip a redstone torch off."),
    card_tint=card("leaf", "Biome-tinted everything",
                   "Saplings split into a wood stem plus leaves tinted by the foliage colour of the biome "
                   "you're standing in. Water buckets carry your local water colour."),
    card_sensor=card("sensor", "Sound-aware sculk sensors",
                     "Hold a sensor and it reacts to your landings and to sounds playing within 8 blocks "
                     "(16 for calibrated). Wool underfoot or sneaking keeps you quiet."),
    card_physics=card("wave", "A physics engine, included",
                      "A spring-damper simulation written in Vyn, reacting to movement, camera rotation, "
                      "vertical motion and wind. Import it into your own pack in one line."),
    card_script=card("key", "Script it yourself",
                     "Full transform, colour, light, glint, tint and data-component control over any "
                     "rendered item model — plus events for ticks, swings, sounds and key presses."),

    gallery_head=section_head("Gallery", "See it in motion",
                              "Straight from the Modrinth gallery — click any shot to enlarge it."),
    shot1=shot(TITLECARD, TITLECARD, "Titlecard", "The InteractiveStuff titlecard"),
    shot2=shot("https://cdn.modrinth.com/data/KDfqMm8K/images/0da07f98d8333d3516a3085532778f38e1adfa49.gif",
               "https://cdn.modrinth.com/data/KDfqMm8K/images/0da07f98d8333d3516a3085532778f38e1adfa49.gif",
               "Paintings &amp; Item Frames",
               "Physics Library using Item Frames and Paintings — made with Vyn in InteractiveStuff's resource pack", "GIF"),
    shot3=shot("https://cdn.modrinth.com/data/KDfqMm8K/images/4d7934c0414324929d0421e1b1025b0f3721f612.gif",
               "https://cdn.modrinth.com/data/KDfqMm8K/images/4d7934c0414324929d0421e1b1025b0f3721f612.gif",
               "House &amp; Panda",
               "Interactive redstone repeater and comparator, with Refined Torches &amp; InteractiveStuff", "GIF"),

    note_order=note("Always keep the <strong>InteractiveStuff resource pack at the bottom</strong> of your "
                    "pack order when you're using other packs that import its scripts — otherwise they "
                    "won't be able to see them.", "warn", "Pack order matters"),
)

