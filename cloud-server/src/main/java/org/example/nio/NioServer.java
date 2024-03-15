package org.example.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private ServerSocketChannel server;
    private Selector selector;

    private Path path;

    private RandomAccessFile fileToRead;
    private FileChannel fileChannel;




    public NioServer() throws IOException {
        server = ServerSocketChannel.open();
        selector = Selector.open();

        server.bind(new InetSocketAddress(8189));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);

        path = Path.of(System.getProperty("user.home"));

    }

    public void start() throws IOException {
        while (server.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept();
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        // how is socketChannel registered to SELECTOR??????
        ByteBuffer buf = ByteBuffer.allocate(1024);
        SocketChannel channel = (SocketChannel) key.channel();


        StringBuilder sb = new StringBuilder();
        while (channel.isOpen()) {
            int read = channel.read(buf);
            if (read < 0) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                sb.append((char) buf.get());
            }
            buf.clear();
        }
//        sb.append(path).append(">");
        if (sb.toString().startsWith("\uFFFF")) {
            return;
        }

        if (sb.toString().startsWith("cat")) {
            path = (path.resolve(sb.substring(4, sb.length() - 2)) // get rid of "cat" and "/r/n"
                    .normalize()
                    .toAbsolutePath()
            );
            if (path.toFile().exists()) {
                path = Path.of(path.toFile().getCanonicalPath()); // for the right cases! uppercase lowercase

                fileToRead = new RandomAccessFile(path.toString(), "r");
                fileChannel = fileToRead.getChannel();

                fileChannel.read(buf);
                buf.put(("\r\n").getBytes());
//                buf.flip();
                channel.write(ByteBuffer.wrap(buf.array()));
                buf.clear();

                path = path.resolve("..").normalize();

            } else {
                path = path.resolve("..").normalize();
                channel.write(ByteBuffer.wrap(("Can't find that file\r\n").getBytes()));
            }

        } else if (sb.toString().startsWith("cd")) {
            path = (path.resolve(sb.substring(3, sb.length() - 2)) // get rid of "cd" and "/r/n"
                    .normalize()
                    .toAbsolutePath()
            );
            if (path.toFile().exists()) {
                path = Path.of(path.toFile().getCanonicalPath());
            } else {
                path = path.resolve("..").normalize();
                channel.write(ByteBuffer.wrap(("Can't find that path\r\n").getBytes()));

            }
        } else if (sb.toString().trim().equals("ls")) handleListOfFiles(channel);
        else handleUnknownCommand(channel, sb);


        byte[] pathArray = (path.toString() + ">").getBytes(StandardCharsets.UTF_8);
        channel.write(ByteBuffer.wrap(pathArray));

//        for (SelectionKey selectorKey : selector.keys()) {
//            if (selectorKey.isValid() && selectorKey.channel() instanceof SocketChannel sc) {
//                sc.write(ByteBuffer.wrap(message));
//            }
//        }

    }

    private void handleListOfFiles(SocketChannel channel) throws IOException {
        String[] listOfFiles = path.toFile().list();
        for (int i = 0; i < listOfFiles.length; i++) {
            byte[] file = (listOfFiles[i] + "\r\n").getBytes();
            channel.write(ByteBuffer.wrap(file));
        }
    }

    private void handleUnknownCommand(SocketChannel channel, StringBuilder sb) throws IOException {
        sb.setLength(sb.length() - 2); // to get rid of "\r\n"
        sb.insert(0, '"').append("\" - in unknown command\r\n");
        byte[] message = sb.toString().getBytes(StandardCharsets.UTF_8);
        channel.write(ByteBuffer.wrap(message));
    }


    private void handleAccept() throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        channel.write(ByteBuffer.wrap("Welcome in gucci terminal!\r\n".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap("Hello how are you fine thank you!\r\n".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap((path.toString() + ">").getBytes(StandardCharsets.UTF_8)));

    }
}
