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

var wdSparqlClient = new sparql.Client('http://wolfgang.inf.unibz.it:3030/WD');
//not sure we need this anymore, since the script is done in App.java now

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

/*
example pg query :
var query = client.query('select * from connection');
var responseArray = [];
query.on('row', function(row, result) {
  curr = {id: row.id, label: row.name};
  responseArray.push(curr);
});
example column name print
var firstRow = result.rows[0];
for(var columnName in firstRow){
  console.log('%s | ', columnName);
}

get number of instances that dont have the attribute
select (count(?item) as ?cnt)
where {
  ?item wdt:P31 wd:Q5.
  ?item wdt:P21 wd:Q6581097.
  ?item wdt:P27 wd:Q183.
  ?item wdt:P106 wd:Q40348.
  FILTER NOT EXISTS {?item wdt:P40 ?attribute . }
  }
*/
console.log('Database connection established');

app.get("/checkStatus", function(req, res){
  res.send(dbStatus);
});

app.post("/submitted", function(req, res){

  var query = client.query("select result from human where occupation = '" + req.body.occupation
              + "' and nationality = '" + req.body.nationality + "' and gender = '" + req.body.gender
              + "' and attribute = '" + req.body.attribute +"';");
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
