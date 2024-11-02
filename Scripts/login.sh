#!/bin/bash

# Log file for invalid attempts
INVALID_LOG="invalid_attempts.log"

# Function to log invalid attempts
log_invalid_attempt() {
    local username="$1"
    echo "$(date): Invalid login attempt for user: $username" >> "$INVALID_LOG"
}

# Function to perform login
perform_login() {
    local username="$1"
    local password="$2"
    # Attempt login via SSH (dummy command with sshpass)
    if sshpass -p "$password" ssh -o StrictHostKeyChecking=no "$username@SERVER_IP" exit; then
        echo "Login successful for user: $username"
        return 0
    else
        return 1
    fi
}

# Main script logic
USERNAME="$1"
PASSWORD="$2"
ATTEMPTS=0
MAX_ATTEMPTS=3

while [ "$ATTEMPTS" -lt "$MAX_ATTEMPTS" ]; do
    if perform_login "$USERNAME" "$PASSWORD"; then
        exit 0
    else
        ((ATTEMPTS++))
        log_invalid_attempt "$USERNAME"
        echo "Invalid login attempt $ATTEMPTS for user: $USERNAME"
    fi
done

# Handle excessive invalid attempts
echo "Unauthorized user!"
# Create timestamped log file for invalid attempts
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
cp "$INVALID_LOG" "client_timestamp_invalid_attempts_$TIMESTAMP.log"

# Use SFTP to copy the log to the server
sftp "$USERNAME@SERVER_IP" <<EOF
put client_timestamp_invalid_attempts_$TIMESTAMP.log
bye
EOF

# Schedule logout after 30 seconds
sleep 30
logout
