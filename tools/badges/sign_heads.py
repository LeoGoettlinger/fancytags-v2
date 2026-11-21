"""Author-time conversion only. Upload each distinct face once and verify its pixels on Mojang's CDN."""
import base64
import hashlib
import io
import json
import os
from pathlib import Path
import re
import time
import zipfile

import requests
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / 'plugin/src/main/resources'
API = 'https://api.mineskin.org/v2'
client = requests.Session()
client.headers['User-Agent'] = 'FancyTags-SampleCraft/2.0 (https://github.com/LeoGoettlinger/fancytags-v2)'
if os.getenv('MINESKIN_API_KEY'):
    client.headers['Authorization'] = 'Bearer ' + os.environ['MINESKIN_API_KEY']


def generate(face):
    skin = Image.new('RGBA', (64, 64)); skin.paste(face, (8, 8))
    buf = io.BytesIO(); skin.save(buf, 'PNG')
    url = API + '/queue'
    failures = 0
    for _ in range(240):
        if url.endswith('/queue'):
            r = client.post(url, files={'file': ('badge.png', buf.getvalue(), 'image/png')},
                            data={'name': 'FancyTags', 'visibility': 'unlisted', 'variant': 'classic'}, timeout=90)
        else:
            r = client.get(url, timeout=45)
        if r.status_code == 429:
            time.sleep(max(10, float(r.headers.get('retry-after', '30')))); continue
        if r.status_code in (502, 503, 504) and failures < 8:
            failures += 1
            time.sleep(min(180, 15 * failures)); continue
        if not r.ok:
            raise RuntimeError(f'MineSkin HTTP {r.status_code}: {r.text[:450]}')
        body = r.json()
        if 'skin' in body:
            data = body['skin']['texture']['data']; value, signature = data['value'], data['signature']
            assert base64.b64decode(signature, validate=True)
            link = json.loads(base64.b64decode(value))['textures']['SKIN']['url']
            assert re.fullmatch(r'https?://textures\.minecraft\.net/texture/[0-9a-f]{32,64}', link)
            texture = requests.get(link.replace('http:', 'https:'), timeout=35); texture.raise_for_status()
            img = Image.open(io.BytesIO(texture.content)).convert('RGBA')
            assert img.crop((8, 8, 16, 16)).tobytes() == face.convert('RGBA').tobytes(), 'Face pixels changed'
            assert img.crop((40, 8, 48, 16)).getchannel('A').getextrema() == (0, 0), 'Unexpected hat layer'
            time.sleep(max(1, body.get('rateLimit', {}).get('next', {}).get('relative', 0)/1000))
            return value + ',' + signature
        job = body['job']
        if job['status'] == 'failed':
            errors = body.get('errors', [])
            codes = {e.get('code') for e in errors}
            mojang_limit = any(e.get('code') == 'skin_change_failed' and 'rate-limit' in e.get('message', '').lower() for e in errors)
            if failures < 10 and (mojang_limit or codes.intersection({'proxy_rate_limited', 'rate_limited', 'upstream_error', 'no_account_available'})):
                failures += 1
                delay = min(180, 30 * failures)
                print(f'MineSkin temporarily unavailable ({codes}); waiting {delay}s', flush=True)
                time.sleep(delay); url = API + '/queue'; continue
            raise RuntimeError(str(body.get('errors', job)))
        assert re.fullmatch('[a-zA-Z0-9-]+',job['id'])
        url = API + '/queue/' + job['id']; time.sleep(3)
    raise RuntimeError('Timed out waiting for MineSkin')


def main():
    props = OUT / 'head-textures.properties'
    cache = dict(line.split('=', 1) for line in props.read_text().splitlines() if line) if props.exists() else {}
    def save(): props.write_text(''.join(f'{k}={v}\n' for k,v in sorted(cache.items())))
    ranks = {}
    with zipfile.ZipFile(ROOT / 'artwork/FancyTags-Ranks.zip') as z:
        for path in z.namelist():
            if not path.endswith('.png'): continue
            _, rank, tile = path.rsplit('/', 2)
            face = Image.open(io.BytesIO(z.read(path))).convert('RGB'); assert face.size == (8,8)
            key = hashlib.sha256(face.tobytes()).hexdigest()
            if key not in cache:
                print(f'Signing {rank}/{tile} ({len(cache)} unique textures ready)', flush=True)
                cache[key] = generate(face); save()
            ranks.setdefault(rank, {})[int(tile[:-4])] = key
    (OUT / 'rank-heads.properties').write_text(''.join(rank+'='+','.join(tiles[i] for i in range(len(tiles)))+'\n' for rank, tiles in sorted(ranks.items())))

if __name__ == '__main__': main()
