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

 Personaje mas odiado 'db.filteredCharacters.find({$and:[{"status":"Deceased"},{"appearances":{"$exists":true}}]}).sort({"appearances": 1}).limit(1)'
 * Publisher con mas personajes calvos 'db.filteredCharacters.aggregate( [ {$match:{$and:[{publisher:{$exists:true}},{$or:[{hairColor:{$eq:"Bald"}},{hairColor:{$eq:"No Hair"}}]}]}},{$group:{_id:{publisher:"$publisher",hair:"$hairColor"},bald:{$sum:1}}},{$sort:{"bald":-1}},{$limit:1}])'
 *
 * En concreto comics de Capitan America
 * Personajes que salen en comics de una saga 'db.filteredComics.aggregate([ {$match:{"title":{$regex:".*Captain America.*"}}}, {$lookup: {from: "filteredCharacters", localField: "characterIds", foreignField: "id", as: "Characters_in_CA"} },{$unwind:"$Characters_in_CA"},{$group:{_id:"$Characters_in_CA.name"}} ]).pretty()'
    "" con comics en los que aparecen 'db.filteredComics.aggregate([ {$match:{"title":{$regex:".*Captain America.*"}}}, {$lookup: {from: "filteredCharacters", localField: "characterIds", foreignField: "id", as: "Characters_in_CA"} } ]).pretty()'
 
* Query 9 : Primeros personajes en aparecer en un comic.
* db.filteredComics.aggregate([{$match:{year:{$exists:true}}},{$sort : { "year" : 1 } },{$lookup: {from: "filteredCharacters", localField: "characterIds", foreignField: "id", as: "Characters_in_Comics"}},{ "$addFields": {"Characters_in_Comics": {"$filter": {"input": "$Characters_in_Comics","cond": { $ifNull : ["$$this.powers", null]}}}}},{$limit: 1}]).pretty()
*
*
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
