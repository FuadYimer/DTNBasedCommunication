package org.disrupted.ibits.database.events;

/**
 * @author
 */
public class HashtagInsertedEvent extends DatabaseEvent {

    public final String hashtag;

    public HashtagInsertedEvent(String hashtag) {
        this.hashtag = hashtag;
    }


    @Override
    public String shortDescription() {
        return hashtag;
    }
}
