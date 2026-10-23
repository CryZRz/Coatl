package mx.tecnm.leon;

import mx.tecnm.leon.Lexer.Lexer;
import mx.tecnm.leon.Lexer.exceptions.LexicalException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Coatl {

    public static String readFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    static void main(String[] args) {

        if (args.length == 0){
            System.out.print("Uso: coatl 'arhcivo.coalt'");
            System.exit(1);
        }

        try {
            var fileContent = readFile(args[0]);
            var lexer = new Lexer(fileContent);
            lexer.generateTokens()
                    .forEach(System.out::println);
        }catch (LexicalException lexicalException){
            System.out.print(lexicalException.getMessage());
            System.exit(1);
        }
        catch (IOException fileException){
            System.out.printf("Error al leer el archivo: %s", args[0]);
            System.exit(1);
        }
    }
}
