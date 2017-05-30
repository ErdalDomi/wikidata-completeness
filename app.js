var express = require('express')
var app = express()
var async = require('async')
var path = require('path')
var sparql = require('sparql')
var Client = require('pg').Client;
var connection = require('pg').Connection;
var bodyParser = require('body-parser')
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: true
}));

app.use(express.static(path.join(__dirname, 'public')))

var client; //we use this to query the database

client = new Client({
  user: 'postgres',
  password: 'password',
  database: 'wikidata',
  host: '127.0.0.1',
  port: 5432
});
client.connect();
var dbStatus = "ON";
client.on('error', function(error){
  console.log(error);
  dbStatus = "OFF"
});

console.log('Database connection established');

app.get("/checkStatus", function(req, res){
  res.send(dbStatus);
});

app.post("/submitted", function(req, res){

  var queryString = "";
  if(req.body.table == 'human'){

    // this string is needed to populate human table
    // queryString = "select result, totalnr, attnr, completeinstances, incompleteinstances, completequery, "
    //             + "incompletequery, completelabel, incompletelabel from human where "
    //             + "occupation = '" + req.body.occupation
    //             + "' and nationality = '" + req.body.nationality
    //             + "' and centuryofbirth = '" + req.body.centuryofbirth
    //             + "' and gender = '" + req.body.gender
    //             + "' and attribute = '" + req.body.attribute
    //             +"';"

    //this string will populate humangraph
    queryString = "select graphdata from humangraph where "
                + "occupation = '" + req.body.occupation
                + "' and nationality = '" + req.body.nationality
                + "' and centuryofbirth = '" + req.body.centuryofbirth
                + "' and gender = '" + req.body.gender + "';";
  }else if(req.body.table == 'album'){
    queryString = "select result, totalnr, attnr, completeinstances, incompleteinstances, completequery,"
    +" incompletequery, completelabel, incompletelabel from album where "
    + "genre = '" + req.body.genre
    + "' and language = '" + req.body.language
    + "' and date = '" + req.body.date
    + "' and attribute = '" + req.body.attribute
    + "';"
  }

  var query = client.query(queryString);
  query.on('error', function(error) {
      console.log("There was an error with the db query: " + error);
    });
  var queryResult = [];
  query.on('row', function(row, result) {

    //this is the mapping for human table
    // queryResult.push(row.result);
    // queryResult.push(row.totalnr);
    // queryResult.push(row.attnr);
    //
    // queryResult.push(row.completeinstances);
    // queryResult.push(row.incompleteinstances);
    //
    // queryResult.push(row.completequery);
    // queryResult.push(row.incompletequery);
    //
    // queryResult.push(row.completelabel);
    // queryResult.push(row.incompletelabel);

    //this is the mapping for humangraph 
    queryResult.push(row.graphdata);
  });
  query.on('end', function(result){
    res.send(queryResult);
  });
});

app.listen(8000,function(){
  console.log('Listening on port 8000')
})
