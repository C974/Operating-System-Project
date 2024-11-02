#!/bin/bash

# Define variables
search_date=$(date +"%Y-%m-%d %H:%M:%S")
bigfile="bigfile"
email="am2104114@qu.edu.qa"

# Find files larger than 1M
find ~ -type f -size +1M > "$bigfile"
count=$(wc -l < "$bigfile")

# Log the search date and number of files found
{
    echo "Search Date: $search_date"
    echo "Number of files larger than 1M: $count"
    echo "Files:"
    cat "$bigfile"
} >> "$bigfile"

# Check if bigfile is not empty and send email
if [ "$count" -gt 0 ]; then
    mail -s "Files larger than 1M found" "$email" < "$bigfile"
fi
