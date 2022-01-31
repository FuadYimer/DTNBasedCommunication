package org.disrupted.ibits.database.events;

/**
 * @author
 */
public class StatusDeletedEvent extends StatusDatabaseEvent {

    public final String uuid;
    public final long dbid;

    public StatusDeletedEvent(String uuid, long dbid){
        this.uuid = uuid;
        this.dbid = dbid;
    }

    @Override
    public String shortDescription() {
        return "uuid="+uuid+" dbid="+dbid;
    }
}
