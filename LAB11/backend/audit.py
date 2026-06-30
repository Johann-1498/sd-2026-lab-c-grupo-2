from datetime import datetime
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
LOG_FILE = os.path.join(BASE_DIR, "audit.log")


def write_log(event_type, message, username="anonymous", status="INFO"):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    log_line = f"[{timestamp}] [{status}] [{event_type}] user={username} - {message}\n"

    with open(LOG_FILE, "a", encoding="utf-8") as file:
        file.write(log_line)


def read_logs():
    try:
        with open(LOG_FILE, "r", encoding="utf-8") as file:
            return file.readlines()
    except FileNotFoundError:
        return []