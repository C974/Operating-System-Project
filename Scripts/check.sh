#!/bin/bash

# Log file for permission changes
LOG_FILE="perm_change.log"

# Find all files with 777 permissions and process them
echo "Searching for files with 777 permissions..."

# Find files, display, change permissions, and log the actions
find / -type f -perm 777 2>/dev/null | while read -r file; do
    echo "Found: $file"
    chmod 700 "$file"
    echo "$(date): Changed permissions of $file from 777 to 700" >> "$LOG_FILE"
done

echo "All changes have been logged in $LOG_FILE."
