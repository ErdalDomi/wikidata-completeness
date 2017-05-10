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
  if(req.body.occupation == 'any'){
    console.log('if you seek any');
  }



  var queryString = "select result from human where occupation = '" + req.body.occupation
              + "' and nationality = '" + req.body.nationality
              + "' and centuryofbirth = '" + req.body.centuryofbirth
              + "' and gender = '" + req.body.gender
              + "' and attribute = '" + req.body.attribute
              +"';"
  var query = client.query(queryString);
  query.on('error', function(error) {
      console.log("There was an error with the db query: " + error);
    });
  var queryResult = [];
  query.on('row', function(row, result) {
    queryResult.push(row.result);
  });
  query.on('end', function(result){
    res.send(queryResult);
  });
});

app.listen(8000,function(){
  console.log('Listening on port 8000')
})
