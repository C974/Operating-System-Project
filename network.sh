#!/bin/bash

# Log file
LOG_FILE="network.log"

# Function to log messages
log_message() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Check for required tools
command -v ping > /dev/null || { log_message "ping command not found"; exit 1; }
command -v traceroute > /dev/null || { log_message "traceroute command not found"; exit 1; }

# Input target IPs
TARGET_IPS=$1

# Connectivity test loop
for run in {1..3}; do
    log_message "Connectivity test run #$run"
    
    # Ping the target IPs
    log_message "Pinging $TARGET_IPS..."
    if ping -c 2 $TARGET_IPS > /dev/null; then
        log_message "Connectivity with $TARGET_IPS is ok"
    else
        log_message "$TARGET_IPS is not responding."
        
        # Run traceroute.sh if not responding
        ./traceroute.sh
    fi
    
    sleep 5  # Adding a delay before the next test
done
