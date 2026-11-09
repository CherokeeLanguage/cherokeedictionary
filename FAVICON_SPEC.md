# Favicon / Site Icon Specification

**Status:** Approved (design direction "Serif Duet" selected from a 14-concept brainstorm)
**Scope:** Browser favicon, Apple touch icon, PWA-ready maskable icons
**Regeneration:** `scripts/gen_favicon.py` (deterministic — see §7)

---

## 1. Goal

Replace the stock Grails favicon with a modern site icon that reads as the
Cherokee syllabary at every size, using the two leading glyphs of the site's
Cherokee phrase **ᏗᎧᏁᎢᏍᏗ ᏕᎦᏛᎬᎢ** (*dikanetisdi degadvgvi*, "the dictionary we
learn with"):

- **Ꮧ (di, U+13D7)** — opens the word for "dictionary"
- **Ꮥ (de, U+13D5)** — opens the second word

## 2. Glyph anatomy (from FreeSerif outlines)

| Glyph | Structure | Signature |
|---|---|---|
| Ꮧ di | wide top serif bar, tall stem at right, tail sweeping from the bar's left end down into a spiral | **ball terminal, bottom-left** |
| Ꮥ de | S-curved body, horizontal crossbar at mid-height extending right | **ball terminal, bottom-left** |

Shared DNA — a horizontal stroke plus a bottom-left ball terminal — is the
design hook; the two glyphs are presented as siblings ("two languages, one
dictionary").

## 3. Approved design — "Serif Duet"

The authentic serif glyph outlines, recolored and paired on a rounded tile:

- **Canvas:** `viewBox 0 0 512 512`, corner radius `112` (~22%, iOS-like)
- **Type scale:** `0.35` of the 1000-upm font; inter-glyph gap `24`
- **Baseline-aligned** at `y = 371.85`; block optically centered
  (di at `translate(38.725, 371.85)`, de at `translate(286.725, 371.85)`,
  each followed by `scale(0.35) scale(1,-1)` for the y-up → y-down flip)
- **Fill rule:** `nonzero` (TrueType winding)

### 3.1 Palette — "river & clay"

| Role | Hex | Usage |
|---|---|---|
| Tile (river navy) | `#1D3557` | background tile |
| Ink (cream) | `#F1FAEE` | di, page/paper association |
| Accent (clay) | `#E76F51` | de; earth/clay accent, Cherokee-flag orange nod |

Solid tile color is intentional: the icon must hold up on both light and dark
browser tab bars (no transparent-background variants are shipped).

### 3.2 Variants

| Variant | Tile | Art placement |
|---|---|---|
| `favicon.svg` (tab) | rounded, `rx 112` | full block |
| `favicon-maskable.svg` | full-bleed square (no radius) | art scaled to 66% about center — stays inside the maskable safe-zone circle (r ≈ 0.4 × 512) |

## 4. Deliverables

| File | Canvas | Built from | Purpose |
|---|---|---|---|
| `grails-app/assets/images/favicon.svg` | 512 | tab variant | primary favicon (SVG-capable browsers) |
| `grails-app/assets/images/favicon-maskable.svg` | 512 | maskable variant | PWA/manifest source |
| `grails-app/assets/images/favicon.ico` | 48 + 32 + 16 | tab variant | legacy favicon |
| `grails-app/assets/images/apple-touch-icon.png` | 180 | maskable variant | iOS home-screen icon |
| `grails-app/assets/images/apple-touch-icon-retina.png` | 360 | maskable variant | legacy retina touch icon |
| `grails-app/assets/images/icon-192.png` | 192 | maskable variant | manifest-ready |
| `grails-app/assets/images/icon-512.png` | 512 | maskable variant | manifest-ready |
| `src/main/webapp/favicon.ico` | copy of the ico | — | direct `/favicon.ico` requests (crawlers, bare fetches) |

## 5. Integration

Both layouts (`grails-app/views/layouts/main.gsp`, `manager.gsp`) declare, in
`<head>` after the description meta:

```gsp
<link rel="icon" href="${assetPath(src: 'favicon.svg')}" type="image/svg+xml">
<link rel="alternate icon" href="${assetPath(src: 'favicon.ico')}" sizes="any">
<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
```

Order matters: SVG-capable browsers take the first `icon`; the `sizes="any"`
ICO is the fallback.

## 6. Provenance & licensing

Glyph outlines are extracted from **FreeSerif** (GNU GPL v3 **with Font
Embedding Exception**), which permits embedding font content — including these
outlines — in documents/pages without spreading GPL to the page. Both SVGs
carry a provenance comment to that effect.

If the mark is ever reused outside page embedding (standalone logo files,
merchandise), re-trace the shapes as original vector work or swap in outlines
from an OFL-licensed serif Cherokee font; the visual stays effectively
unchanged.

## 7. Regeneration

```sh
python3 scripts/gen_favicon.py            # default font path
FREE_SERIF=/path/to/FreeSerif.ttf python3 scripts/gen_favicon.py
```

Requirements (build-time only, not runtime): Python 3 with `fontTools` and
`Pillow`, plus `inkscape` and ImageMagick `convert` on `PATH`. The script
extracts the two outlines, composes both SVG variants, rasterizes, writes every
deliverable in §4, and prints a manifest. Output is deterministic for a given
font file.

## 8. Acceptance criteria

- [x] All §4 files exist with the stated canvas sizes
- [x] `favicon.ico` contains 48/32/16 frames
- [x] Both layouts carry the three `<link>` tags, resolving through the asset pipeline
- [x] Mark is legible at 16 px and on both light and dark tab bars (verified at 256/128/64/32/16 on `#ffffff` and `#26262b`)
- [x] Maskable art stays inside the 80% safe-zone circle
- [x] Provenance comment present in both SVGs
- [x] No runtime dependencies added (generation is a build-time script)

## 9. Directions considered and not taken

From the brainstorm (all stress-tested at 16 px on light/dark): geometric
"Arch" knockouts (most legible, but the abstraction drifted toward generic
letterforms); twin-spiral fusion (distinctive, lost syllabary identity); one-
thread continuous stroke (read as a numeral); open-book mirrored arches;
ring badge; bar-and-dot minimal reduction (dropped de entirely); geometric
duet and interlock; shared-ball-terminal fusion. The serif duet was chosen
because it is the only direction that keeps both syllabary characters
recognizably themselves at a glance — the others read as abstract marks
(and, per review, several read as Hebrew letters rather than Cherokee).
