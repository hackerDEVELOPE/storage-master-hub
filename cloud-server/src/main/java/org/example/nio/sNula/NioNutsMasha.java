package org.example.nio.sNula;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioNutsMasha {
    private ServerSocketChannel ssc;
    private Selector selector;


    public NioNutsMasha() throws IOException {
        ssc = ServerSocketChannel.open();
        selector = Selector.open();

    }

    public void marie() throws IOException {
        ssc.bind(new InetSocketAddress(8189));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT); // TODO: register?


        while (ssc.isOpen()) {
            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey request = iterator.next();

                if (request.isAcceptable()) {
                    handleAccept();
                }
                if (request.isReadable()) {
                    handleRead(request);
                }

                iterator.remove();

            }

        }


    }

    private void handleRead(SelectionKey request) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        SocketChannel sc = (SocketChannel) request.channel();

        StringBuilder sb = new StringBuilder();

        while (sc.isOpen()) {
            int read = sc.read(buf);
            if (read < 0) {
                sc.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()){
                sb.append((char) buf.get());
            }
            buf.clear();
        }
        System.out.println(sb);
    }

    private void handleAccept() throws IOException {
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        sc.write(ByteBuffer.wrap("HI KIDS".getBytes()));
    }

}
