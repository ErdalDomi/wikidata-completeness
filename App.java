package populationScript.anything;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.tdb.TDBFactory;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class App {
	
	public static void main(String[] args) {

		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;
		}
		System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/wikidata", "postgres",
					"password");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		

		
		String endpoint = "https://query.wikidata.org/sparql";
		
		String[] occupation = { "Q82955", "Q40348", "Q937857", "Q10800557", "other", "any" }; //original string
				//occupation = {'politician', 'lawyer', 'soccer player', 'film actor', 'other', 'any'};
		String[] nationality = { "Q30", "Q183", "Q148", "Q38", "other", "any" };
				//nationality = {'USA', 'Germany', 'China', 'Italy', 'other', 'any'};
		String[] attribute = { "P735", "P19", "P22","P25","P103" };
				//attribute = {'given name', 'place of birth', 'father', 'mother', 'native language'};
		String[] gender = { "Q6581097", "Q6581072", "any" };
				//gender = {'male', 'female', 'any'};
		int[] centuryofbirth = { 0, 1, 17, 18, 19, 20 };
		
		//time to sleep between wikidata.org sparql queries (might wanna raise this number during am hours) 
		int sleepTime = 0;
		
		String totalNrQuery;
		String attrNrQuery;
		String completeInstanceQuery;
		String incompleteInstanceQuery;
		
		QueryExecution qe;
		ResultSet rs;
		QuerySolution qs;
		RDFNode node;
		
		
//		Dataset ds;		
//		ds = TDBFactory.createDataset("/media/erdal/Maxtor/ttl space file/tdb-direct-wikidata-20170320-all-BETA-unix-friendly");		
//		Model model = ds.getDefaultModel();		
//		qe = QueryExecutionFactory.create("select ?item where {?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q2133344>.OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }  }", model);
//		rs = qe.execSelect();		
//		System.out.println("The test here: " + rs.next().get("label"));

		if (connection != null) {
			try {
				stmt = connection.createStatement();
				
				for (String occ : occupation) {
					
					
					//---preparing occupation string ---//					
					String occupationString = "";//this falls under the 'any' choice, means no filtering of results
				
					if (!occ.equals("any") && !occ.equals("other")) {//filter triple by occupation
						occupationString = "?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+ occ + "> .";
					} 					
					else if (occ.equals("other")) {//otherwise get complement of given occupations 
						occupationString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> ?occupation } "
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occupation[0]+"> }. " 
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occupation[1]+"> }. " 
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occupation[2]+"> }. " 
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occupation[3]+"> }. " ;
					}
					//------------end occ-------------//
					
					
					for (String nat : nationality) {
						
						//---preparing nationality string ---//
						String nationalityString = "";
						if (!nat.equals("any") && !nat.equals("other")) {
							nationalityString = "?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+ nat + "> . ";
						} else if (nat.equals("other")) {
							nationalityString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> ?nationality } " 
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nationality[0]+"> }. "  
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nationality[1]+"> }. "  
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nationality[2]+"> }. "   
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nationality[3]+"> }. "  ;
						}
						//------------end nat-------------//
						
						for (String gen : gender) {
							
							//---preparing gender string ---//
							String genderString = "";
							if(!gen.equals("any")){
								genderString = "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+ gen + "> . ";
							}
							//------------end gen---------------//
							
								for (int cob : centuryofbirth) {
									
									//-----preparing century of birth ---//
									String centuryofbirthString = "";
									if (cob != 0 && cob != 1) { //0<-any, 1<- more than 0 less than 1700
										centuryofbirthString = "?item <http://www.wikidata.org/prop/direct/P569> ?dob ."
												+ "FILTER(YEAR(?dob) >= " + ((cob - 1) * 100) + "&& YEAR(?dob) < "+ (cob * 100) + ")";
									} else if (cob == 1) {
										centuryofbirthString = "?item <http://www.wikidata.org/prop/direct/P569> ?dob ."+ "FILTER(YEAR(?dob) < 1700)";
									}
									//------------end cob-----------//
									
									
									//------------start querying-------//
									
									//totalNrQuery will get a number of instances that satisfy all filters
									totalNrQuery = "select (COUNT (distinct ?item) AS ?count) " + "where { " 
											+ occupationString
											+ nationalityString
											+ genderString
											+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . " //P31 instance of Q5 human
											+ centuryofbirthString 
											+ "}";
									
									//this string will insert the attributes to be considered for percentage completion of an entity
									//in our case we check each entity completeness against our attribute[]
									String attributeString="";
									
									for(String attr : attribute){
										attributeString += "{?item <http://www.wikidata.org/prop/direct/"+attr+"> ?val. BIND (\""+attr+"\" AS ?prop)} UNION";
									}
									attributeString += "{BIND (\"PDUMMY\" AS ?prop) }";
									
									//this query is gonna collect and group the number of entities according to their completeness
									//this is also the data we use to display the graph
									String completenessPercentageQuery = "SELECT ?completenessPercentage (COUNT(?item) AS ?countItem){"
											+"SELECT ?item (((COUNT(DISTINCT(?prop))-1)/"+attribute.length+"*100) AS ?completenessPercentage)" //attribute.lenght is necessary for correct % computation
											+ "WHERE {"
											+ occupationString
											+ nationalityString
											+ genderString
											+ centuryofbirthString
											+ "{"
											+ attributeString
											+ "}"
											+ "} GROUP BY ?item}GROUP BY ?completenessPercentage";
									
									System.out.println("the percentage query: "+completenessPercentageQuery);
									qe = QueryExecutionFactory.sparqlService(endpoint, completenessPercentageQuery);//fire up query
									rs = qe.execSelect();
									//here is where we sleep if we don't want to hammer server down
									RDFNode item;
									
									String[] completeness = {"0","20","40","60","80","100"}; //for now hardcoded, requires 5 attributes to be investigated
									String[] count = {"0","0","0","0","0","0"};//this workaround is what we use to save numbers for each slot
									
									int pos = 0;
									while(rs.hasNext()){
										qs = rs.next(); //get first result
										for(int i=0;i<completeness.length;i++){//loop to find the right slot, since answers come back without order eg. 80,40,20,60,0,100
											if(completeness[i].equals(qs.get("completenessPercentage").asLiteral().getValue().toString())){ //slot found
												count[i] = qs.get("countItem").asLiteral().getValue().toString();//update slot-count												
											}
										}
									}
									
									//json format to save the data since this is what the graph uses
									String dataObj = "{";
									for(int i =0;i<completeness.length;i++){//for every slot										
										if(i==5){//exclude last trailing comma
											dataObj += "\""+completeness[i]+"\":"+count[i]; //gotta escape quotes here, since psql cant insert with ''
										} else{
											dataObj += "\""+completeness[i]+"\":"+count[i]+",";
										}										
									}
									dataObj += "}";
									
									System.out.println(dataObj);
									System.out.println("Going to sleep...");
									try {
										Thread.sleep(sleepTime);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									//now we need to get instances for every slot 
									//-----loop this six times to get 10 labels for every completeness slot

									
									
									int i = 0;
									System.out.println("----------------------------------");
									for(i=0;i<completeness.length;i++){

									System.out.println("Entering slot: "+completeness[i]);
									
									String tenLabelQuery = "SELECT distinct ?item ?label (((COUNT(DISTINCT(?prop))-1)/"+attribute.length+"*100) AS ?completenessPercentage)" //attribute.lenght is necessary for correct % computation
											+ "WHERE {"
											+ occupationString
											+ nationalityString
											+ genderString
											+ centuryofbirthString
											+ "{"
											+ attributeString
											+ "}"
											+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
											+ "} GROUP BY ?item ?label HAVING(?completenessPercentage = "+completeness[i]+") LIMIT 10";
									
									System.out.println("Query for ?item and ?label: "+tenLabelQuery);
									qe = QueryExecutionFactory.sparqlService(endpoint, tenLabelQuery);//fire up query
									rs = qe.execSelect();
									//here is where we sleep if we don't want to hammer server down
									
									String slotEntities = "";
									String slotLabels = "";
									
									QueryExecution qe2;
									ResultSet rs2;
									QuerySolution qs2;
									
									String tableData = ""; //{
									
									while(rs.hasNext()){
										qs = rs.next();
										item = qs.get("item");	
										System.out.print("Inspecting: "+item.toString());
										//now that we got the item, we can check each of the properties existence. Note: no values will be considered complete
										tableData+="[";
										for(String attr:attribute){//we will see if we get a result for every attribute for the item and put a 1 or 0 in the object we're creating
											System.out.println(" for attribute: "+attr);
											String propertyQuery = "SELECT * WHERE{ <"+item.toString()+"> <http://www.wikidata.org/prop/direct/"+attr+"> ?o }";
											qe2 = QueryExecutionFactory.sparqlService(endpoint, propertyQuery);
											rs2 = qe2.execSelect();
											//pause for server
											if(rs2.hasNext()){
												if(attr.equals(attribute[attribute.length-1])){
													System.out.println("Last attribute of item "+item+"so no trailing comma on 1.");
													tableData+="1";
												}else{
													System.out.println("Putting a 1. (found)");
													tableData+="1,";
												}
												
											}else{
												if(attr.equals(attribute[attribute.length-1])){
													System.out.println("Last attribute of item "+item+"so no trailing comma on 0.");
													tableData+="0";
												}else{
													System.out.println("Putting a 0. (not found)");
													tableData+="0,";
												}
											}
										}											
										
										slotEntities += item.toString() + ","; //get raw entities
										node = qs.get("label");
										if(node != null){										
											slotLabels += node.toString().replace(",", "").replace("'", " ") + ",";	//sanitize and add
										} else{
											slotLabels += item.toString()+","; //add raw entity
										}
										tableData+="];";
									}			
									
									if(tableData.length()>2){
										tableData = tableData.substring(0, tableData.length()-1); //last trailing comma
									}
									
									
									//tableData +="}"; 
									
									
									if(slotEntities != null && slotEntities.length() > 0){
										slotEntities = slotEntities.substring(0, slotEntities.length()-1);
									}else{System.out.println("There was nothing to substring");}
									
									if(slotLabels !=null && slotLabels.length() > 0){
										slotLabels = slotLabels.substring(0, slotLabels.length()-1);
									}else{System.out.println("There was nothing to substring");}
									
									System.out.println("Have: " + slotEntities + " \nand: "+slotLabels + "\nand tableData: " +tableData);
									//add to database
									String psqlQuery ="INSERT INTO humangraph(occupation,nationality,centuryofbirth,gender,graphdata, slot, labels, tabledata, items)"
											+ "VALUES('" + occ + "', '" + nat + "','" + cob + "', '" + gen + "','" + dataObj+"', '" + completeness[i] 
											+ "', '" + slotLabels + "', '" + tableData + "', '" + slotEntities + "')";
									stmt.executeUpdate(psqlQuery);
									
									}
									
									
									
									
//									qs = rs.next();
//									node = qs.get("count");
//									String totalnr = node.asLiteral().getValue().toString();
//
//
//									attrNrQuery = "select (COUNT (distinct ?item ) AS ?count) " + "where { "
//											+ centuryofbirthString 
//											+ occupationString 
//											+ nationalityString
//											+ genderString
//											+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//											+ " ?item <http://www.wikidata.org/prop/direct/"+ att + "> ?attribute . " 
//											+ "}";
//									
//									
//									try {
//										Thread.sleep(sleepTime);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//									qe = QueryExecutionFactory.sparqlService(endpoint, attrNrQuery);
//									rs = qe.execSelect();
//									qs = rs.next();
//									node = qs.get("count");									
//									String attnr = node.asLiteral().getValue().toString();
//									
//									int attnrint = Integer.parseInt(attnr);
//									int totalnrint = Integer.parseInt(totalnr);
//									float result = attnrint / (float) totalnrint;
//									
//									String stringResult = Float.toString(result);
//									
//									
//									completeInstanceQuery = "select distinct ?item ?label " 
//											+ "where { "
//											+ centuryofbirthString 
//											+ occupationString 
//											+ nationalityString
//											+ genderString
//											+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//											+ " ?item <http://www.wikidata.org/prop/direct/"+ att + "> ?attribute . " 
//											+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
//											+ "} limit 10";
//									
//									try {
//										Thread.sleep(sleepTime);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//									
//									qe = QueryExecutionFactory.sparqlService(endpoint, completeInstanceQuery);
//									rs = qe.execSelect();
//									
//									String completeInstances = "";
//									String completeLabels = "";
//									
////									RDFNode item;
//									
//									while(rs.hasNext()){
//										qs = rs.next();
//										item = qs.get("item");									
//										completeInstances += item.toString() + ",";
//										node = qs.get("label");
//										if(node != null){										
//											completeLabels += node.toString().replace(",", "").replace("'", " ") + ",";	
//										} else{
//											completeLabels += item.toString()+",";
//										}
//										
//									}
//									
//
//										
//									
//									incompleteInstanceQuery = "select distinct ?item ?label " 
//													+ "where { "
//													+ centuryofbirthString 
//													+ occupationString 
//													+ nationalityString
//													+ genderString
//													+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//													+ "filter not exists {?item <http://www.wikidata.org/prop/direct/"+att+"> ?attribute }" 
//													+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
//													+ "} limit 10";
//											
//									try {
//										Thread.sleep(sleepTime);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//									qe = QueryExecutionFactory.sparqlService(endpoint, incompleteInstanceQuery);
//									rs = qe.execSelect();
//									
//									String incompleteInstances = "";
//									String incompleteLabels = "";
//									
//									while(rs.hasNext()){
//										qs = rs.next();
//										item = qs.get("item");									
//										incompleteInstances += item.toString() + ",";
//										node = qs.get("label");
//										if(node != null){										
//											incompleteLabels += node.toString().replace(",", "").replace("'", " ") + ",";	
//										} else{
//											incompleteLabels += item.toString()+",";
//										}
//									}
									
//									String psqlQuery = "INSERT INTO human (occupation, nationality, centuryofbirth, gender, attribute, "
//											+ "totalnr, attnr, result, completeinstances, incompleteinstances, completequery, incompletequery,"
//											+ "completelabel, incompletelabel)"
//											+ "VALUES('" + occ + "', '" + nat + "','" + cob + "', '" + gen + "','" + att
//											+ "','" + totalnr + "', '" + attnr + "', '" + stringResult + "', '" + completeInstances + "', '" 
//											+ incompleteInstances+ "', '" + completeInstanceQuery+ "', '" + incompleteInstanceQuery+ "', '" 
//											+ completeLabels+ "', '" + incompleteLabels+"')";
//									stmt.executeUpdate(psqlQuery);
//									System.out.println("occ: " + occ + "nat: " + nat + "gen: " + gen + "cob: " + cob + "att: " + att + "totalnr: " + totalnr + "attnr: " + attnr + "result: " + result + " into database");
									

								}
						}
					}
				}
				System.out.println("Done with human queries.");
				//end first query permutation 

//				//start second query permutation - album
//				String[] genre = { "any", "Q8341", "Q11399", "Q11401", "Q235858", "Q9759", "other" };
//				String[] language = { "any", "Q1860", "Q7737", "Q652", "Q1321", "other" };
//				int[] date = {0,1,1960,1970,1980,1990,2000 };
//				String[] albumattribute = {"P264", "P658", "P162", "P154"};
//				
//				totalNrQuery = "";
//				attrNrQuery= "";
//				completeInstanceQuery= "";
//				incompleteInstanceQuery= "";
//				
//				i = 0;				
//				
//				for (String gen : genre) {
//					String genreString = "";
//					if (!gen.equals("any") && !gen.equals("other")) {
//						genreString = "?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/"+ gen + "> .";
//					} else if (gen.equals("other")) {
//						genreString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> ?genre } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q8341> } " 
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q11399> } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q11401> } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q9759> } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q235858> }"; 
//					}
//					for(String lang : language){
//						String languageString = "";
//						if (!lang.equals("any") && !lang.equals("other")) {
//							languageString = "?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/"+ lang + "> .";
//						} else if (lang.equals("other")) {
//							languageString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> ?genre } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q1860> } " 
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q7737> } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q652> } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q1321> } "; 
//						}
//						for(int dat : date){
//							String dateString;
//							if(dat == 0){
//								dateString = "";
//							}
//							else if(dat == 1){
//								dateString = "?item <http://www.wikidata.org/prop/direct/P577> ?pubdate . filter(year(?pubdate) < 1960)";
//							} else{
//								dateString = "?item <http://www.wikidata.org/prop/direct/P577> ?pubdate . filter(year(?pubdate) <= " 
//							    + (dat+10) + "&& year(?pubdate) > " + dat + ")";
//							}
//							for(String att : albumattribute){
//								
//								
//								totalNrQuery = "select (COUNT (distinct ?item) AS ?count) " + "where { " 
//										+ genreString
//										+ languageString										
//										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
//										+ dateString 
//										+ "}";
//								
//								i++;
//								qe = QueryExecutionFactory.sparqlService(endpoint, totalNrQuery);
//								try {
//									Thread.sleep(sleepTime);
//									rs = qe.execSelect();
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									System.out.println("Problem getting data... Retrying...");
//									try {
//										Thread.sleep(2000);
//									} catch (InterruptedException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
//									rs = qe.execSelect();
//								}
//								
//								
//								qs = rs.next();
//								node = qs.get("count");
//								String totalnr = node.asLiteral().getValue().toString();
//
//								// maybe use the variable query instead of
//								// rewriting(pasting) this
//								attrNrQuery = "select (COUNT (distinct ?item) AS ?count) " + "where { " 
//										+ genreString
//										+ languageString										
//										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
//										+ dateString 
//										+ "?item <http://www.wikidata.org/prop/direct/" + att + "> ?attribute . "
//										+ "}";
//
//								try {
//									Thread.sleep(sleepTime);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								qe = QueryExecutionFactory.sparqlService(endpoint, attrNrQuery);
//								try {
//									Thread.sleep(sleepTime);
//									rs = qe.execSelect();
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									System.out.println("Problem getting data... Retrying...");
//									try {
//										Thread.sleep(2000);
//									} catch (InterruptedException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
//									rs = qe.execSelect();
//								}
//								qs = rs.next();
//								node = qs.get("count");
//
//								String attnr = node.asLiteral().getValue().toString();
//								int attnrint = Integer.parseInt(attnr);
//								int totalnrint = Integer.parseInt(totalnr);
//								float result = attnrint / (float) totalnrint;
//								String stringResult = Float.toString(result);
//								
//								
//								completeInstanceQuery = "select distinct ?item ?label " 
//										+ "where { " 
//										+ genreString
//										+ languageString										
//										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
//										+ dateString 
//										+ "?item <http://www.wikidata.org/prop/direct/" + att + "> ?attribute . "
//										+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
//										+ "} limit 10";
//								
//								try {
//									Thread.sleep(sleepTime);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								
//								qe = QueryExecutionFactory.sparqlService(endpoint, completeInstanceQuery);
//								try {
//									Thread.sleep(sleepTime);
//									rs = qe.execSelect();
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									System.out.println("Problem getting data... Retrying...");
//									try {
//										Thread.sleep(2000);
//									} catch (InterruptedException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
//									rs = qe.execSelect();
//								}
//								
//								String completeInstances = "";
//								String completeLabels = "";
//								RDFNode item;
//								while(rs.hasNext()){
//									qs = rs.next();
//									item = qs.get("item");									
//									completeInstances += item.toString() + ",";
//									node = qs.get("label");
//									if(node != null){										
//										completeLabels += node.toString().replace(",", "").replace("'", " ") + ",";	
//									} else{
//										completeLabels += item.toString()+",";
//									}
//															
//								}
//								
//								incompleteInstanceQuery = "select distinct ?item ?label " 
//										+ "where { " 
//										+ genreString
//										+ languageString										
//										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
//										+ dateString 
//										+ "filter not exists {?item <http://www.wikidata.org/prop/direct/"+att+"> ?attribute }"
//										+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
//										+ "} limit 10";
//								
//								String incompleteInstances = "";
//								String incompleteLabels = "";
//								
//								try {
//									Thread.sleep(sleepTime);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								
//								qe = QueryExecutionFactory.sparqlService(endpoint, incompleteInstanceQuery);
//								
//								try {
//									Thread.sleep(sleepTime);
//									rs = qe.execSelect();
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									System.out.println("Problem getting data... Retrying...");
//									try {
//										Thread.sleep(2000);
//									} catch (InterruptedException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
//									rs = qe.execSelect();
//								}
//								
//								
//								while(rs.hasNext()){
//									qs = rs.next();
//									item = qs.get("item");
//									incompleteInstances += item.toString() + " ,";
//									node = qs.get("label");
//									if(node != null){
//										incompleteLabels += node.toString().replace(",", "").replace("'", " ") + ",";
//									}else{
//										incompleteLabels += item.toString()+ ",";
//									}
//									
//								}
//								
//								
//								System.out.println("adding " + gen + " " + lang + " " + dat + " " + att + " " + totalnr + " " + attnr + " " + result + "into database");
//								
//								String psqlQuery = "INSERT INTO album (genre, language, date, attribute, totalnr, attnr, result, completeinstances, incompleteinstances, "
//										+ "completequery, incompletequery, completelabel, incompletelabel) "
//										+ "VALUES('" + gen + "', '" + lang + "','" + dat + "', '" + att + "','"  + totalnr + "', '" 
//										+ attnr + "', '" + stringResult + "', '" + completeInstances+ "', '" + incompleteInstances 
//										+ "', '" + completeInstanceQuery + "','"  + incompleteInstanceQuery + "', '" + completeLabels +"', '" + incompleteLabels+"')";
//								
//								stmt.executeUpdate(psqlQuery);
//								
//							}
//						}
//					}
//				}
				stmt.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Failed to make connection!");
		}

	}
}
