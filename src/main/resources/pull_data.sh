#!/bin/bash

# Create directory path for script
export SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
echo "Script directory: $SCRIPT_DIR"

# Log file path
LOG_FILE="$SCRIPT_DIR/script_log.log"

# Function to log messages
log() {
    echo "$(date +"%Y-%m-%d %H:%M:%S") - $1" >> "$LOG_FILE"
}

# Define tickers
tickers=("GOOGL" "AMZN" "AAPL" "TSLA" "WMT" "MSFT" "META" "COST" "LMT" "NOC" "UNH")

# Get current date
current_date=$(date "+%Y-%m-%d")
log "Current date: $current_date"

# Iterate over tickers
for ticker in "${tickers[@]}"; do
    log "Processing ticker: $ticker"
    
    # Download data
    log "Downloading data for $ticker..."
    python3 - <<END
import yfinance as yf
import pandas as pd
import datetime

ticker = "$ticker"
current_date = datetime.date.today()

data = yf.download(ticker, start="2004-01-01", end=current_date)

# Reset index to make 'Date' a column
data.reset_index(inplace=True)

# Add 'Ticker' column with ticker value
data['Symbol'] = ticker

# Reorder the columns
data = data[['Date', 'Symbol', 'Open', 'Close', 'Low', 'High', 'Volume']]

# Save to CSV
file_name = f"$SCRIPT_DIR/stock_data/{ticker}.csv"
data.to_csv(path_or_buf=file_name, index=False)
END

    log "Data downloaded and saved for $ticker."
done

log "Script execution completed."
