#!/usr/bin/env python3
"""Generate the Cherokee-English Dictionary favicon set.

Builds every deliverable listed in FAVICON_SPEC.md §4 from the glyph outlines
of Ꮧ (U+13D7) and Ꮥ (U+13D5) in FreeSerif:

    grails-app/assets/images/favicon.svg            browser-tab icon (rounded tile)
    grails-app/assets/images/favicon-maskable.svg   full-bleed PWA maskable icon
    grails-app/assets/images/favicon.ico            48/32/16 multi-size ico
    grails-app/assets/images/apple-touch-icon.png   180 px touch icon
    grails-app/assets/images/apple-touch-icon-retina.png  360 px touch icon
    grails-app/assets/images/icon-192.png           manifest-ready
    grails-app/assets/images/icon-512.png           manifest-ready

Build-time requirements: python3 with fontTools + Pillow; inkscape and
ImageMagick `convert` on PATH. See FAVICON_SPEC.md for the design spec.

Usage:
    python3 scripts/gen_favicon.py
    FREE_SERIF=/path/to/FreeSerif.ttf python3 scripts/gen_favicon.py
"""

import os
import subprocess
import sys

from fontTools.ttLib import TTFont
from fontTools.pens.svgPathPen import SVGPathPen
from PIL import Image

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
IMAGES = os.path.join(REPO, 'grails-app', 'assets', 'images')

FONT = os.environ.get('FREE_SERIF',
                      '/usr/share/fonts/truetype/freefont/FreeSerif.ttf')

# palette — river & clay (FAVICON_SPEC.md §3.1)
BG, INK, ACC = '#1D3557', '#F1FAEE', '#E76F51'
DI, DE = 0x13D7, 0x13D5

# layout constants — FAVICON_SPEC.md §3 (font units: upm 1000, y-up)
SCALE = 0.35
GAP = 24
DI_W, DE_W = 640, 493          # glyph advance-box widths (bounds x extents)
TOP_DI, TOP_DE = 660, 676      # glyph heights above baseline
YB = (512 - 690 * SCALE) / 2 + TOP_DE * SCALE   # baseline line, optically centered

PROVENANCE = """<!-- Cherokee syllabary di (U+13D7) + de (U+13D5) from "dikanetisdi degadvgvi".
     Glyph outlines derived from FreeSerif (GNU GPL with Font Embedding Exception).
     Palette: river navy #1D3557, cream #F1FAEE, clay #E76F51. -->
"""


def glyph_svg(font, codepoint, fill, tx):
    cmap = font.getBestCmap()
    gset = font.getGlyphSet()
    pen = SVGPathPen(gset)
    gset[cmap[codepoint]].draw(pen)
    d = pen.getCommands()
    return (f'<g transform="translate({tx},{YB}) scale({SCALE}) scale(1,-1)">'
            f'<path d="{d}" fill="{fill}" fill-rule="nonzero"/></g>')


def build_svgs():
    font = TTFont(FONT)
    x0 = (512 - ((DI_W + DE_W) * SCALE + GAP)) / 2
    tx_di = x0 - 20 * SCALE
    tx_de = x0 + DI_W * SCALE + GAP - 20 * SCALE
    glyphs = glyph_svg(font, DI, INK, tx_di) + glyph_svg(font, DE, ACC, tx_de)

    tab = (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">'
           f'<rect width="512" height="512" rx="112" fill="{BG}"/>{glyphs}</svg>')
    # maskable: full-bleed square, art at 66% stays inside the safe-zone circle
    mask = (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">'
            f'<rect width="512" height="512" fill="{BG}"/>'
            f'<g transform="translate(256,256) scale(0.66) translate(-256,-256)">'
            f'{glyphs}</g></svg>')
    return PROVENANCE + tab, PROVENANCE + mask


def raster(svg_path, png_path):
    subprocess.run(['inkscape', svg_path, '-o', png_path, '-w', '512', '-h', '512'],
                   check=True, capture_output=True)


def write_outputs():
    tab_svg, mask_svg = build_svgs()
    tab_file = os.path.join(IMAGES, 'favicon.svg')
    mask_file = os.path.join(IMAGES, 'favicon-maskable.svg')
    open(tab_file, 'w').write(tab_svg)
    open(mask_file, 'w').write(mask_svg)

    tab_png = os.path.join(IMAGES, 'favicon_tab_tmp.png')
    mask_png = os.path.join(IMAGES, 'favicon_mask_tmp.png')
    raster(tab_file, tab_png)
    raster(mask_file, mask_png)
    tab = Image.open(tab_png).convert('RGBA')
    mask = Image.open(mask_png).convert('RGBA')

    mask.resize((180, 180), Image.LANCZOS).save(
        os.path.join(IMAGES, 'apple-touch-icon.png'))
    mask.resize((360, 360), Image.LANCZOS).save(
        os.path.join(IMAGES, 'apple-touch-icon-retina.png'))
    mask.resize((192, 192), Image.LANCZOS).save(
        os.path.join(IMAGES, 'icon-192.png'))
    mask.resize((512, 512), Image.LANCZOS).save(
        os.path.join(IMAGES, 'icon-512.png'))

    frames = []
    for size in (48, 32, 16):
        f = os.path.join(IMAGES, f'favicon_{size}_tmp.png')
        tab.resize((size, size), Image.LANCZOS).save(f)
        frames.append(f)
    ico = os.path.join(IMAGES, 'favicon.ico')
    subprocess.run(['convert', *frames, ico], check=True, capture_output=True)
    # note: bare /favicon.ico is served by the redirect in UrlMappings.groovy
    # (/assets/favicon.ico) — the executable WAR does not serve
    # src/main/webapp statically, so no web-root copy is shipped

    for tmp in [tab_png, mask_png, *frames]:
        os.remove(tmp)
    print('written:')
    for f in sorted(os.listdir(IMAGES)):
        if 'favicon' in f or 'icon' in f:
            print('  grails-app/assets/images/' + f)
    print('  src/main/webapp/favicon.ico')


if __name__ == '__main__':
    write_outputs()
