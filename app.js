var express = require('express')
var app = express()
var path = require('path')
var sparql = require('sparql')
var Client = require('pg').Client;
var connection = require('pg').Connection;
var bodyParser = require('body-parser')
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: true
})); 
// var sparqlClient = new sparql.Client('https://query.wikidata.org/sparql')
// //example sparql query
// sparqlClient.query('SELECT ?item WHERE {?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q146> .}', function(err, res){
//   console.log("result: "+JSON.stringify(res))
//
// })
var wdSparqlClient = new sparql.Client('http://wolfgang.inf.unibz.it:3030/WD');
wdSparqlClient.query('SELECT (COUNT(?item) as ?cnt)'
+ 'WHERE { '
+ '?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> .'
+ '}', function(err, res){
  console.log("result of wd query: " + JSON.stringify(res));
  console.log("ustringified: " + res.results.bindings[0].cnt.value);
})

app.use(express.static(path.join(__dirname, 'public')))

pgClient = new Client({
  user: 'postgres',
  password: 'password',
  database: 'travel',
  host: '127.0.0.1',
  port: 5432
});
pgClient.connect();
var dbStatus = "ON";
pgClient.on('error', function(error){
  console.log(error);
  dbStatus = "OFF"
});
/* example pg query :
var query = client.query('select * from connection');
var responseArray = [];
query.on('row', function(row, result) {
  curr = {id: row.id, label: row.name};
  responseArray.push(curr);
});

*/
console.log('Database connection established');

app.get("/checkStatus", function(req, res){
  res.send(dbStatus);
});

app.post("/submitted", function(req, res){
  console.log(req.body.class);
  res.send("nice");
});

app.listen(8000,function(){
  console.log('Listening on port 8000')
})
