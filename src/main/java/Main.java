import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {

    public static final String PATH = "./src/main/java/data/";

    public static void main(String[] args) {
        
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String file_dataCSV = PATH + "data.csv";
        String file_dataJSON = PATH + "data.json";
        
        List<Employee> listCSV = parseCSV(columnMapping, file_dataCSV);
        String json = listToJson(listCSV);
        writeString(json, file_dataJSON);
    }

    private static void writeString(String json, String file_dataJSON) {
        try (FileWriter fileWriter = new FileWriter(file_dataJSON)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> listCSV) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(listCSV, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String file_dataCSV) {
        List<Employee> data = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(file_dataCSV))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            data = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
