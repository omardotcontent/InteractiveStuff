from common import *


def faq(q, a, open_=False):
    return (f'<div class="faq-item fade-up{" open" if open_ else ""}">'
            f'<button class="faq-q" type="button" aria-expanded="{str(open_).lower()}">{q}'
            f'<span class="faq-caret" aria-hidden="true">+</span></button>'
            f'<div class="faq-a"><p>{a}</p></div></div>')


TITLE = "FAQ — InteractiveStuff"
DESC = ("Frequently asked questions about InteractiveStuff: supported versions, dependencies, "
        "compatibility, performance, scripting and licensing.")
EXTRA_JS = ""

BODY = """
<section class="section" style="padding-top:50px">
  <div class="wrap" style="max-width:860px">
    {head}

    {items}

    <h2 class="display mt-3">Still stuck?</h2>
    <p class="lead">The Discord is the fastest way to get an answer — and the place to share what you
    build.</p>
    <div class="btn-row">
      <a class="btn btn-blue" href="{DISCORD}" target="_blank" rel="noopener">Join the Discord</a>
      <a class="btn btn-ghost" href="{GITHUB}/issues" target="_blank" rel="noopener">Report a bug on GitHub</a>
      <a class="btn btn-ghost" href="mailto:omar@merakistudios.dev">Email Omar</a>
    </div>
  </div>
</section>
""".format(
    head=section_head("FAQ", "Questions, answered",
                      "Everything people usually ask before installing — and a few things they ask after."),
    DISCORD=DISCORD, GITHUB=GITHUB,
    items="\n    ".join([
        faq("What exactly is InteractiveStuff?",
            "A client-side Fabric mod for Minecraft 1.21.10+ that adds immersive, interactive behaviours "
            "to items and blocks in first-person view: animated held items, reactive note blocks, sculk "
            "sensors, lanterns and much more.", True),
        faq("Is this a first-person animation mod?",
            "No — and that distinction matters. InteractiveStuff is a <strong>first-person item "
            "interactions</strong> mod. It makes the things in your hand respond to the world; it doesn't "
            "rework how your character moves."),
        faq("Which Minecraft versions are supported?",
            "Currently <strong>1.21.10, 1.21.11 and 26.2</strong> on the Fabric loader."),
        faq("Does the server need it installed?",
            "No. It's <strong>client-side</strong> (Modrinth lists it as client-required, "
            "server-unsupported), so it works on any server. Other players just won't see your effects "
            "unless they install it too."),
        faq("What are the hard dependencies?",
            "<strong>Fabric API</strong> is required. <strong>VynAPI</strong>, the scripting engine, is "
            "bundled inside the mod jar. <strong>Mod Menu</strong> and <strong>YACL</strong> are optional — "
            "together they give you an in-game config screen."),
        faq("How do I open the config?",
            "Install Mod Menu and YACL, then open <em>Mods → InteractiveStuff → config button</em>. You'll "
            "find Scripted Packs settings, Interactive Hits settings, and options to open the config "
            "directory or reload the configuration."),
        faq("Nothing is animating — what did I miss?",
            "Three likely causes: the bundled resource pack is disabled or ordered wrong, you're in third "
            "person, or another pack is overriding the models. Check <em>Options → Resource Packs</em> "
            "first, and switch to first person."),
        faq("Why does pack order matter?",
            "If another pack imports InteractiveStuff's script libraries, the InteractiveStuff pack must "
            "sit <strong>below</strong> it in the selected list, otherwise the importing pack can't see "
            "the scripts it's asking for."),
        faq("Where do my own scripts go?",
            "In your resource pack at <code>assets/minecraft/scripts/*.vyn</code>. Older guides say "
            "<code>assets/interactivestuff/scripts/</code> — that path changed in the 0.7 snapshots."),
        faq("Can I turn off a built-in interaction I don't like?",
            "Yes. Call <code>excludeScript(\"interactivestuff:interactive_resourcepack\", \"sculk_sensors\")</code> "
            "from your own script, or just disable the bundled pack and ship your own selection."),
        faq("Does it work with Sodium, Iris or shaders?",
            "Yes — InteractiveStuff works on the standard Fabric rendering pipeline. The one thing to be "
            "aware of is that scripts can set custom light values on held items, which some shader packs "
            "handle differently."),
        faq("Will it fight with other animation mods?",
            "The bundled scripts check for <code>holdmyitems</code> and step aside automatically. For other "
            "animation mods, exclude the scripts that overlap with what they do."),
        faq("Does it hurt performance?",
            "Scripts run per rendered item per frame, so the cost scales with how much is happening. Keep "
            "your <code>onItemUpdate</code> guards tight (check the item name and gamemode before doing "
            "work) and it stays cheap."),
        faq("Does it support languages other than English?",
            "Yes — 0.6.2 added 15 languages including Dutch, Filipino, French, Polish, Simplified Chinese, "
            "Spanish, Turkish, Ukrainian, Thai and Hindi, plus several English variants."),
        faq("Can I include it in a modpack?",
            "Yes, on both Modrinth and CurseForge. Just make sure Fabric API ships alongside it."),
        faq("Can I redistribute or re-upload the scripts or jar?",
            "InteractiveStuff is <strong>All Rights Reserved</strong>. Bundling it in a modpack is fine; "
            "re-hosting the jar or republishing the scripts is not. Ask first — "
            "<a href=\"mailto:omar@merakistudios.dev\">omar@merakistudios.dev</a>."),
    ]),
)
