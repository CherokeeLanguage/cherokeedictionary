#!/usr/bin/env python3
"""Fixture generator (cherokeedictionary#133): a TRACKED seed subset built
from the operator's prod dumps, so the regression suite can run from the
repository alone — no 260 MB operator dumps, no restored secrets.

Output: poc/seeds-fixture/01-fixture-dictionary-sample.sql (UTF-8 SQL,
small) plus copies of the shared 98/99 seed entries — the Poe compose
stack mounts poc/seeds-fixture when CED_SEEDS_DIR=seeds-fixture so the
fixture is an ALTERNATIVE seed document (fixture-mode vs dump-mode;
loading both is unsupported — ids collide).

Fixture content:
  ...same as before (per-source samples, whole tiny reference tables,
  empty sensitive tables)...

Usage:
    python3 poc/seeds/make-fixture.py [dump01.sql.gz] [dump02.sql.gz]
"""
import os
import re
import shutil
import sys
import gzip


def split_create(sql):
    out = {}
    for m in re.finditer(r"CREATE TABLE `(\w+)` \(.*?\) ENGINE=[^;]+;", sql,
                         re.S):
        out[m.group(1)] = m.group(0)
    return out


def split_tuple_rows(body):
    """Split an INSERT's VALUES body into row tuples — quote-aware."""
    rows, depth, cur, in_str = [], 0, [], False
    body = body.strip().rstrip(";")
    body = body[body.index("("):]
    i = 0
    while i < len(body):
        c = body[i]
        if in_str:
            if c == "\\" and body[i + 1:i + 2]:
                cur.append(c)
                cur.append(body[i + 1])
                i += 1
            else:
                cur.append(c)
                if c == "'":
                    if body[i + 1:i + 2] == "'":
                        cur.append("'")
                        i += 1
                    else:
                        in_str = False
        elif c == "'":
            in_str = True
            cur.append(c)
        elif c == "(":
            depth += 1
            if depth == 1:
                cur = []
            else:
                cur.append(c)
        elif c == ")":
            depth -= 1
            if depth == 0:
                rows.append("".join(cur))
            else:
                cur.append(c)
        else:
            cur.append(c)
        i += 1
    return rows


def parse_insert(table, line):
    stmt = line.strip().rstrip(";")
    m = re.match(r"INSERT INTO `(\w+)` (?:\(.*?\) )?VALUES", stmt)
    if not m or m.group(1) != table:
        return []
    return split_tuple_rows(stmt[stmt.index("VALUES") + 6:])


def split_fields(row):
    """Split one row tuple's interior (no parens) into fields — quote-aware."""
    fields, cur, in_str = [], [], False
    i = 0
    while i < len(row):
        c = row[i]
        if in_str:
            if c == "\\" and row[i + 1:i + 2]:
                cur.append(c)
                cur.append(row[i + 1])
                i += 1
            else:
                cur.append(c)
                if c == "'":
                    if row[i + 1:i + 2] == "'":
                        cur.append("'")
                        i += 1
                    else:
                        in_str = False
        elif c == "'":
            in_str = True
            cur.append(c)
        elif c == ",":
            fields.append("".join(cur))
            cur = []
        else:
            cur.append(c)
        i += 1
    if cur:
        fields.append("".join(cur))
    return fields


def as_fields(rows):
    return [split_fields(r) for r in rows]


def create_cols(table_sql):
    body = re.search(r"CREATE TABLE `(\w+)` \((.*)\) ENGINE=", table_sql,
                     re.S).group(2)
    # works for both multi- and single-line dumps: column defs end before
    # the first PRIMARY KEY/KEY constrAint spine
    cut = re.search(r"\n? *(PRIMARY KEY|UNIQUE KEY|KEY|CONSTRAINT)", body)
    if cut:
        body = body[:cut.start()]
    return re.findall(r"`(\w+)`", body)


def main(argv):
    p01 = argv[1] if len(argv) > 1 else \
        "poc/seeds/01-likespreadsheets-prod-noaudiolookup.sql.gz"
    p02 = argv[2] if len(argv) > 2 else \
        "poc/seeds/02-audio-lookup-fixed-cedpoc.sql.gz"
    fixture_dir = "poc/seeds-fixture"
    out_path = os.path.join(fixture_dir, "01-fixture-dictionary-sample.sql")
    os.makedirs(fixture_dir, exist_ok=True)
    # shared seed entries the fixture-mode stack also needs (copies — a
    # bind mount of seeds-fixture cannot see ../seeds targets)
    for shared in ("98-poc-test-admin.sh", "99-poc-settings.sql"):
        dst = os.path.join(fixture_dir, shared)
        shutil.copyfile(os.path.join(os.path.dirname(p01), shared), dst)
        os.chmod(dst, 0o755 if shared.endswith(".sh") else 0o644)

    d01 = gzip.open(p01).read().decode()
    d02 = gzip.open(p02).read().decode()
    lines01 = d01.splitlines()
    lines02 = d02.splitlines()

    creates = {**split_create(d01), **split_create(d02)}

    needed = ["likespreadsheets", "CherokeeIndex", "cherokeeindex",
              "audio_file", "audio_link", "definition", "definition_link",
              "word", "word_link", "word_definition_link", "cat_link",
              "verse", "source_management", "settings", "part_of_speech",
              "verb_category", "word_type", "category", "dialect",
              "corpus_source", "admin", "user", "user_role", "role",
              "user_search", "history", "search_controller",
              "cherokee_word_searches"]

    def rows_from(lines, needed_names):
        t = {n: [] for n in needed_names}
        for l in lines:
            if not (l.startswith("INSERT INTO `")):
                continue
            m = re.match(r"INSERT INTO `(\w+)` ", l)
            if m and m.group(1) in t:
                t[m.group(1)].extend(as_fields(parse_insert(m.group(1), l)))
        return t

    t = rows_from(lines01, needed)
    t["audio_lookup"] = rows_from(lines02, ["audio_lookup"])["audio_lookup"]

    ci = {n: {c: i for i, c in enumerate(create_cols(creates[n]))}
          for n in list(t)}

    # ---- tiny reference tables ship whole ------------------------------
    whole = ["source_management", "settings", "part_of_speech",
             "verb_category", "word_type", "category", "dialect",
             "corpus_source"]

    # ---- EMPTY-but-created: prod secrets/PII never ride the fixture ----
    for name in ["admin", "user", "user_role", "role", "user_search",
                 "history", "search_controller", "cherokee_word_searches"]:
        if name not in creates:
            raise SystemExit(f"sanity: expected table missing from dump: {name}")

    def clean(v):
        if isinstance(v, str) and v.startswith("'") and v.endswith("'"):
            return v[1:-1]
        return v

    # ---- entry selection: per-source samples + check terms -------------
    lsi, smi = ci["likespreadsheets"], ci["source_management"]
    source_codes = set()
    for row in t["source_management"]:
        source_codes.add(clean(row[smi["code"]]))

    per_source = {code: [] for code in source_codes}
    check_term_rows = []

    for row in t["likespreadsheets"]:
        code = clean(row[lsi["source"]])
        term = (clean(row[lsi["definitiond"]] or "") + " " +
                clean(row[lsi["entrya"]] or "")).lower()
        if "belt" in term or "hat" in term or "water" in term:
            check_term_rows.append(row)
        elif code in per_source and len(per_source[code]) < 12:
            per_source[code].append(row)

    # ---- entries carrying audio (exercise the audio surface) -----------
    audio_by_lsid = {}
    for row in t["audio_link"]:
        audio_by_lsid.setdefault(row[ci["audio_link"]["likespreadsheets_id"]],
                                 []).append(row)

    selected = list(check_term_rows)
    for code, rows in per_source.items():
        selected.extend(rows)

    # sources with zero prod entries still need ONE sample record so the
    # UI's source filters/rendering are exercisable ("sample" placeholder
    # rows — never fabricated Cherokee words)
    empty_sources = []
    for code, rows in per_source.items():
        if not rows:
            empty_sources.append(code)

    like_cols = create_cols(creates["likespreadsheets"])
    like_idx = {c: i for i, c in enumerate(like_cols)}
    synthetic = []
    for i, code in enumerate(empty_sources):
        row = ["NULL"] * len(like_cols)
        row[like_idx["id"]] = str(900000 + i)
        row[like_idx["version"]] = "0"
        row[like_idx["definitiond"]] = "'sample entry (fixture)'"
        row[like_idx["entrya"]] = "'sample'"
        row[like_idx["syllabaryb"]] = "'ᏤᏏᏝ sample'"  # latin-script row marker + token, not a fabricated word
        row[like_idx["source"]] = f"'{code}'"
        synthetic.append(row)
        per_source[code] = [row]
    if synthetic:
        print(f"note: synthesized {len(synthetic)} sample rows for sources "
              "with zero prod entries")
    seen = {r[lsi["id"]] for r in selected}
    for row in t["likespreadsheets"]:
        lsid = row[lsi["id"]]
        if lsid not in audio_by_lsid or lsid in seen:
            continue
        selected.append(row)
        seen.add(lsid)
        if len(seen) >= 60:
            break

    for code in sorted(per_source, key=lambda c: str(c)):
        if len(per_source[code]) < 12:
            print(f"note: source {code!r} has only {len(per_source[code])} "
                  "sample rows")

    # cap total rows sensibly (fixture stays small)
    if len(selected) > 400:
        selected = selected[:400]
        print(f"note: capped entries at 400 rows")
    lsids = {row[lsi["id"]] for row in selected}
    ls = selected

    # deterministic audio entry for the browser tier (the suite opens this
    # entry directly: its <audio> presence is the positive audio check)
    audio_entry = next((clean(row[lsi["id"]]) for row in selected
                        if clean(row[lsi["id"]]) in audio_by_lsid), None)
    if audio_entry:
        with open(os.path.join(
                os.path.dirname(os.path.dirname(fixture_dir)),
                "tests", "fixture_audio_entry.txt"), "w") as af:
            af.write(str(audio_entry) + "\n")

    # ---- cascade the FKs from the sampled entries ----------------------
    cho = lambda n, key, ids, rows: [
        row for row in rows if row[ci[n][key]] in ids]

    # CherokeeIndex/cherokeeindex map entry ids -> syllabary strings
    # (two spellings of the same index table in the dump — ONE table per
    # INSERT; mixing them duplicates ids)
    cherokee_upper = cho("CherokeeIndex", "likespreadsheetid", lsids,
                         t["CherokeeIndex"])
    cherokee_lower = cho("cherokeeindex", "likespreadsheetid", lsids,
                         t["cherokeeindex"])

    # NOTE: the relational word/definition tables in the prod dump are
    # FRAGMENTED (word ids 27505-30897 vs word_link ids 12375-20960 —
    # they never overlap), so their chains cannot be built consistently.
    # They are CREATED EMPTY here; the search entry pages render from
    # likespreadsheets directly, which is the tested surface.

    audio_file_ids = set()
    audio_links = []
    for lsid, rows in audio_by_lsid.items():
        if lsid in lsids:
            audio_links.extend(rows)
            audio_file_ids |= {r[ci["audio_link"]["audio_file_id"]]
                               for r in rows}
    audio_files = cho("audio_file", "id", audio_file_ids, t["audio_file"])
    audio_paths = {clean(r[ci["audio_file"]["audio_file_path"]])
                   for r in audio_files}
    audio_lookups = [r for r in t["audio_lookup"]
                     if clean(r[ci["audio_lookup"]["audio_file"]])
                     in audio_paths]

    verses = t["verse"][::len(t["verse"]) // 60 or 1][:60]

    parts = [
        ("source_management", [r for r in t["source_management"]]),
        ("settings", t["settings"]),
        ("part_of_speech", t["part_of_speech"]),
        ("verb_category", t["verb_category"]),
        ("word_type", t["word_type"]),
        ("category", t["category"]),
        ("dialect", t["dialect"]),
        ("corpus_source", t["corpus_source"]),
        ("likespreadsheets", ls),
        ("CherokeeIndex", cherokee_upper),
        ("cherokeeindex", cherokee_lower),
        ("audio_file", audio_files),
        ("audio_link", audio_links),
        ("audio_lookup", audio_lookups),
        ("verse", verses),
    ]

    creates_needed = whole + [
        "likespreadsheets", "CherokeeIndex", "cherokeeindex",
        "audio_file", "audio_link", "audio_lookup", "verse"] + \
        ["admin", "user", "user_role", "role", "user_search", "history",
         "word", "word_link", "word_definition_link", "definition",
         "definition_link", "cat_link",
         "search_controller", "cherokee_word_searches"]

    with gzip.open(out_path, "at") if out_path.endswith(".gz") else \
            open(out_path, "w") as f:
        f.write("-- TRACKED fixture subset (cherokeedictionary#133) — generated\n"
                "-- by poc/seeds/make-fixture.py from the operator's prod dumps.\n")
        f.write("-- Secrets-free by construction: the admin/user/history/role\n"
                "-- tables are CREATED EMPTY (prod passwords/PII stay out).\n")
        f.write("SET NAMES utf8mb4;\n\n")
        for name in dict.fromkeys(creates_needed):
            blk = re.sub(r"AUTO_INCREMENT=\d+\s", "", creates[name])
            f.write(blk + "\n")
        # the spring-session JDBC store (#130) ships its own committed DDL
        # (no rows ride fixtures — the session store is per-boot state)
        spring_ddl = os.path.join(os.path.dirname(os.path.dirname(
            os.path.dirname(os.path.abspath(__file__)))),
            "sql", "spring-session-schema-mysql.sql")
        if os.path.exists(spring_ddl):
            f.write(open(spring_ddl).read() + "\n")
        for name, rows in parts:
            if not rows:
                continue
            f.write(f"\n-- {len(rows)} row(s)\n")
            f.write(render_insert(name, rows))
        f.write("\n")

    total = sum(len(r) for _, r in parts)
    print(f"fixture: {total} rows across {len(parts)} tables -> {out_path}")
    for name, rows in parts:
        print(f"  {name}: {len(rows)}")


def render_insert(table, rows):
    body = "),(".join(",".join(fields) for fields in rows)
    return f"INSERT INTO `{table}` VALUES ({body});\n"


if __name__ == "__main__":
    main(sys.argv)
