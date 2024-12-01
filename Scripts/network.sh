#!/bin/bash

# Predefined client IPs
client1_ip="192.168.15.130"
client2_ip="192.168.15.131"

# Checking if the ping command is found on the system
if ! command -v ping &> /dev/null; then
    echo "ping could not be found, installing ..."
    sudo apt install -y iputils-ping &> /dev/null || sudo dnf install -y iputils &> /dev/null
    echo "ping installed successfully."
fi

# Checking if the traceroute command is found on the system
if ! command -v traceroute &> /dev/null; then
    echo "traceroute could not be found, installing ..."
    sudo apt install -y traceroute &> /dev/null || sudo dnf install -y traceroute &> /dev/null
    echo "traceroute installed successfully."
fi

for i in {1..3}; do
    echo "Pinging client 1 IP: $client1_ip ..."
    if ping -c 3 -W 3 "$client1_ip"; then    # -c 3 means send 3 packets, -W means wait time is 3 seconds
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "$timestamp: Connectivity with $client1_ip is ok" | tee -a ./network.log  # Appends output to network.log
    else
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "Connection with $client1_ip is down at $timestamp"
        echo "Connection with $client1_ip is down at $timestamp" | tee -a ./network.log
        ./traceroute.sh "$client1_ip"
    fi

    echo "Pinging client 2 IP: $client2_ip ..."
    if ping -c 3 -W 3 "$client2_ip"; then
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "$timestamp: Connectivity with $client2_ip is ok" | tee -a ./network.log
    else
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "Connection with $client2_ip is down at $timestamp"
        echo "Connection with $client2_ip is down at $timestamp" | tee -a ./network.log
        ./traceroute.sh "$client2_ip"
    fi
done
