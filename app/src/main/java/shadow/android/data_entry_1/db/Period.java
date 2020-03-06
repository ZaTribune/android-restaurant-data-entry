package shadow.android.data_entry_1.db;

import java.util.Date;

public class Period {
    private long id;
    private long start;
    private long end;
    private long client;

    public Period() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStart() {
        return new Date(start);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Date getEnd() {
        return new Date(end);
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getClient() {
        return client;
    }

    public void setClient(long client) {
        this.client = client;
    }
}
