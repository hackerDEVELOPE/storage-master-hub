package org.example.old;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatHandler implements Runnable {

    private DataInputStream is;
    private DataOutputStream os;
    private final String serverDir = "serverFiles/"; // TODO: replace magic words and do the same for client side

    List<CloudFile> cloudFiles = new ArrayList<>();


    private int fileNameLength;
    private String fileName;

    private File serverFiles;

    public ChatHandler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());

        System.out.println("Client was accepted");


        sendListOfFiles(serverDir);
    }


    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    private void sendListOfFiles(String dir) throws IOException {
        os.writeUTF("#list#");
        List<String> files = getFiles(serverDir);
        os.writeInt(files.size());
        for (String file : files) {
            os.writeUTF(file);
        }
        os.flush();
    }

    private String sendServerFiles() {
        // OLD METHOD?
        serverFiles = new File("serverFiles");
        String[] filesArr = serverFiles.list();
        StringBuilder sb = new StringBuilder();
        sb.append("/serverFiles ");
        for (int i = 0; i < filesArr.length; i++) {
            sb.append(filesArr[i] + "\n ");
        }
        System.out.println(sb);
        return sb.toString();

    }


    @Override
    public void run() {
        byte[] buf = new byte[256];
        try {

            while (true) {
                String command = is.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    String fileName = is.readUTF();
                    long len = is.readLong();
                    File file = Path.of(serverDir).resolve(fileName).toFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        for (int i = 0; i < (len + 255) / 256; i++) {
                            int read = is.read(buf);
                            fos.write(buf, 0, read);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    sendListOfFiles(serverDir);
                }

            }
        } catch (Exception e) {
            System.out.println("Connection was broken");
        }
    }
}
