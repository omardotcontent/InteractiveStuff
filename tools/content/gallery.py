from common import *


def item(src, full, title, caption, tag=""):
    t = f'<span class="tag">{tag}</span>' if tag else ""
    return (f'<figure class="shot fade-up" style="margin:0">{t}'
            f'<img src="{src}" data-full="{full}" alt="{title}" loading="lazy">'
            f'<figcaption class="cap"><b>{title}</b><span>{caption}</span></figcaption></figure>')



def card(src, title, caption):
    return (f'<figure class="shot fade-up" style="margin:0">'
            f'<img src="{src}" alt="{title}" loading="lazy" style="height:150px;object-fit:contain;'
            f'padding:14px;background:var(--surface-2)">'
            f'<figcaption class="cap"><b>{title}</b><span>{caption}</span></figcaption></figure>')

TITLE = "Gallery — InteractiveStuff"
DESC = ("Screenshots and clips of InteractiveStuff in action: physics-driven item frames, interactive "
        "redstone, biome-tinted plants and the official titlecard.")
EXTRA_JS = ""

SHOTS = [
    (TITLECARD, TITLECARD, "Titlecard", "The official InteractiveStuff titlecard"),
    ("https://cdn.modrinth.com/data/KDfqMm8K/images/0da07f98d8333d3516a3085532778f38e1adfa49.gif",
     "https://cdn.modrinth.com/data/KDfqMm8K/images/0da07f98d8333d3516a3085532778f38e1adfa49.gif",
     "Paintings &amp; Item Frames",
     "Physics Library using Item Frames and Paintings — made with Vyn in InteractiveStuff's resource pack", "GIF"),
    ("https://cdn.modrinth.com/data/KDfqMm8K/images/4d7934c0414324929d0421e1b1025b0f3721f612.gif",
     "https://cdn.modrinth.com/data/KDfqMm8K/images/4d7934c0414324929d0421e1b1025b0f3721f612.gif",
     "House &amp; Panda",
     "Interactive redstone repeater and comparator, with Refined Torches &amp; InteractiveStuff", "GIF"),
    ("https://cdn.modrinth.com/data/KDfqMm8K/images/9cc693d18fcd25b1b173c2031dae32d4db68fb41.png",
     "https://cdn.modrinth.com/data/KDfqMm8K/images/9cc693d18fcd25b1b173c2031dae32d4db68fb41.png",
     "Savanna",
     "Tall grass and acacia sapling, biome-tinted with Fresh Flowers and Plants &amp; InteractiveStuff"),
]

BODY = """
<section class="section" style="padding-top:56px">
  <div class="wrap">
    {head}
    <div class="gallery-grid">
      {shots}
    </div>
    <p class="center muted small mt-3">Click any image to view it full size. Press <kbd>Esc</kbd> to close.</p>
  </div>
</section>

<section class="section section-alt">
  <div class="wrap">
    {brandhead}
    <div class="grid grid-3">
      {b1}
      {b2}
      {b3}
    </div>
    <div class="grid grid-3 mt-2">
      {b4}
      {b5}
      {b6}
    </div>
  </div>
</section>

<section class="section">
  <div class="wrap center">
    <span class="eyebrow">Shot something cool?</span>
    <h2 class="display">Show it off</h2>
    <p class="lead">Screenshots, clips and pack experiments are always welcome on the Discord.</p>
    <div class="btn-row center mt-2">
      <a class="btn btn-blue btn-lg" href="{DISCORD}" target="_blank" rel="noopener">Join the Discord</a>
      <a class="btn btn-ghost btn-lg" href="{MODRINTH}" target="_blank" rel="noopener">Modrinth page</a>
    </div>
  </div>
</section>

<div class="lightbox" id="lightbox" role="dialog" aria-modal="true" aria-label="Image viewer">
  <button class="lb-close" aria-label="Close">
    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
  </button>
  <div>
    <img src="" alt="">
    <div class="lb-cap"></div>
  </div>
</div>
""".format(
    head=section_head("Gallery", "InteractiveStuff in motion",
                      "Straight from the official Modrinth gallery."),
    shots="\n      ".join(item(*s) for s in SHOTS),
    brandhead=section_head("Brand &amp; assets", "Logos and banners",
                           "Official art, hot-linked from the Modrinth CDN."),
    b1=card(LOGO, "Icon", "The mod icon (512×512 source)"),
    b2=card(DESC_BANNER, "Description banner", "Used at the top of the Modrinth page"),
    b3=card(IMPORTANT_BANNER, "\"Important\" banner", "The requirements notice"),
    b4=card(FABRIC_API, "Fabric API", "The one hard dependency"),
    b5=card(COLLECTION, "Recommended collection", "Curated packs on Modrinth"),
    b6=card(KOFI, "Ko-fi", "Support the project"),
    DISCORD=DISCORD,
    MODRINTH=MODRINTH,
)

