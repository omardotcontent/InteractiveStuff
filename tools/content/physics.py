from common import *

TITLE = "Physics Library — InteractiveStuff"
DESC = ("The built-in Vyn physics engine: spring-based item sway that reacts to movement, camera rotation, "
        "vertical motion and wind. Parameters, presets and copyable examples.")
EXTRA_JS = ""

BODY = """
<section class="section" style="padding-top:50px">
  <div class="wrap">
    <div class="docs-layout">

      <aside class="docs-side">
        <h4>Physics Engine</h4>
        <a href="#overview">Overview</a>
        <a href="#how">How it works</a>
        <a href="#params">Parameters</a>
        <a href="#examples">Examples</a>
        <a href="#presets">Presets</a>
        <a href="#tuning">Tuning cheatsheet</a>
        <a href="#notes">Notes</a>
        <h4>Reference</h4>
        <a href="api.html#itemmodel">ItemModel transforms</a>
        <a href="docs.html">Getting started</a>
        <a href="https://github.com/iamcyberia/fresh-flowers-and-plants" target="_blank" rel="noopener">Fresh Flowers &amp; Plants ↗</a>
      </aside>

      <div class="docs-main">

        <h2 id="overview">Overview</h2>
        <p>InteractiveStuff ships with a built-in <strong>Physics Engine</strong> script library, written
        entirely in Vyn. It simulates spring-based item rotation that reacts to player movement, camera
        rotation and wind — giving held items a natural, physical feel without you having to write the
        math yourself.</p>
        <p>Import it in any script with:</p>
        {code_import}

        <h2 id="how">How it works</h2>
        <p>The engine maintains a spring-damper simulation on two rotation axes. Each frame it accumulates
        forces from:</p>
        <ul>
          <li><strong>Player movement</strong> — acceleration when you walk, sprint or strafe.</li>
          <li><strong>Camera rotation</strong> — impulses from looking around.</li>
          <li><strong>Vertical movement</strong> — jumping, falling, landing.</li>
          <li><strong>Wind</strong> — a procedural layered sine wave that adds subtle organic drift.</li>
        </ul>
        <p>After calling <code>runPhysicsEngine(...)</code> the results are written to two global variables
        you can read directly:</p>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Variable</th><th>Description</th></tr></thead>
            <tbody>
              <tr><td><code>angleX</code></td><td>Current rotation angle on the X axis (pitch-like sway).</td></tr>
              <tr><td><code>angleZ</code></td><td>Current rotation angle on the Z axis (roll-like sway).</td></tr>
            </tbody>
          </table>
        </div>
        <p>Apply them to your item with <code>rotateX</code> and <code>rotateZ</code> — or both at once with
        <code>rotate(angleX, 0, angleZ)</code>.</p>

        <h2 id="params">Parameters</h2>
        <p><code>runPhysicsEngine(stiffness, damping, moveRes, lookRes, vertRes, inertial, maxAngle, windMult)</code>
        runs one tick of the simulation. Call it <strong>once per frame, at the top of your event</strong>,
        before applying the resulting angles.</p>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Parameter</th><th>Type</th><th>Description</th><th>Typical</th></tr></thead>
            <tbody>
              <tr><td><code>stiffness</code></td><td>Double</td><td>How strongly the item springs back to rest. Higher = snappier.</td><td>0.01 – 0.05</td></tr>
              <tr><td><code>damping</code></td><td>Double</td><td>How quickly oscillation dies out. Higher = less bouncy.</td><td>0.1 – 0.5</td></tr>
              <tr><td><code>moveRes</code></td><td>Double</td><td>How strongly movement acceleration affects the angles. <strong>Negative inverts</strong> the sway direction.</td><td>-1.0 – 3.0</td></tr>
              <tr><td><code>lookRes</code></td><td>Double</td><td>How strongly camera rotation affects the angles.</td><td>0.3 – 0.36</td></tr>
              <tr><td><code>vertRes</code></td><td>Double</td><td>How strongly vertical movement affects the X angle.</td><td>0.45 – 0.5</td></tr>
              <tr><td><code>inertial</code></td><td>Double</td><td>Global inertia multiplier. Scales wind and movement/look impulses. Higher = heavier.</td><td>0.8 – 1.0</td></tr>
              <tr><td><code>maxAngle</code></td><td>Double</td><td>Maximum lean angle, in degrees, in either direction.</td><td>15 – 60</td></tr>
              <tr><td><code>windMult</code></td><td>Double</td><td>Wind strength. Set to <code>0</code> to disable wind entirely.</td><td>0 – 0.1</td></tr>
            </tbody>
          </table>
        </div>
        <p class="muted small">It returns <code>0</code> early (a no-op) if called more than once in the
        same tick, or if the frame delta is too large — in that case velocities are also reset for
        stability.</p>

        <h2 id="examples">Examples</h2>

        <h3>Basic plant sway</h3>
        <p>Flowers, grasses, tall grass.</p>
        {code_plant}

        <h3>Stiff shrub</h3>
        <p>Azalea, flowering azalea — stiffer and more damped, so it's less floppy than a flower.</p>
        {code_shrub}

        <h3>Handle and body</h3>
        <p>Basket-style models: <code>select()</code> and <code>setParent()</code> let you apply different
        physics multipliers to different parts of the same model. The handle swings with full physics while
        the body follows at 25%.</p>
        {code_basket}

        <h3>Dynamic shear</h3>
        <p>Adds squash-and-stretch on top of rotation, scaling with how much the item is swaying.</p>
        {code_shear}

        <h2 id="presets">Presets</h2>
        <p>These are the actual values used by InteractiveStuff's own scripts — a good starting point for
        your own pack.</p>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Item</th><th>stiffness</th><th>damping</th><th>moveRes</th><th>lookRes</th><th>vertRes</th><th>inertial</th><th>maxAngle</th><th>windMult</th></tr></thead>
            <tbody>
              <tr><td>Item frames, paintings, hanging signs</td><td>0.01</td><td>0.1</td><td>0.6</td><td>0.3</td><td>0.2</td><td>0.8</td><td>60</td><td>0</td></tr>
              <tr><td>Mace</td><td>0.01</td><td>0.2</td><td>0.1</td><td>0.2</td><td>0.4</td><td>1.0</td><td>45</td><td>0</td></tr>
              <tr><td>Flowers &amp; tall grass</td><td>0.01</td><td>0.15</td><td>-1.0</td><td>0.3</td><td>0.5</td><td>1.0</td><td>45</td><td>0.1</td></tr>
              <tr><td>Azalea &amp; shrubs</td><td>0.05</td><td>0.5</td><td>-1.0</td><td>0.3</td><td>0.5</td><td>1.0</td><td>15</td><td>0.1</td></tr>
              <tr><td>Basket-style (handle + body)</td><td>0.01</td><td>0.1</td><td>3.0</td><td>0.36</td><td>0.45</td><td>0.8</td><td>60</td><td>0</td></tr>
            </tbody>
          </table>
        </div>

        <h2 id="tuning">Tuning cheatsheet</h2>
        <div class="table-scroll">
          <table>
            <thead><tr><th>Goal</th><th>Adjust</th></tr></thead>
            <tbody>
              <tr><td>Snappier return to rest</td><td>Increase <code>stiffness</code> (e.g. <code>0.05</code>)</td></tr>
              <tr><td>Less bouncy / more stable</td><td>Increase <code>damping</code> (e.g. <code>0.5</code>)</td></tr>
              <tr><td>More sway when walking</td><td>Increase <code>moveRes</code> (e.g. <code>3.0</code>)</td></tr>
              <tr><td>Invert sway direction</td><td>Use negative <code>moveRes</code> (e.g. <code>-1.0</code>)</td></tr>
              <tr><td>More tilt when turning</td><td>Increase <code>lookRes</code> (e.g. <code>0.36</code>)</td></tr>
              <tr><td>Stronger jump/fall reaction</td><td>Increase <code>vertRes</code> (e.g. <code>0.5</code>)</td></tr>
              <tr><td>Heavier, more inertial feel</td><td>Keep <code>inertial</code> at <code>1.0</code></td></tr>
              <tr><td>Limit maximum lean angle</td><td>Decrease <code>maxAngle</code> (e.g. <code>15</code>)</td></tr>
              <tr><td>Wide, dramatic sway</td><td>Increase <code>maxAngle</code> (e.g. <code>60</code>)</td></tr>
              <tr><td>Remove wind drift</td><td>Set <code>windMult</code> to <code>0</code></td></tr>
              <tr><td>Subtle wind</td><td>Set <code>windMult</code> to <code>0.1</code></td></tr>
            </tbody>
          </table>
        </div>

        <h2 id="notes">Notes</h2>
        <ul>
          <li>The simulation is <strong>framerate-independent</strong> — it uses
          <code>getDelta()</code> internally, so results are consistent at any FPS.</li>
          <li>The engine <strong>resets automatically</strong> if a large frame spike is detected
          (delta &gt; 5.0), stopping the item from flying off-screen after a freeze.</li>
          <li><code>angleX</code> and <code>angleZ</code> are <strong>global variables</strong> — they
          persist between frames and are updated in place each call.</li>
          <li>Only <strong>one simulation can run per script</strong>. If you need physics on multiple
          parts, apply the same <code>angleX</code> / <code>angleZ</code> to both (optionally scaled).</li>
        </ul>

        <div class="note">
          <span class="note-title">Compatibility habit</span>
          <p>The shipped scripts wrap their physics calls in
          <code>check !modLoader.isModLoaded("holdmyitems") do … end</code> so they step aside when
          another animation mod is present. Worth copying.</p>
        </div>

        <div class="btn-row mt-3">
          <a class="btn btn-primary" href="api.html">{i_api} API reference</a>
          <a class="btn btn-ghost" href="docs.html">{i_doc} Getting started</a>
        </div>
      </div>
    </div>
  </div>
</section>
""".format(
    code_import=code('importScript("interactivestuff:interactive_resourcepack", "physics_lib")'),

    code_plant=code('''importScript("interactivestuff:interactive_resourcepack", "physics_lib")

task onItemUpdate takes itemRendered do
    check !modLoader.isModLoaded("holdmyitems") do
        runPhysicsEngine(0.01, 0.15, -1.0, 0.3, 0.5, 1.0, 45, 0.1)

        itemRendered.setPivot(0.0, -0.01, 0.0)
        itemRendered.rotate(angleX, 0, angleZ)
    end
end''', label="plant.vyn"),

    code_shrub=code('''importScript("interactivestuff:interactive_resourcepack", "physics_lib")

task onItemUpdate takes itemRendered do
    check !modLoader.isModLoaded("holdmyitems") do
        ~ Stiffer, more damped — less floppy than a flower
        runPhysicsEngine(0.05, 0.5, -1.0, 0.3, 0.5, 1.0, 15, 0.1)

        itemRendered.setPivot(0.005, 0.21, 0.12)
        itemRendered.rotate(angleX, 0, angleZ)
    end
end''', label="shrub.vyn"),

    code_basket=code('''importScript("interactivestuff:interactive_resourcepack", "physics_lib")

task onItemUpdate takes itemRendered do
    check !modLoader.isModLoaded("holdmyitems") do
        runPhysicsEngine(0.01, 0.1, 3.0, 0.36, 0.45, 0.8, 60, 0)

        ~ Handle swings with full physics
        make handle = itemRendered.copy()
        handle.select(27, 43)
        handle.setPivot(0, 0.3, 0)
        handle.rotate(angleX, 0, -angleZ)

        ~ Body follows but at 25% intensity
        itemRendered.setParent(handle)
        itemRendered.select(0, 27)
        itemRendered.setPivot(0, 0.15, 0)
        itemRendered.rotate(angleX * 0.25, 0, -angleZ * 0.25)
    end
end''', label="basket.vyn"),

    code_shear=code('''importScript("interactivestuff:interactive_resourcepack", "physics_lib")

task onItemUpdate takes itemRendered do
    check !modLoader.isModLoaded("holdmyitems") do
        runPhysicsEngine(0.01, 0.15, -1.0, 0.3, 0.5, 1.0, 45, 0.1)

        itemRendered.setPivot(0.005, 0.21, 0.12)
        itemRendered.rotate(angleX, 0, angleZ)

        ~ Stretch more the further it's swinging
        make motionSq = angleX * angleX + angleZ * angleZ
        make dynamicScale = 1.0 + motionSq * 0.0005
        check dynamicScale > 1.2 do make dynamicScale = 1.2 end

        make shearXY = angleZ * 0.006 * dynamicScale
        make shearXZ = (angleX * 0.006 + angleZ * 0.006 * 0.35) * dynamicScale
        make shearYX = angleX * 0.003 * dynamicScale
        make shearYZ = angleZ * 0.003 * dynamicScale
        make shearZX = angleZ * 0.0066 * dynamicScale
        make shearZY = angleX * 0.0072 * dynamicScale

        itemRendered.shear(shearXY, shearXZ, shearYX, shearYZ, shearZX, shearZY)
    end
end''', label="shear.vyn"),

    i_api=icon("key", 17),
    i_doc=icon("book", 17),
)
