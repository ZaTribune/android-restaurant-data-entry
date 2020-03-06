package shadow.android.data_entry_1.db;

import java.io.Serializable;

public class Client implements Serializable {
    private long id;
    private String name;
    private byte[] thump;

    public Client(long id, String name, byte[] thump) {
        this.id = id;
        this.name = name;
        this.thump = thump;
    }

    public Client(String name, byte[] thump) {
        this.name = name;
        this.thump = thump;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getThump() {
        return thump;
    }

    public void setThump(byte[] thump) {
        this.thump = thump;
    }

}
