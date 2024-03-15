package org.example.old;

public class CloudFile {
    private int id;
    private String name;
    private byte[] data;
    private String fileExtension;

    public CloudFile(int id, String name, byte[] data, String fileExtension) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.fileExtension = fileExtension;
    }

}
