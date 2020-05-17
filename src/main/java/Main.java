import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

/**
 * Query personajes de DC con Telekinesis 'db.filteredCharactes.find({$and:[{"publisher": "DC Comics"},{"powers": {$in: ["Telekinesis"]}}]}).pretty()'
 * Query comic con mas personajes ' db.filteredComics.aggregate([ {$unwind: "$characterIds"}, { $group: { _id : "$title", len: {$sum : 1} } }, { $sort : { len : -1 }}, { $limit : 25 } ])'
 * Query Lista decreciente de color de ojos ' db.characters_info.aggregate({$group : { _id: '$EyeColor', count: {$sum : 1}}},{$sort: {count: -1}}) '
   Query para obtener los comics en los que sale un personaje: 
        var character = db.characters.findOne({'name' : /Captain America/})
        db.charactersToComics.find({"characterID": character.characterID}).pretty()
   Query para obtener el ratio entre hombres y mujeres en un universo concreto(En este caso Marvel)
        var males= db.marvel_dc_characters.find({$and:[{"Gender": "Male"},{"Universe": "Marvel"}]}).count()
        var females= db.marvel_dc_characters.find({$and:[{"Gender": "Female"},{"Universe": "Marvel"}]}).count()

 */
public class Main {


    public static void main(String[] args){

        Map<String,Character> characters = new TreeMap<>();
        Map<String,Comic> comics = new TreeMap<>();
        Map<String,List<Long>> charactersInComic = new TreeMap<>();
        File inputFolder = new File("personajes/");
        MongoClient mongoClient = new MongoClient("localhost",27017);

        DB database = mongoClient.getDB("test");
        mongoClient.getDatabaseNames().forEach(System.out::println);
        System.out.println(database.toString());
//        database.createCollection("test",null);
//        DBCollection collection = database.getCollection("test");
//        BasicDBObject document = new BasicDBObject();
//        document.put("name", "Shubham");
//        document.put("company", "Baeldung");
//        collection.insert(document);
//        database.getCollectionNames().forEach(System.out::println);
//        BasicDBObject query = new BasicDBObject();
//        query.put("company","Baeldung");
//        DBCursor cursor = collection.find(query);
//        while (cursor.hasNext()){
//            System.out.println(cursor.next());
//        }
//        collection.drop();
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        csvSchema.withColumnSeparator(';');
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        mapper.setDateFormat(new SimpleDateFormat());


        for (File file : inputFolder.listFiles()) {
            String outputName = file.getName().split("\\.")[0];

            File output = new File("personajes/" + outputName + ".json");
            try {
                if(!file.getName().split("\\.")[1].equals("json")) {
                    System.out.println(file.getName().split("\\.")[1]);
                    sleep(500);

                    List readAll = mapper.readerFor(Map.class).with(csvSchema).readValues(file).readAll();

                    ObjectMapper oMapper = new ObjectMapper();
                    oMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));

                    oMapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);
                    List<Map<String,String>> readAllList = (List<Map<String,String>>) readAll;

                    if(output.getName().contains("characters")){
                        readAllList.stream().forEach(entry->{
                            Long id = 0L;
                            String name = "";
                            Character current = new Character(id,name);
                            if(entry.get("name")!=null&&!characters.containsKey(entry.get("name"))){
                                id = Long.parseLong(entry.get("characterID"));
                                name = entry.get("name");
                            }else if(output.getName().equals("characters_info.json")){

                                if(!characters.containsKey(entry.get("Name"))){
                                    id = Long.parseLong(entry.get("ID"));
                                }else{
                                    id = characters.get(entry.get("Name")).getId();
                                }
                                name = entry.get("Name");
                                current.setName(name);
                                current.setId(id);
                                current.setAlignment(entry.get("Alignment"));
                                current.setEyeColor(entry.get("EyeColor"));
                                current.setGender(entry.get("Gender"));
                                current.setRace(entry.get("Race"));
                                current.setHairColor(entry.get("HairColor"));
                                current.setPublisher(entry.get("Publisher"));
                                current.setSkinColor(entry.get("SkinColor"));
                                String height = entry.get("Height");
                                try {
                                    current.setHeight(Float.valueOf(entry.get("Height")));
                                    current.setWeight(Float.valueOf(entry.get("Weight")));
                                }catch (NumberFormatException e){
                                    System.out.println(height);
                                }
                            }

                            if(output.getName().contains("stats")){

                                current = characters.get(entry.get("Name"));
                                if(current==null){
                                    name = entry.get("Name");
                                    characters.put(name,new Character(new Random().nextLong(),name));
                                    current = characters.get(entry.get("Name"));
                                }
                                entry.remove("Name");
                                current.setStats(entry);
                                characters.put(name,current);
                            }
                            characters.put(name,current);

                        });
                    }
                    if(output.getName().startsWith("superheroes_power_matrix")){
                        readAllList.parallelStream().forEach(entry->{
                            List<String> powers = new ArrayList<>();
                            Character current = characters.get(entry.get("Name"));

                            if(current==null){
                                String name = entry.get("Name");
                                characters.put(name,new Character(new Random().nextLong(),name));
                                current = characters.get(entry.get("Name"));
                            }
                            for (String s : entry.keySet()) {
                                if(!s.equals("Name")){
                                    if(entry.get(s).equals("TRUE")){
                                        powers.add(s);
                                    }
                                }
                            }
                            current.setPowers(powers);
                            System.out.println(current.getPowers().toString());
                        });
                    }

                    if(output.getName().startsWith("charactersToComics")){
                        readAllList.parallelStream().forEach(entry->{
                            long id = 0;
                            try {
                                id = Long.parseLong(entry.get("comicID"));
                            }catch (NumberFormatException e){
                                System.out.println(entry.get("comicID"));
                            }
                            if(!charactersInComic.containsKey(String.valueOf(id))){
                                charactersInComic.put(String.valueOf(id),new ArrayList());
                            }
                            try {
                                charactersInComic.get(String.valueOf(id)).add(Long.parseLong(entry.get("characterID")));
                            }catch (NullPointerException e){
                                System.out.println("Character List ->"+charactersInComic.get(String.valueOf(id)));
                                System.out.println("Character Id -> "+Long.parseLong(entry.get("characterID")));
                                charactersInComic.get(String.valueOf(id)).add(Long.parseLong(entry.get("characterID")));
                            }
                        });
                    }

                    if(output.getName().startsWith("comics")){
                        readAllList.parallelStream().forEach(entry->{
                            long id = 0;
                            try {
                                id = Long.parseLong(entry.get("comicID"));
                            }catch (NumberFormatException e){
                                System.out.println(entry.get("comicID"));
                            }
                            int issueNumber = Integer.parseInt(entry.get("issueNumber"));
                            String title = entry.get("title");
                            comics.put(String.valueOf(id),new Comic(id,title,issueNumber,charactersInComic.get(String.valueOf(id))));
                        });
                    }
                }
            } catch (IOException e) {

                try {

                    List readAll = mapper.readerFor(Map.class).with(csvSchema).readValues(new InputStreamReader(new FileInputStream(file), "ISO-8859-1")).readAll();
                    ObjectMapper oMapper = new ObjectMapper();
                    oMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
                    oMapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);
                    List<Map<String,String>> readAllList = (List<Map<String,String>>) readAll;


                        readAllList.stream().forEach(entry->{
                            Long id = 0L;
                            String name = "";
                            if(!characters.containsKey(entry.get("Name"))){
                                id = Long.parseLong(entry.get("ID"));
                            }else{
                                id = characters.get(entry.get("Name")).getId();
                            }
                            name = entry.get("Name");

                            characters.put(name,new Character(id,name));

                        });

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines( Paths.get(output.toURI()), StandardCharsets.UTF_8))
            {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            BasicDBList dbo = (BasicDBList) com.mongodb.util.JSON.parse(contentBuilder.toString());
            List<DBObject> list = new ArrayList<>();
            list.add(dbo);
//save them into database:
            for (Object o : dbo) {

                database.getCollection(output.getName().split("\\.")[0]).insert((DBObject) o);
            }
        }

        Stream<Character> charStream = characters.values().stream();
        List<DBObject> charList = new ArrayList<>();
        charStream.forEach(s ->{
            ObjectMapper oMapper = new ObjectMapper();
            try {
                charList.add(new BasicDBObject((BasicDBObject)JSON.parse(oMapper.writeValueAsString(s))));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        Stream<Comic> comicStream = comics.values().stream();
        List<DBObject> comicList = new ArrayList<>();
        comicStream.forEach(s ->{
            ObjectMapper oMapper = new ObjectMapper();
            try {
                comicList.add(new BasicDBObject((BasicDBObject)JSON.parse(oMapper.writeValueAsString(s))));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });


        database.getCollection("filteredCharactes").insert(charList);
        database.getCollection("filteredComics").insert(comicList);

        System.out.println(characters.keySet());
        database.getCollectionNames().forEach(System.out::println);

        Menu m = new Menu();
            m.selectQuery();
           

    }
}
