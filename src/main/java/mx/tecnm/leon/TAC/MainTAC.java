package mx.tecnm.leon.TAC;

import mx.tecnm.leon.Lexer.Lexer;
import mx.tecnm.leon.Lexer.exceptions.LexicalException;
import mx.tecnm.leon.Parser.Exceptions.ParseError;
import mx.tecnm.leon.Parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainTAC {

    public static String readFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    public static void main(String[] args) {
        String source = """
                int y = 4;
                int x = (2 * 3) + (y * 1);
                
                if (x >= 10 and not false) {
                    print(x);
                } else {
                    x = -1;
                }
                
                while (y > 0) {
                    y = y - 1;
                }
                """;

        try {
            if (args.length > 0){
                source = readFile(args[0]);
            }

            var lexer = new Lexer(source);
            var tokens = lexer.generateTokens();

            var parser = new Parser(tokens);
            var statements = parser.parse();

            var generator = new TACGenerator();
            var tacCode = generator.generate(statements);

            System.out.println("Codigo Intermedio (TAC):");
            tacCode.forEach(System.out::println);

        } catch (LexicalException | ParseError e){
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException fileException){
            System.out.printf("Error al leer el archivo: %s", args[0]);
            System.exit(1);
        }
    }
}
