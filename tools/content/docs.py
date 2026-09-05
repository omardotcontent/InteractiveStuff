from common import *

TITLE = "Getting Started — InteractiveStuff Docs"
DESC = ("Learn Vyn, the scripting language built into InteractiveStuff: where scripts live, the language "
        "basics, events, types and the notes that trip people up.")
EXTRA_JS = ""

BODY = """
<section class="section" style="padding-top:50px">
  <div class="wrap">
    <div class="docs-layout">

      <aside class="docs-side">
        <h4>Getting Started</h4>
        <a href="#overview">What is Vyn?</a>
        <a href="#where">Where scripts live</a>
        <a href="#first">Your first script</a>
        <a href="#language">Language basics</a>
        <a href="#events">Events</a>
        <a href="#types">Types</a>
        <a href="#notes">Notes to take care of</a>
        <a href="#next">Next steps</a>
        <h4>Reference</h4>
        <a href="api.html">Full API reference</a>
        <a href="physics.html">Physics Library</a>
        <a href="https://github.com/Abdelaziz1586/Vyn" target="_blank" rel="noopener">Vyn engine ↗</a>
        <a href="__WIKI__" target="_blank" rel="noopener">Official wiki ↗</a>
      </aside>

      <div class="docs-main">

        <h2 id="overview">What is Vyn?</h2>
        <p>Starting in <strong>0.7</strong>, InteractiveStuff ships with <strong>Vyn</strong> — a built-in
        scripting engine that lets resource pack creators script item rendering and behaviour any way they
        want, <strong>with no Java required</strong>. Every built-in interaction in the mod is itself a Vyn
        script, which means the whole feature set is readable and remixable.</p>
        <p>Scripts interact with the game through <strong>VynTypes</strong> — objects that represent in-game
        data like the player, blocks, sounds and the world. Each type exposes a set of
        <strong>VynFunctions</strong> you call in your scripts. Vyn uses <code>make</code> to declare
        variables, <code>check</code> for conditionals, <code>cycle</code> for loops and <code>task</code>
        for functions.</p>
        {code_hello}

        <h2 id="where">Where scripts live</h2>
        <p>Scripts are plain text files with a <code>.vyn</code> extension inside a resource pack:</p>
        {code_tree}
        {note_path}
        <p>Pack IDs matter when you import. The bundled pack is
        <code>interactivestuff:interactive_resourcepack</code>, so importing its physics library looks like
        this:</p>
        {code_import}

        <h2 id="first">Your first script</h2>
        <p>Create <code>my_first_script.vyn</code> in your pack, drop the pack into your resource packs
        folder, enable it, and press <kbd>F3</kbd> + <kbd>T</kbd> to reload. This script tints whatever
        you're holding with the grass colour of the biome you're standing in:</p>
        {code_first}
        <p>Nothing appeared? Turn on <strong>Resource Pack Debug Mode</strong> in the mod config —
        <code>debugText</code> only renders when it's enabled.</p>

        <h2 id="language">Language basics</h2>
        <p>Vyn is small. This is essentially the whole language:</p>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Statement</th><th>What it does</th></tr></thead>
            <tbody>
              <tr><td><code>make x = value</code></td><td>Declare (or reassign) a variable.</td></tr>
              <tr><td><code>lock X = value</code></td><td>Declare a constant. Used for tunables at the top of a file.</td></tr>
              <tr><td><code>check cond do … end</code></td><td>If statement.</td></tr>
              <tr><td><code>check cond do … otherwise … end</code></td><td>If / else.</td></tr>
              <tr><td><code>cycle i from 0 to n do … end</code></td><td>Counting loop, inclusive.</td></tr>
              <tr><td><code>escape</code></td><td>Break out of the current loop.</td></tr>
              <tr><td><code>task name do … end</code></td><td>Define a function.</td></tr>
              <tr><td><code>task name takes a, b do … end</code></td><td>Define a function with parameters.</td></tr>
              <tr><td><code>reply value</code></td><td>Return a value from a function.</td></tr>
              <tr><td><code>wait 20 do … end</code></td><td>Run a block after 20 ticks (1 second), in the background.</td></tr>
              <tr><td><code>~ comment</code></td><td>A line comment. Vyn uses <code>~</code>, not <code>//</code>.</td></tr>
              <tr><td><code>nothing</code></td><td>The null value. Compare with <code>x != nothing</code>.</td></tr>
              <tr><td><code>new ItemModel("minecraft:book")</code></td><td>Construct a type. Also <code>new Position(x, y, z)</code>, <code>new Sound(id, vol, pitch, pos)</code>, <code>new Key()</code>.</td></tr>
              <tr><td><code>new List</code></td><td>Create a list, then <code>myList.add(a, b, c)</code>, <code>myList.get(i)</code>, <code>myList.size()</code>, <code>myList.contains(x)</code>.</td></tr>
            </tbody>
          </table>
        </div>
        <h3>Operators &amp; built-ins</h3>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Token</th><th>Meaning</th></tr></thead>
            <tbody>
              <tr><td><code>==</code> <code>!=</code> <code>&lt;</code> <code>&gt;</code> <code>&lt;=</code> <code>&gt;=</code></td><td>Comparison.</td></tr>
              <tr><td><code>&amp;&amp;</code> <code>||</code> <code>!</code></td><td>And, or, not.</td></tr>
              <tr><td><code>size(x)</code></td><td>Length of a list.</td></tr>
              <tr><td><code>random()</code> / <code>random(a, b)</code></td><td>Random float 0–1, or a random integer in a range.</td></tr>
              <tr><td><code>time()</code></td><td>Current time in milliseconds — handy for spinning animations.</td></tr>
              <tr><td><code>sin(x)</code> <code>cos(x)</code></td><td>Trigonometry, in radians.</td></tr>
              <tr><td><code>replace(str, from, to)</code></td><td>String replace — used everywhere for building model IDs.</td></tr>
            </tbody>
          </table>
        </div>

        <h2 id="events">Events</h2>
        <p>Events are just tasks with reserved names. Define one and Vyn calls it at the right time.</p>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Event</th><th>Fires when</th></tr></thead>
            <tbody>
              <tr><td><code>task onTick do … end</code></td><td>Every game tick (20×/second). Use it for logic on a fixed schedule.</td></tr>
              <tr><td><code>task onSwingHand do … end</code></td><td>The player swings their hand.</td></tr>
              <tr><td><code>task onPlaySound takes sound do … end</code></td><td>The game plays a sound. You get the <code>Sound</code> before it plays.</td></tr>
              <tr><td><code>task onItemUpdate takes itemRendered do … end</code></td><td>Every render frame, for each item being rendered. <strong>The</strong> event for transforms, physics and colours.</td></tr>
              <tr><td><code>task onKeyPress takes keyInput do … end</code></td><td>A key is pressed. <code>keyInput</code> is the integer key code.</td></tr>
            </tbody>
          </table>
        </div>
        {note_spectator}

        <h2 id="types">Types</h2>
        <p>Nine types are available globally, plus one documented from the source. Click through for the
        full reference:</p>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Type</th><th>Instance</th><th>What it gives you</th></tr></thead>
            <tbody>
              <tr><td><a href="api.html#block">Block</a></td><td><code>block</code></td><td>State, position, light levels and properties of a block.</td></tr>
              <tr><td><a href="api.html#itemmodel">ItemModel</a></td><td><code>itemRendered</code></td><td>Transform, colour, lighting, tint and data components of a rendered item.</td></tr>
              <tr><td><a href="api.html#player">Player</a></td><td><code>player</code></td><td>Position, health, held items, movement state, sounds.</td></tr>
              <tr><td><a href="api.html#world">World</a></td><td><code>world</code></td><td>Blocks, biomes, time, dimension and colours.</td></tr>
              <tr><td><a href="api.html#position">Position</a></td><td><code>pos</code></td><td>An integer X/Y/Z coordinate.</td></tr>
              <tr><td><a href="api.html#sound">Sound</a></td><td><code>snd</code></td><td>A sound with ID, volume, pitch and optional position.</td></tr>
              <tr><td><a href="api.html#modloader">ModLoader</a></td><td><code>modLoader</code></td><td>Loaded mods, active resource packs, game version.</td></tr>
              <tr><td><a href="api.html#config">InteractiveStuffConfig</a></td><td><code>config</code></td><td>The player's current mod settings.</td></tr>
              <tr><td><a href="api.html#key">Key</a></td><td><code>keyHelper</code></td><td>Resolve Minecraft translation keys.</td></tr>
              <tr><td><a href="api.html#isp">ISP</a></td><td><code>isp</code></td><td>Hand checks and camera velocity — used throughout the built-in pack.</td></tr>
            </tbody>
          </table>
        </div>
        <div class="note info"><p><strong>Alias tip:</strong> the shipped scripts call
        <code>player.getGameMode()</code> while the wiki documents <code>player.getGamemode()</code>. Both
        work — pick one and stay consistent.</p></div>

        <h2 id="notes">Notes to take care of</h2>
        <ul>
          <li><strong>Add Pack IDs before the file/pack file name</strong> for functions that take one —
          e.g. <code>importScript("mypack", "math_utils")</code>.</li>
          <li><strong>Always keep the InteractiveStuff resource pack at the bottom</strong> of the pack list
          before using other resource packs that rely on its importable scripts.</li>
          <li><strong>It's not a first-person animation mod</strong> — it's a first-person
          <em>item interactions</em> mod. Set expectations accordingly.</li>
        </ul>

        <h2 id="next">Next steps</h2>
        <div class="btn-row">
          <a class="btn btn-primary" href="api.html">{i_api} Browse the API reference</a>
          <a class="btn btn-ghost" href="physics.html">{i_phys} Physics Library</a>
          <a class="btn btn-ghost" href="features.html">{i_feat} Built-in scripts</a>
        </div>

      </div>
    </div>
  </div>
</section>
""".replace("__WIKI__", WIKI).format(
    code_hello=code('''~ Basic example: tint the held item based on the block you're standing on
make block player.getSteppingBlock()
make item player.getMainHandItem()
check block.hasBlockTag("minecraft:logs") do
    item.setColor(139, 90, 43)
end''', label="example.vyn"),

    code_tree=code("""my_resource_pack/
├── pack.mcmeta
└── assets/
    └── minecraft/
        └── scripts/
            ├── my_first_script.vyn
            └── physics_lib.vyn""", lang="bash", label="pack layout"),

    note_path=note("Scripts used to live in <code>assets/interactivestuff/scripts/</code>. As of the 0.7 "
                   "snapshots the path is <code>assets/minecraft/scripts/</code> — if you're following an "
                   "older tutorial, that's the thing that will silently break.", "warn", "Path changed"),

    code_import=code('importScript("interactivestuff:interactive_resourcepack", "physics_lib")'),

    code_first=code('''task onItemUpdate takes itemRendered do
    check player.getGamemode() != "spectator" do
        make color = player.getWorld().getGrassColor(player.getPosition())
        itemRendered.setTint(color)
    end
end''', label="my_first_script.vyn"),

    note_spectator=note("Always guard <code>onItemUpdate</code> with "
                        "<code>check player.getGamemode() != \"spectator\" do … end</code> so your logic "
                        "doesn't run while spectating.", "warn", "Guard your render events"),

    i_api=icon("key", 17), i_phys=icon("wave", 17), i_feat=icon("spark", 17),
)
