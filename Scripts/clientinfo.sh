#!/bin/bash
# Define variables
log_file="process_info.log"
server="client1@192.168.15.129"
current_time=$(date +"%Y-%m-%d %H:%M:%S")

# Gather process information
{
    echo "Process Information as of: $current_time"
    echo "=============================="
    echo "Process Tree:"
    ps axjf

    echo -e "\nDead or Zombie Processes:"
    ps aux | grep 'Z'  # Check for zombie processes

    echo -e "\nCPU Usage:"
    top -b -n1 | head -n 10  # Adjust as necessary

    echo -e "\nMemory Usage:"
    free -h  # Display memory usage

    echo -e "\nTop 5 Resource-Consuming Processes:"
    ps aux --sort=-%mem | head -n 6  
} > "$log_file"

# Securely copy the log file to the server
scp "$log_file" "$server:/"  
