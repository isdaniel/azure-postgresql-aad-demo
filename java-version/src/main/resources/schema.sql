-- Azure PostgreSQL AAD Demo Schema
-- This script creates the necessary tables for the demo application

-- Drop table if exists (for demo purposes)
DROP TABLE IF EXISTS todo;

-- Create todo table
CREATE TABLE todo (
    id BIGINT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    details VARCHAR(4096),
    done BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on description for better query performance
CREATE INDEX idx_todo_description ON todo(description);

-- Create index on done status for filtering
CREATE INDEX idx_todo_done ON todo(done);

-- Insert a welcome message (optional)
INSERT INTO todo (id, description, details, done) 
VALUES (0, 'welcome', 'Welcome to Azure PostgreSQL with AAD authentication!', true);

-- Display table information
\dt todo;