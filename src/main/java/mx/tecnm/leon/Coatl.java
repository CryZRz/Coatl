package mx.tecnm.leon;

import mx.tecnm.leon.Backend.AssemblyCreate;
import mx.tecnm.leon.Backend.AssemblyGenerator;
import mx.tecnm.leon.IRGenerator.IRGenerator;
import mx.tecnm.leon.Lexer.Lexer;
import mx.tecnm.leon.Lexer.Exceptions.LexicalException;
import mx.tecnm.leon.Parser.Parser;
import mx.tecnm.leon.Semantic.SemanticAnalyzer;

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
            var tokens = lexer.generateTokens();

            var parser = new Parser(tokens);
            var statements = parser.parse();

            var semantic = new SemanticAnalyzer(statements);
            semantic.analyze();

            var irgenerator = new IRGenerator(statements, semantic.getExpressionTypes());
            var instruction = irgenerator.generate();

            var asmGenerator = new AssemblyGenerator(instruction);
            var asmCode = asmGenerator.generate();
            if (args.length > 2){
                if (args[1] == "-c"){
                    System.out.println(asmCode);
                }
            }
            var assemblyCreate = new AssemblyCreate(asmCode);
            assemblyCreate.generate();
        }catch (LexicalException lexicalException){
            System.out.print(lexicalException.getMessage());
            System.exit(1);
        }
        catch (IOException fileException){
            System.out.printf("Error al leer el archivo: %s", args[0]);
            System.exit(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
