import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String PATH = "./src/main/java/data/";

    public static void main(String[] args) {
        
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String file_dataCSV = PATH + "data.csv";
        String file_dataJSON = PATH + "data.json";
        String file_dataXML = PATH + "data.xml";
        String file2_dataJSON = PATH + "data2.json";
        
        List<Employee> listCSV = parseCSV(columnMapping, file_dataCSV);
        String json = listToJson(listCSV);
        writeString(json, file_dataJSON);

        List<Employee> listXML = parseXML(file_dataXML);
        String json2 = listToJson(listXML);
        writeString(json2, file2_dataJSON);

        String json3 = readString(file2_dataJSON);
        List<Employee> jsonToList = jsonToList(json3);
        jsonToList.stream().forEach(System.out::println);
    }

    private static List<Employee> jsonToList(String json3) {
        List<Employee> employees = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        JSONParser parser = new JSONParser();
        try {
            JSONArray array = (JSONArray) parser.parse(json3);
            for (Object o : array) {
                employees.add(gson.fromJson(String.valueOf(o), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static String readString(String file2_dataJSON) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file2_dataJSON))){
            String s;
            while ((s= reader.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static List<Employee> parseXML(String file_dataXML) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(file_dataXML);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                employees.add(new Employee(
                        Long.parseLong(getTextContent("id", element)),
                        getTextContent("firstName", element),
                        getTextContent("lastName", element),
                        getTextContent("country", element),
                        Integer.parseInt(getTextContent("age", element))
                ));
            }
        }
        return employees;
    }

    private static String getTextContent(String tag, Element element) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
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
