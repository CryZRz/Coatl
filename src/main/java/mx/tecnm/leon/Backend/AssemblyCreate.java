package mx.tecnm.leon.Backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AssemblyCreate {
    private final String source;


    public AssemblyCreate(String source){
        this.source = source;
    }

    public void generate() throws IOException, InterruptedException {
        Files.writeString(
                Path.of("index.asm"),
                source
        );

        var processAsm = new ProcessBuilder(
                "nasm",
                "-f", "elf64",
                "index.asm",
                "-o", "index.o"
        ).inheritIO().start();

        int exitCodeAsm = processAsm.waitFor();

        var processGcc = new ProcessBuilder(
                "gcc",
                "-no-pie",
                "index.o",
                "-o", "program"
        ).inheritIO().start();

        int exitCodeGcc = processGcc.waitFor();
    }

}
