import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import com.mongodb.util.JSON;

import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class QueryExecutioner {

    private DB database;
    public QueryExecutioner(){
        MongoClient mongoClient = new MongoClient("localhost",27017);
        database = mongoClient.getDB("grupo6");
    }

    void executeListOfComicsOfACharacter(String charName){
        BasicDBObject query = new BasicDBObject("name",charName);
        DBCursor cursor = database.getCollection("filteredCharacters").find(query);
        DBObject result = null;
        if(cursor.hasNext())
            result=cursor.next();

        if(result != null){
            int charId = (Integer)result.get("id");
            System.out.println("Finding ->"+charName+ "id="+charId);

            BasicDBObject queryComics = new BasicDBObject();
            queryComics.put("characterIds",new BasicDBObject("$in",Arrays.asList(charId)));
            DBCursor finalCursor = database.getCollection("filteredComics").find(queryComics);
            System.out.println("---------------------------------------------------------");
            while (finalCursor.hasNext()){
                DBObject comicResult = finalCursor.next();
                System.out.println(comicResult.toString());
            }
            System.out.println("---------------------------------------------------------");
        }
    }

    void executeEyeColorFrequencies(){
        //db.characters_info.aggregate({$group : { _id: '$EyeColor', count: {$sum : 1}}},{$sort: {count: -1}})
        DBCollection collection = database.getCollection("filteredCharacters");
        DBObject groupFields = new BasicDBObject( "_id", "$eyeColor");
        groupFields.put("count", new BasicDBObject( "$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields );
        DBObject sortFields = new BasicDBObject("count", -1);
        DBObject sort = new BasicDBObject("$sort", sortFields );
        List<DBObject> pipeline= new ArrayList();
        pipeline.add(group);
        pipeline.add(sort);
        AggregationOptions aggregationOptions = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build();
        Iterator<DBObject> cursor = collection.aggregate(pipeline,aggregationOptions); 
        System.out.println("---------------------------------------------------------");
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
        System.out.println("---------------------------------------------------------");
    }

    void executeComicWithHighestNumberOfCharacters(){
        // db.filteredComics.aggregate([ {$unwind: "$characterIds"}, { $group: { _id : "$title", len: {$sum : 1} } }, { $sort : { len : -1 }}, { $limit : 1 } ])
        DBCollection collection = database.getCollection("filteredComics");
        DBObject groupFields = new BasicDBObject( "_id", "$title");
        groupFields.put("count", new BasicDBObject( "$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields );
        DBObject sortFields = new BasicDBObject("count", -1);
        DBObject sort = new BasicDBObject("$sort", sortFields );
        DBObject limit = new BasicDBObject("$limit", 1 );
        DBObject unwind = new BasicDBObject("$unwind", "$characterIds" );
        List<DBObject> pipeline= new ArrayList();
        pipeline.add(unwind);
        pipeline.add(group);
        pipeline.add(sort);
        pipeline.add(limit);
        AggregationOptions aggregationOptions = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build();
        Iterator<DBObject> cursor = collection.aggregate(pipeline,aggregationOptions); 
        System.out.println("---------------------------------------------------------");
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
        System.out.println("---------------------------------------------------------");
    }

     void executePublisherWithMostBaldCharacters(){
         //db.filteredCharacters.aggregate(
         // [
         // {$match:{
         // $and:[
         // {publisher:{$exists:true}},
         // {$or:[{hairColor:{$eq:"Bald"}},{hairColor:{$eq:"No Hair"}}]}]}},
         // {$group:{_id:{publisher:"$publisher",hair:"$hairColor"},bald:{$sum:1}}},
         // {$sort:{"bald":-1}},
         // {$limit:1}])
         DBObject matchFieldsPublisher = new BasicDBObject("publisher",new BasicDBObject("$exists",true));
         DBObject matchFieldsBald = new BasicDBObject("hairColor",new BasicDBObject("$eq","Bald"));
         DBObject matchFieldsNoHair = new BasicDBObject("hairColor",new BasicDBObject("$eq","No Hair"));
         DBObject matchFieldsHairColor = new BasicDBObject("$or",Arrays.asList(matchFieldsBald,matchFieldsNoHair));
         DBObject matchFields = new BasicDBObject("$and",Arrays.asList(matchFieldsPublisher,matchFieldsHairColor));
         DBObject match = new BasicDBObject("$match",matchFields);
         DBObject groupField = new BasicDBObject("_id",new BasicDBObject("publisher","$publisher").append("hair","$hairColor")).append("bald",new BasicDBObject("$sum",1));
         DBObject group = new BasicDBObject("$group",groupField);
         DBObject sort = new BasicDBObject("$sort",new BasicDBObject("bald",-1));
         DBObject limit = new BasicDBObject("$limit",1);

         List<DBObject> pipeline = new ArrayList<>();
         pipeline.add(match);
         pipeline.add(group);
         pipeline.add(sort);
         pipeline.add(limit);

         AggregationOptions aggregationOptions = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build();
         Cursor cursor = database.getCollection("filteredCharacters").aggregate(pipeline,aggregationOptions);

         System.out.println("---------------------------------------------------------");
         while (cursor.hasNext()){
             System.out.println(cursor.next().toMap());
         }
         System.out.println("---------------------------------------------------------");

     }

    void executeMenAndWomenRatioInAConcreteUniverse(int universeSelector){
        //var males= db.marvel_dc_characters.find({$and:[{"gender": "Male"},{"universe": "Marvel"}]}).count()
        //var females= db.marvel_dc_characters.find({$and:[{"gender": "Female"},{"universe": "Marvel"}]}).count()
        DBObject universe;
        String selectedUniverse;
        if(universeSelector == 1){
            universe = new BasicDBObject("universe", "Marvel");
            selectedUniverse = "Marvel";
        }else{
            universe = new BasicDBObject("universe", "DC");
            selectedUniverse = "DC Comics";
        }
        DBCollection collection = database.getCollection("filteredCharacters");
        DBObject maleGender = new BasicDBObject("gender", "Male");
        DBObject femaleGender = new BasicDBObject("gender", "Female");
        
        List<DBObject> criteria = new ArrayList<DBObject>();  
        criteria.add(maleGender);
        criteria.add(universe);
        DBCursor dbCursor = collection.find(new BasicDBObject("$and", criteria));
        int males = dbCursor.size();
        criteria.clear();
        criteria.add(femaleGender);
        criteria.add(universe);
        dbCursor = collection.find(new BasicDBObject("$and", criteria));
        int females = dbCursor.size();
        double ratio = (double)males/(double)females;
        DecimalFormat df = new DecimalFormat("####0.00");
        System.out.println("---------------------------------------------------------");
        System.out.println("Male characters= " + males + "; Female characters= "+ females);
        System.out.println("In "+ selectedUniverse+ " there are " + df.format(ratio) +" male characters for every female character");
        System.out.println("---------------------------------------------------------");
    }

    void executeMostHatedCharacter(){
        BasicDBObject deceased = new BasicDBObject();
        deceased.put("status","Deceased");
        BasicDBObject existsAppearances = new BasicDBObject();
        existsAppearances.put("appearances",new BasicDBObject("$exists",true));
        BasicDBObject existsYear = new BasicDBObject();
        existsYear.put("year",new BasicDBObject("$exists",true));
        BasicDBObject query = new BasicDBObject();
        query.put("$and",Arrays.asList(deceased,existsAppearances,existsYear));
        DBCursor cursor = database.getCollection("filteredCharacters").find(query).sort(new BasicDBObject("appearances",1).append("year",1)).limit(1);

        if (cursor.hasNext()){
            System.out.println(cursor.next().toString());
        }
    }

    void executeUniverseWithMostClassicComics(){
        DBObject yearLimit      = new BasicDBObject("year",new BasicDBObject("$lt","2000"));
        DBObject matchYearUnder2000 = new BasicDBObject("$match", yearLimit);

        DBObject marvelUniverse = new BasicDBObject("universe", "Marvel");
        DBObject dcUniverse     = new BasicDBObject("universe", "DC");
        List<DBObject> universes = new ArrayList<DBObject>();
        universes.add(marvelUniverse);
        universes.add(dcUniverse);
        DBObject matchUniverses = new BasicDBObject();
        matchUniverses.put("$match", new BasicDBObject("$or", universes));

        List<DBObject> pipeline= new ArrayList();
        pipeline.add(matchYearUnder2000);
        pipeline.add(matchUniverses);
        AggregationOptions aggregationOptions = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build();
        Iterator<DBObject> cursor = database.getCollection("filteredCharacters").aggregate(pipeline,aggregationOptions);
        System.out.println("---------------------------------------------------------");
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
        System.out.println("---------------------------------------------------------");
    }

    void executeAverageIntelligenceHumansVsCyborgs(){
        DBObject humanRace = new BasicDBObject("race", "Human");
        DBObject cyborgRace = new BasicDBObject("race", "Cyborg");
        List<DBObject> races = new ArrayList<DBObject>();
        races.add(humanRace);
        races.add(cyborgRace);
        DBObject matchRaces = new BasicDBObject();
        matchRaces.put("$match", new BasicDBObject("$or", races));

        DBObject groupFields = new BasicDBObject("_id", "$race");
        groupFields.put("avgIntelligence", new BasicDBObject("$avg", new BasicDBObject("$toInt", "$stats.Intelligence")));
        DBObject groupRace = new BasicDBObject("$group", groupFields );

        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));
        DBObject limit = new BasicDBObject("$limit", 1 );

        List<DBObject> pipeline= new ArrayList();
        pipeline.add(matchRaces);
        pipeline.add(groupRace);
        pipeline.add(sort);
        pipeline.add(limit);
        AggregationOptions aggregationOptions = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build();
        Iterator<DBObject> cursor = database.getCollection("filteredCharacters").aggregate(pipeline,aggregationOptions);
        System.out.println("---------------------------------------------------------");
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
        System.out.println("---------------------------------------------------------");
    }

    void executeFirstCharacterWithSuperpowers(){
        
    }

    void executeQueryTBD(){
        
    }

    public void dumpData(){
        Map<String,Character> characters = new TreeMap<>();
        Map<String,Comic> comics = new TreeMap<>();
        Map<String, List<Long>> charactersInComic = new TreeMap<>();
        File inputFolder = new File("personajes/");

        database.getCollection("filteredCharacters").drop();
        database.getCollection("filteredComics").drop();
        System.out.println(database.toString());
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
                    System.out.println(file.getName());
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
                                current.setName(name);
                                current.setId(id);
                            }else if(output.getName().equals("characters_info.json")){
                                if(entry.get("Name").equals("Captain America")){
                                    System.out.println("Captain America");
                                }
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
                        readAllList.stream().forEach(entry->{
                            long id = 0;

                            try {
                                id = Long.parseLong(entry.get("comicID"));
                            }catch (NumberFormatException e){
                                System.out.println(entry.get("comicID"));
                            }
                            if(!charactersInComic.containsKey(String.valueOf(id))){
                                charactersInComic.put(String.valueOf(id),new ArrayList());
                            }

                                charactersInComic.get(String.valueOf(id)).add(Long.parseLong(entry.get("characterID")));

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
                        Character current;
                        if(entry.get("Name").equals("Captain America")){
                            System.out.println("Captain America");
                        }
                        if(!characters.containsKey(entry.get("Name"))){
                            id = Long.parseLong(entry.get("ID"));
                            current = new Character(id,entry.get("Name"));
                        }else{
                            current = characters.get(entry.get("Name"));
                        }
                        name = entry.get("Name");

                        current.setStatus(entry.get("Status"));
                        try {
                            current.setAppearances(Integer.parseInt(entry.get("Appearances")));
                        }catch (NumberFormatException nf){

                        }
                        current.setFirstAppearance(entry.get("FirstAppearance"));
                        current.setYear(entry.get("Year"));
                        if(entry.get("Name").contains("Earth-616")){
                            current.setPublisher("Marvel Comics");
                            current.setUniverse("Marvel");
                        }else if(entry.get("Name").contains("New Earth")){
                            current.setPublisher("DC Comics");
                            current.setUniverse("DC");
                        }

                        current.setAlignment(entry.get("Alignment"));
                        current.setEyeColor(entry.get("EyeColor"));
                        current.setGender(entry.get("Gender"));
                        current.setRace(entry.get("Race"));

                        current.setHairColor(entry.get("HairColor"));
                        if(entry.get("HairColor").equals("Bald")){
                            current.setHairColor("No Hair");
                        }
                        current.setSkinColor(entry.get("SkinColor"));

                        characters.put(name,current);

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
            oMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            oMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            try {
                charList.add(new BasicDBObject((BasicDBObject) JSON.parse(oMapper.writeValueAsString(s))));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        Stream<Comic> comicStream = comics.values().stream();
        List<DBObject> comicList = new ArrayList<>();
        comicStream.forEach(s ->{
            ObjectMapper oMapper = new ObjectMapper();
            oMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            oMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            try {
                comicList.add(new BasicDBObject((BasicDBObject)JSON.parse(oMapper.writeValueAsString(s))));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });


        database.getCollection("filteredCharacters").insert(charList);
        database.getCollection("filteredComics").insert(comicList);
        database.getCollection("comics").drop();
        database.getCollection("characters").drop();
        database.getCollection("characters_info").drop();
        database.getCollection("characters_stats").drop();
        database.getCollection("charactersToComics").drop();
        database.getCollection("marvel_dc_characters").drop();
        database.getCollection("superheroes_power_matrix").drop();

        System.out.println(characters.keySet());
        database.getCollectionNames().forEach(System.out::println);
    }

}