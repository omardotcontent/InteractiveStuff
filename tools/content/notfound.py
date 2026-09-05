from common import *

TITLE = "404 — Page not found | InteractiveStuff"
DESC = "That page doesn't exist."
EXTRA_JS = ""

BODY = """
<section class="hero">
  <div class="hero-bg"></div>
  <div class="wrap center">
    <img class="hero-logo" src="{LOGO}" alt="InteractiveStuff logo">
    <span class="eyebrow">Error 404</span>
    <h1 class="display">This block is <span class="accent">air</span></h1>
    <p class="tagline">The page you were looking for isn't here. Try one of these instead.</p>
    <div class="btn-row center">
      <a class="btn btn-primary" href="index.html">Home</a>
      <a class="btn btn-ghost" href="features.html">Features</a>
      <a class="btn btn-ghost" href="docs.html">Docs</a>
      <a class="btn btn-ghost" href="api.html">API</a>
    </div>
  </div>
</section>
""".format(LOGO=LOGO)
