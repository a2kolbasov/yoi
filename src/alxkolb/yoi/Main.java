package alxkolb.yoi;

import alxkolb.yoi.utils.FileReader;
import alxkolb.yoi.run.Run;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath, inputData;
            FileReader fileReader = new FileReader();
            if (args.length == 1){
                filePath = args[0];
                inputData = fileReader.read(filePath);
                System.out.println(
                        filePath+
                                ":\n////\n" +
                                inputData +
                                "\n////\n"
                );
            } else {
                inputData = null;
                System.out.println("    YOI    \nНеверный параметр запуска.\nЗадайте имя входного файла.");
            }
            if (inputData!=null)
                new Run(inputData);
        } catch (IOException e){
            System.err.println("Не найден файл " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
