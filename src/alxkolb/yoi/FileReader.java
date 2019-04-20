package alxkolb.yoi;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileReader {
    // Тестирование
    /*
    public static void main(String[] args) {
        //args = new String[]{"d","-f","rCCkn94RI33OBw","sftrFh4Bj"};
        FileReader fileReader = new FileReader();

        for (String s:args)
            System.out.println(s);

        String filePath = null;

        if (args.length == 2 && args[0].equals("-f"))
            filePath = args[1];
        else {
            System.out.println("Не указан файл");
            System.exit(0);
        }

        try {
            String input = fileReader.read(filePath);
            // ...
        } catch (IOException e) {
            System.err.println("Не найден файл " + e.getMessage());
        }
    }
    */

    public String read(String filePath) throws IOException {
        Scanner scanner = new Scanner(
                Paths.get(filePath));
        StringBuilder input = new StringBuilder();
        while (scanner.hasNextLine())
            input.append(
                    scanner.nextLine()).append("\n");
        scanner.close();
        return input.toString();
    }
}
