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


        Menu m = new Menu();
            m.selectQuery();
           

    }
}
