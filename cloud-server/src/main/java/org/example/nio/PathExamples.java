package org.example.nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PathExamples {
    public static void main(String[] args) throws IOException {
        Path files = Path.of("serverFiles");
        Path rap = files.resolve("rap.txt");

        Path path = rap.resolve("..").resolve("rap.txt").resolve("..");
        System.out.println(path.normalize());
        System.out.println(rap.toAbsolutePath());

        path.getParent();
        System.out.println(System.getProperty("user.home"));

        Files.writeString(Path.of("serverFiles", "test.txt"),
                "Hello mir",
                StandardOpenOption.APPEND
        );



        // Files. - lots of static useful methods



    }
}
