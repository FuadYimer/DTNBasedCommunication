package org.disrupted.ibits.database.events;

/**
 * @author
 */
public class FileInsertedEvent extends DatabaseEvent {

    public String filename;
    public String status_uid_base64;

    public FileInsertedEvent(String filename, String status_uid_base64) {
        this.filename = filename;
        this.status_uid_base64 = status_uid_base64;
    }

    @Override
    public String shortDescription() {
        return "";
    }
}
