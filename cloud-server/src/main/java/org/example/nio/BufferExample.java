package org.example.nio;

import java.nio.ByteBuffer;

public class BufferExample {
    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put((byte) 'a');
        buf.put((byte) 'b');

        buf.flip();

        while (buf.hasRemaining()){
            System.out.println((char) buf.get());
        }
        buf.limit(5);
        buf.put((byte) 'a');
        buf.put((byte) 'b');
        buf.flip();

        System.out.println();

        while (buf.hasRemaining()){
            System.out.println((char) buf.get());
        }

        // buf.rewind() position to zero
        // buf.clear() position zero, limit = capacity


    }
}
