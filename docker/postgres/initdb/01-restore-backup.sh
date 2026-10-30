#!/bin/sh
# Ripristina backup/PrenotazioniDB.backup nel database creato dall'immagine.
# Eseguito da Postgres solo al primo avvio, quando il volume dei dati e' vuoto.
set -e

BACKUP=/backup/PrenotazioniDB.backup

if [ ! -f "$BACKUP" ]; then
    echo "Nessun backup in $BACKUP, database lasciato vuoto"
    exit 0
fi

echo "Ripristino $BACKUP in $POSTGRES_DB"
# Lo schema public esiste gia' nel database nuovo: lo escludo dalla lista.
# --no-owner/--no-privileges: gli oggetti diventano dell'utente $POSTGRES_USER.
pg_restore -l "$BACKUP" | grep -v ' SCHEMA - public ' > /tmp/restore.list
pg_restore --no-owner --no-privileges --exit-on-error \
    -U "$POSTGRES_USER" -d "$POSTGRES_DB" -L /tmp/restore.list "$BACKUP"
