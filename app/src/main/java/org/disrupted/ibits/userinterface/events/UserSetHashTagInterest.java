package org.disrupted.ibits.userinterface.events;

/**
 * @author
 */
public class UserSetHashTagInterest extends UserInteractionEvent {

    public final String hashtag;
    public final int levelOfInterest;

    public UserSetHashTagInterest(String hashtag, int levelOfInterest) {
        this.hashtag = hashtag;
        this.levelOfInterest = levelOfInterest;
    }

    @Override
    public String shortDescription() {
        return hashtag + "("+levelOfInterest+")";
    }
}
