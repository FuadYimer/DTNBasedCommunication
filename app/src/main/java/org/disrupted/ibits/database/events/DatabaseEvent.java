package org.disrupted.ibits.database.events;


import org.disrupted.ibits.app.RumbleEvent;

/**
 * A ResourceEvent is published to the event bus whenever a new resource is available.
 * A resource is understood to be an element from the database such as a Status, a contact
 * a picture, an attached file, etc.
 *
 * @author
 */
public abstract class DatabaseEvent implements RumbleEvent {

}
