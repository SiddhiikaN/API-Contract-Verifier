# API Contract Verifier

API Contract Verifier is a simple Java command-line tool that compares two JSON API responses (old vs new) and reports field-level differences: added fields, removed fields, changed values (OLD → NEW), and a basic risk score. It handles nested objects and arrays and uses Gson for JSON parsing.

## Features
- Field-level diffs: Added, removed, changed (with OLD → NEW)
- Nested support: works with nested objects and arrays
- Simple risk assessment: HIGH / MEDIUM / LOW / SAFE
- Minimal single-file implementation

## Prerequisites
- Java Development Kit (JDK 17 or higher)  
- gson-2.10.1.jar (or compatible Gson jar) placed in `lib/` folder  
- A terminal (PowerShell, CMD, or bash)

## How to Run
Clone the repository and run the tool from the project root.

### Clone
```bash
git clone https://github.com/SiddhiikaN/API-Contract-Verifier.git
cd API-Contract-Verifier
