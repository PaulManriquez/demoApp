-- Make clients.maps_link optional (nullable).
-- This matches the UI change where maps links are no longer required/present.
ALTER TABLE clients
    MODIFY COLUMN maps_link VARCHAR(255) NULL;

