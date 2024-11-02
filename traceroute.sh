#!/bin/bash

# Log file
LOG_FILE="network.log"

# Function to log messages
log_message() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Display routing table
log_message "Checking routing table..."
ip route | tee -a "$LOG_FILE"

# Display hostname
log_message "Hostname: $(hostname)" | tee -a "$LOG_FILE"

# Test local DNS server
log_message "Testing local DNS server..."
nslookup google.com | tee -a "$LOG_FILE"

# Traceroute to google.com
log_message "Tracing route to google.com..."
traceroute google.com | tee -a "$LOG_FILE"

# Ping google.com
log_message "Pinging google.com..."
if ping -c 2 google.com > /dev/null; then
    log_message "Successfully pinged google.com."
else
    log_message "Failed to ping google.com."
fi
