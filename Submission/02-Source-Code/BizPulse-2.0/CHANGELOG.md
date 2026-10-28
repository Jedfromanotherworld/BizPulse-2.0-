# 2.0.0

Eight revisions: scanner fallback, offline AI and FIFO batches, Cash/QR payment, sales reports, kiosk product grid, cashier shifts, owner scan-to-add Code128, and geographic supplier maps.

New identity supports installation alongside v1. New supplied logo and QR replace old branding. Profile images are omitted. Database schema is version 5, with explicit migrations 1→2→3→4→5 and no destructive fallback. Backups use v2 format and include new records and selected images. Old v1 JSON backups are rejected rather than partially imported.
