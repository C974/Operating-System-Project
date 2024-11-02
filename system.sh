#!/bin/bash

# Define log file names
DISK_LOG="disk_info.log"
MEM_CPU_LOG="mem_cpu_info.log"

# Function to gather disk information
function gather_disk_info {
    echo "Disk Information for HOME Directory:" | tee $DISK_LOG
    echo "------------------------------------" | tee -a $DISK_LOG
    du -h --max-depth=1 $HOME | tee -a $DISK_LOG
    echo "" | tee -a $DISK_LOG
    df -h $HOME | tee -a $DISK_LOG
}

# Function to gather memory and CPU information
function gather_mem_cpu_info {
    echo "Memory and CPU Information:" | tee $MEM_CPU_LOG
    echo "----------------------------" | tee -a $MEM_CPU_LOG

    # Memory usage
    free -h | awk '/^Mem:/ {printf("Used Memory: %s, Free Memory: %s\n", $3, $4)}' | tee -a $MEM_CPU_LOG
    # CPU model and number of cores
    lscpu | grep "Model name" | tee -a $MEM_CPU_LOG
    lscpu | grep "^CPU\(s\):" | tee -a $MEM_CPU_LOG
}

# Call the functions
gather_disk_info
gather_mem_cpu_info

# Print completion message
echo "Information gathered successfully!"
