#!/bin/bash
set -e  # Exit on errors

SERVER_IP="192.168.15.132"
SFTP_USER="client1"
INVALID_ATTEMPTS_LOG="invalid_attempts.log"
CLIENT_LOG="client_timestamp_invalid_attempts.log"
MAX_ATTEMPTS=3
ATTEMPT_COUNT=0

# Check and install dependencies
if ! command -v ssh &>/dev/null || ! command -v sftp &>/dev/null; then
    echo "ssh or sftp not found. Installing..."
    sudo apt update
    sudo apt install -y ssh
fi

if ! command -v sshpass &>/dev/null; then
    echo "sshpass not found. Installing..."
    sudo apt update
    sudo apt install -y sshpass
fi

log_invalid_attempt() {
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "$timestamp - Invalid login attempt for user: $username" >> $INVALID_ATTEMPTS_LOG
}

copy_log_to_server() {
    echo "Copying log file to server..."
    ssh -o StrictHostKeyChecking=no $SFTP_USER@$SERVER_IP "mkdir -p logs"
    sftp -oBatchMode=no $SFTP_USER@$SERVER_IP <<< "put $INVALID_ATTEMPTS_LOG logs/"
}

# Request username and password interactively
read -p "Enter username: " username
read -s -p "Enter password: " password
echo  # Add a new line for better display

# Attempt login
while [ $ATTEMPT_COUNT -lt $MAX_ATTEMPTS ]; do
    sshpass -p "$password" ssh -o PreferredAuthentications=password -o PubkeyAuthentication=no $username@$SERVER_IP "exit" &>/tmp/ssh_output.log
    if [ $? -eq 0 ]; then
        echo "Login successful!"
        exit 0
    else
        echo "Invalid credentials. Please try again."
        log_invalid_attempt
        ((ATTEMPT_COUNT++))
        if [ $ATTEMPT_COUNT -lt $MAX_ATTEMPTS ]; then
            read -s -p "Enter password: " password
            echo  # Add a new line for better display
        fi
    fi
done

# Handle excessive invalid attempts
echo "Unauthorized user!"

cp $INVALID_ATTEMPTS_LOG $CLIENT_LOG
copy_log_to_server

# Schedule logout
echo "Logging out in 30 seconds..."
sleep 30 && pkill -KILL -u $USER
exit 1
