import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportUtils {

    public static void toCSV(String fileName, List<Map<String, Object>> listToExport) {

        List<String> headers = listToExport.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());

        File[] files = new File(BrowserUtils.getDownloadPath()).listFiles();
        for (File file : files) {
            if ((file.isFile() && file.getName().contains(fileName))) {
                file.delete();
            }
        }

        String filepath =  BrowserUtils.getDownloadPath(fileName);

        try(FileWriter writer= new FileWriter(filepath, true);){
            for (String string : headers) {
//                System.out.println("string = " + string);
                writer.write(string);
                writer.write(",");
            }
            writer.write("\r\n");

            for (Map<String, Object> lmap : listToExport) {
                for (Map.Entry<String, Object> string2 : lmap.entrySet()) {
//                    System.out.println("string2 = " + string2);
                    writer.write(String.valueOf(string2.getValue()));
                    writer.write(",");
                }
                writer.write("\r\n");
            }
            System.out.println("Created file exported to '" + filepath + "'");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
