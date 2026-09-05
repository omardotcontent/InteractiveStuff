from common import *


def fcard(ic, title, script, text, tag=""):
    pill = f'<span class="pill">{script}</span>' if script else ""
    extra = f'<span class="pill new">{tag}</span> ' if tag else ""
    return (f'<div class="card hoverable fade-up"><div class="card-icon">{icon(ic, 24)}</div>'
            f'<h3>{title}</h3>'
            f'<p style="margin-bottom:.7em">{extra}{pill}</p>'
            f'<p class="muted small mb-0">{text}</p></div>')



def card(ic, title, api, text):
    return (f'<div class="card hoverable fade-up"><div class="card-icon">{icon(ic, 24)}</div>'
            f'<h3>{title}</h3>'
            f'<p style="margin-bottom:.6em"><code>{api}</code></p>'
            f'<p class="muted small mb-0">{text}</p></div>')

TITLE = "Features — InteractiveStuff"
DESC = ("Every built-in interaction in InteractiveStuff: chests, note blocks, sculk sensors, pistons, "
        "saplings, torches, the mace, the vault, item frames and more — all scripted in Vyn.")
EXTRA_JS = ""

FEATURES = [
    ("chest", "Chests", "chests.vyn",
     "Chests, ender chests, trapped chests and all four copper chest variants are split into a base and a "
     "separate lid model. The lid swings open proportionally to your downward velocity — jump off a cliff "
     "and it flies open, land softly and it barely moves. Hand-aware: the lid pivots the other way in your "
     "off hand."),
    ("music", "Note Blocks", "note_block.vyn",
     "Stand on a redstone block, crouch while holding a note block, and you walk through an 11-note melody. "
     "Each crouch advances the pattern, so you can play it by hand."),
    ("sensor", "Sculk Sensors", "sculk_sensors.vyn",
     "Holding a sensor makes it react to the world: it pulses and shakes when you land, and when any sound "
     "plays within 8 blocks (16 for the calibrated variant). Standing on wool, sneaking, or triggering one "
     "of the ignored wool/sensor sounds keeps it quiet — just like the real block."),
    ("piston", "Pistons", "pistons.vyn",
     "Sneak on a redstone block and the piston head extends with the real extend/contract sounds — or fall "
     "fast enough and inertia pops it out anyway. Sticky pistons travel slightly further."),
    ("flame", "Torches &amp; Campfires", "torches_and_more.vyn",
     "Take a torch, soul torch, copper torch, redstone torch or campfire underwater and it goes out — "
     "swapped to an unlit model, jittering, with the fire-extinguish sound. Surface again and it relights "
     "with a flint-and-steel hiss. On land, lit items emit a light level of 15."),
    ("block", "Redstone Items", "redstone_items.vyn",
     "Redstone lamps switch to their lit model and glow at light level 15; redstone torches flip to an off "
     "model. Both trigger when you sneak while standing on a redstone block."),
    ("leaf", "Saplings", "sapling.vyn",
     "Every sapling type is rebuilt as two models: a wood stem, plus leaves tinted with the foliage colour "
     "of the biome you're standing in. Walk from a savanna into a forest and watch the colour shift."),
    ("wave", "Mace", "mace.vyn",
     "Physics-driven sway plus a smoothed fall lean — the faster you fall, the further the mace tips — "
     "with a faint tremble layered on top."),
    ("shield", "Totem of Undying", "totem.vyn",
     "The totem shakes, and it shakes harder the lower your health gets. At full health it is almost still; "
     "at two hearts it is rattling."),
    ("chest", "Vault", "vault.vyn",
     "A random loot item — wind charge, emerald, trident, enchanted book and more — floats above the vault, "
     "spinning slowly and swapping out every couple of seconds."),
    ("frame", "Item Frames, Paintings &amp; Hanging Signs", "itemframes.vyn",
     "Anything frame-shaped gets physics-driven swing, with a different tilt depending on whether you're "
     "holding it in your main or off hand. Covers item frames, glow item frames, paintings and every wood "
     "type of hanging sign."),
    ("eye", "Water Bucket", "water_bucket.vyn",
     "The bucket gains a water layer tinted with the water colour of the biome you're standing in. Submerse "
     "yourself and it becomes an empty bucket."),
    ("book", "Enchanting Table", "enchanting_table.vyn",
     "Two books fold open on top of the table whenever there is a bookshelf within 2 blocks — the opening "
     "angle is smoothed, so it eases open as you walk up to a bookshelf wall."),
]

BODY = """
<section class="section" style="padding-top:56px">
  <div class="wrap">
    {head}
    <p class="center muted small">Every interaction below is a real file in the bundled resource pack —
    <code>assets/minecraft/scripts/</code>. Read them, copy them, or exclude them with
    <code>excludeScript()</code>.</p>
  </div>
</section>

<section class="section" style="padding-top:0">
  <div class="wrap">
    <div class="grid grid-3">
      {cards}
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap">
    {syshead}
    <div class="grid grid-3">
      {sys1}
      {sys2}
      {sys3}
      {sys4}
      {sys5}
      {sys6}
    </div>
  </div>
</section>

<section class="section">
  <div class="wrap">
    <div class="grid grid-2" style="gap:40px;align-items:start">
      <div>
        <span class="eyebrow">Also in the pack</span>
        <h2 class="display">Campfires, books and block models</h2>
        <p class="muted">Beyond the scripts, the bundled pack ships item models and textures for everything
        the scripts reference — chest lids, piston heads, sapling wood and leaves, unlit torches, the
        bucket's water layer, and left/right book models. Campfires and soul campfires also use a
        <code>display_context</code> select so they render as full block models in your hand while keeping
        the flat icon in the GUI.</p>
        {code_campfire}
      </div>
      <div>
        <span class="eyebrow">Your turn</span>
        <h2 class="display">Turn any of it off</h2>
        <p class="muted">Don't like one of the built-ins? You don't have to fork the pack. Any script can
        disable another script at load time, so a two-line pack is enough to opt out — or to switch to your
        own version.</p>
        {code_exclude}
        <p class="muted small">Resource pack scripts are discovered from every loaded pack, so your override
        can live entirely outside the mod jar.</p>
        <div class="btn-row">
          <a class="btn btn-primary" href="docs.html">Write your first script →</a>
          <a class="btn btn-ghost" href="api.html">API reference</a>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap center">
    <span class="eyebrow">Get it</span>
    <h2 class="display">Try them all in about two minutes</h2>
    <div class="btn-row center mt-2">
      <a class="btn btn-primary btn-lg" href="{MODRINTH}" target="_blank" rel="noopener">{i_dl} Download</a>
      <a class="btn btn-ghost btn-lg" href="install.html">Install guide</a>
    </div>
  </div>
</section>
""".format(
    head=section_head("Built-in interactions", "Thirteen things that react to you",
                      "All of this ships enabled by default in the bundled InteractiveStuff resource pack. "
                      "First-person mode strongly recommended."),
    cards="\n      ".join(fcard(*f) for f in FEATURES),
    syshead=section_head("Under the hood", "Mod-level features",
                         "Not scripts — these are toggles and systems the mod itself provides, and they're "
                         "all readable from Vyn through the <code>config</code> type."),
    sys1=card("spark", "Interactive Hits", "config.isInteractiveHitsEnabled()",
              "Hit reactions with a configurable cooldown between hits. Read the cooldown in ticks with "
              "<code>config.getHitCooldownTicks()</code>."),
    sys2=card("wrench", "Resource Pack Matrix Editing", "config.isResourcePackMatrixEditingEnabled()",
              "Allows packs to transform rendered item models. Respect this toggle before you call any "
              "translate/rotate/scale in your scripts."),
    sys3=card("eye", "Resource Pack Colour Changing", "config.isResourcePackColorChangingEnabled()",
              "Allows packs to recolour items. Guard your <code>setColor</code> / <code>setTint</code> calls "
              "with this option."),
    sys4=card("block", "Texture Changes", "config.isTextureChangesEnabled()",
              "Dynamic texture changes for items and blocks."),
    sys5=card("key", "Debug Mode", "config.isResourcePackDebugModeEnabled()",
              "Renders on-screen debug text (<code>debugText()</code>) and the pivot-point debug renderer — "
              "invaluable when you're positioning a custom model."),
    sys6=card("wave", "Physics Engine 1.0", "importScript(...)",
              "A spring-damper simulation written in Vyn that ships with the mod. Import it into any pack "
              "instead of writing your own physics."),
    code_campfire=code("""{
  "model": {
    "type": "minecraft:select",
    "property": "minecraft:display_context",
    "cases": [
      { "when": "gui", "model": { "type": "minecraft:model", "model": "minecraft:item/campfire" } }
    ],
    "fallback": { "type": "minecraft:model", "model": "minecraft:block/campfire" }
  }
}""", lang="json", label="assets/minecraft/items/campfire.json"),
    code_exclude=code('''~ Exclude one of InteractiveStuff's built-in scripts
excludeScript("interactivestuff:interactive_resourcepack", "sculk_sensors")

~ ...or only when another mod is present
check modLoader.isModLoaded("holdmyitems") do
    excludeScript("interactivestuff:interactive_resourcepack", "itemframes")
end'''),
    MODRINTH=MODRINTH,
    i_dl=icon("download"),
)

