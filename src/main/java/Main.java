import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args){

        File inputFolder = new File("personajes/");
        MongoClient mongoClient = new MongoClient("localhost",27017);

        DB database = mongoClient.getDB("myMongoDb");
        mongoClient.getDatabaseNames().forEach(System.out::println);
        System.out.println(database.toString());
        database.createCollection("test",null);
        DBCollection collection = database.getCollection("test");
        BasicDBObject document = new BasicDBObject();
        document.put("name", "Shubham");
        document.put("company", "Baeldung");
        collection.insert(document);
        database.getCollectionNames().forEach(System.out::println);
        BasicDBObject query = new BasicDBObject();
        query.put("company","Baeldung");
        DBCursor cursor = collection.find(query);
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
        collection.drop();
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);

        try {
            for (File file : inputFolder.listFiles()) {

                System.out.println(file);
                String outputName = file.getName().split("\\.")[0];
                File output = new File("personajes/"+outputName+".json");

                List readAll = mapper.readerFor(Map.class).with(csvSchema).readValues(file).readAll();
                ObjectMapper oMapper = new ObjectMapper();
                oMapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);
                System.out.println(oMapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
