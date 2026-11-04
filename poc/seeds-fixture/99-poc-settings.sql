-- PoC settings guard (2026-11-03/04): the views dereference
-- Settings.findAll(...)[0].value unguarded; ensure the keys exist
-- (no-ops when the prod dump already carries them).
INSERT INTO settings (version, setting_name, `value`)
  SELECT 0, 'showBible', 'false' FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM settings WHERE setting_name = 'showBible');
INSERT INTO settings (version, setting_name, `value`)
  SELECT 0, 'showAudio', 'false' FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM settings WHERE setting_name = 'showAudio');
