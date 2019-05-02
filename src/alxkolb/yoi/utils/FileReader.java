package alxkolb.yoi.utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileReader {
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
