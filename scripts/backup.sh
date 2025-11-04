#!/bin/bash

################################################################################
# MySQL Backup Script for Floaty
#
# This script creates compressed backups of the MySQL database running in Docker.
# It includes retention policy, logging, and lock file protection.
#
# Usage: ./backup.sh
# Setup:
#   1. Create config.env with required variables
#   2. Make executable: chmod +x backup.sh
#   3. Add to crontab for automation
################################################################################

set -euo pipefail

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="${SCRIPT_DIR}/config.env"

# Load configuration
if [[ ! -f "$CONFIG_FILE" ]]; then
    echo "ERROR: Config file not found: $CONFIG_FILE"
    echo "Please create config.env with required variables."
    exit 1
fi

source "$CONFIG_FILE"

# Validate required variables
required_vars=(
    "DB_CONTAINER"
    "DB_NAME"
    "DB_USER"
    "DB_PASSWORD"
    "BACKUP_DIR"
    "RETENTION_COUNT"
)

for var in "${required_vars[@]}"; do
    if [[ -z "${!var:-}" ]]; then
        echo "ERROR: Required variable $var is not set in config.env"
        exit 1
    fi
done

# Create directories if they don't exist
mkdir -p "$BACKUP_DIR"
mkdir -p "${BACKUP_DIR}/logs"

# Lock file to prevent concurrent backups
LOCK_FILE="${SCRIPT_DIR}/.backup.lock"
LOG_FILE="${BACKUP_DIR}/logs/backup.log"

# Functions
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

error_exit() {
    log "ERROR: $*"
    rm -f "$LOCK_FILE"
    exit 1
}

cleanup() {
    rm -f "$LOCK_FILE"
}

trap cleanup EXIT

# Check if backup is already running
if [[ -f "$LOCK_FILE" ]]; then
    if kill -0 "$(cat "$LOCK_FILE")" 2>/dev/null; then
        log "Backup already running (PID: $(cat "$LOCK_FILE")). Skipping."
        exit 0
    else
        log "Stale lock file found. Removing."
        rm -f "$LOCK_FILE"
    fi
fi

# Create lock file
echo $$ > "$LOCK_FILE"

log "=========================================="
log "Starting MySQL backup"
log "Database: $DB_NAME"
log "Container: $DB_CONTAINER"

# Generate backup filename with timestamp
TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')
BACKUP_FILE="${BACKUP_DIR}/floaty_${TIMESTAMP}.sql"
BACKUP_FILE_GZ="${BACKUP_FILE}.gz"

# Check if Docker container is running
if ! docker ps --format '{{.Names}}' | grep -q "^${DB_CONTAINER}$"; then
    error_exit "Database container '$DB_CONTAINER' is not running"
fi

# Perform backup
log "Creating backup: ${BACKUP_FILE_GZ}"

if docker exec "$DB_CONTAINER" \
    mysqldump \
    --user="$DB_USER" \
    --password="$DB_PASSWORD" \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    --quick \
    --lock-tables=false \
    "$DB_NAME" | gzip > "$BACKUP_FILE_GZ"; then

    # Get backup size
    BACKUP_SIZE=$(du -h "$BACKUP_FILE_GZ" | cut -f1)
    log "Backup created successfully: $BACKUP_SIZE"
else
    error_exit "mysqldump failed"
fi

# Verify backup is not empty
if [[ ! -s "$BACKUP_FILE_GZ" ]]; then
    error_exit "Backup file is empty"
fi

# Apply retention policy - keep only last N backups
log "Applying retention policy (keep last $RETENTION_COUNT backups)"
BACKUP_COUNT=$(ls -1 "${BACKUP_DIR}"/floaty_*.sql.gz 2>/dev/null | wc -l)

if [[ $BACKUP_COUNT -gt $RETENTION_COUNT ]]; then
    DELETE_COUNT=$((BACKUP_COUNT - RETENTION_COUNT))
    log "Deleting $DELETE_COUNT old backup(s)"

    ls -1t "${BACKUP_DIR}"/floaty_*.sql.gz | tail -n "$DELETE_COUNT" | while read -r old_backup; do
        log "Deleting: $(basename "$old_backup")"
        rm -f "$old_backup"
    done
fi

# Apply age-based retention - delete backups older than MAX_AGE_DAYS
if [[ -n "${MAX_AGE_DAYS:-}" ]]; then
    log "Applying age-based retention (delete older than $MAX_AGE_DAYS days)"

    # Find and delete files older than MAX_AGE_DAYS
    OLD_FILES=$(find "$BACKUP_DIR" -name "floaty_*.sql.gz" -type f -mtime +$MAX_AGE_DAYS 2>/dev/null)

    if [[ -n "$OLD_FILES" ]]; then
        echo "$OLD_FILES" | while read -r old_backup; do
            log "Deleting old backup: $(basename "$old_backup")"
            rm -f "$old_backup"
        done
    else
        log "No backups older than $MAX_AGE_DAYS days found"
    fi
fi

# Calculate total backup directory size
TOTAL_SIZE=$(du -sh "$BACKUP_DIR" | cut -f1)
log "Total backup directory size: $TOTAL_SIZE"

log "Backup completed successfully"
log "=========================================="

exit 0
