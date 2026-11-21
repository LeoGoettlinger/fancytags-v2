"""Reconstruct the old 8px badge strips from their Mojang profile properties."""
import argparse
import base64
from io import BytesIO
import json
from pathlib import Path
import re
from urllib.request import urlopen

from PIL import Image


def originals(source):
    for name, data in re.findall(r'tags.put\("(.*?)", new Tag\(List.of\((.*?)\)\)\);', source, re.S):
        tiles = []
        for value in re.findall(r'new PlayerSymbol\("([^"]+)"', data):
            url = json.loads(base64.b64decode(value))['textures']['SKIN']['url']
            digest = url.rsplit('/', 1)[1]
            assert re.fullmatch('[0-9a-f]{32,64}', digest)
            with urlopen('https://textures.minecraft.net/texture/' + digest, timeout=15) as response:
                tiles.append(Image.open(BytesIO(response.read())).convert('RGB').crop((8, 8, 16, 16)))
        if tiles:
            strip = Image.new('RGB', (len(tiles) * 8, 8))
            for i, tile in enumerate(tiles):
                strip.paste(tile, (8 * i, 0))
            yield name, strip


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('source', type=Path)
    args = parser.parse_args()
    for name, strip in originals(args.source.read_text()):
        palette = sorted(set(strip.getdata()))
        chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
        rows = [''.join(chars[palette.index(strip.getpixel((x,y))) % len(chars)] for x in range(strip.width)) for y in range(8)]
        print('::notice::' + name + ': ' + str(palette) + '%0A' + '%0A'.join(rows))
