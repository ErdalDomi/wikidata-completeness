package populationScript.anything;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;

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

		String[] occupation = { "Q82955", "Q40348", "Q937857", "Q10800557", "other", "any" };
		String[] nationality = { "Q30", "Q183", "Q148", "Q38", "other", "any" };
		String[] attribute = { "P40", "P13", "P102", "P69" };
		String[] gender = { "Q6581097", "Q6581072" };
		int[] centuryofbirth = { 0, 1, 17, 18, 19, 20 };



		int i = 0;
		String totalNrQuery;
		String attrNrQuery;
		String completeInstanceQuery;
		String incompleteInstanceQuery;
		QueryExecution qe;
		ResultSet rs;
		QuerySolution qs;
		RDFNode node;

		if (connection != null) {
			try {
				stmt = connection.createStatement();
//				for (String occ : occupation) {
//					String occupationString = "";
//					if (!occ.equals("any") && !occ.equals("other")) {
//						occupationString = "?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+ occ + "> .";
//					} else if (occ.equals("other")) {
//						occupationString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> ?occupation } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q82955> } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q40348> } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q937857> } "
//								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q10800557> }";
//					}
//					for (String nat : nationality) {
//						String nationalityString = "";
//						if (!nat.equals("any") && !nat.equals("other")) {
//							nationalityString = "?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"
//									+ nat + "> . ";
//						} else if (nat.equals("other")) {
//							nationalityString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> ?nationality } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q30> } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q183> } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q148> } "
//									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q38> } ";
//						}
//						for (String gen : gender) {
//							for (String att : attribute) {
//								for (int cob : centuryofbirth) {
//									String centuryofbirthString = "";
//									if (cob != 0 && cob != 1) {
//										centuryofbirthString = "?item <http://www.wikidata.org/prop/direct/P569> ?dob ."
//												+ "FILTER(YEAR(?dob) >= " + ((cob - 1) * 100) + "&& YEAR(?dob) < "
//												+ (cob * 100) + ")";
//									} else if (cob == 1) {
//										centuryofbirthString = "?item <http://www.wikidata.org/prop/direct/P569> ?dob ."
//												+ "FILTER(YEAR(?dob) < 1700)";
//									}
//
//
//									totalNrQuery = "select (COUNT (distinct ?item) AS ?count) " + "where { "
//											+ occupationString
//											+ nationalityString
//											+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+ gen + "> . "
//											+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//											+ centuryofbirthString
//											+ "}";
//
//									i++;
//									qe = QueryExecutionFactory.sparqlService(endpoint, totalNrQuery);
//									rs = qe.execSelect();
//									qs = rs.next();
//									node = qs.get("count");
//									String totalnr = node.asLiteral().getValue().toString();
//
//
//									attrNrQuery = "select (COUNT (distinct ?item ) AS ?count) " + "where { "
//											+ centuryofbirthString
//											+ occupationString
//											+ nationalityString
//											+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+ gen + "> . "
//											+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//											+ " ?item <http://www.wikidata.org/prop/direct/"+ att + "> ?attribute . "
//											+ "}";
//									qe = QueryExecutionFactory.sparqlService(endpoint, attrNrQuery);
//									rs = qe.execSelect();
//									qs = rs.next();
//									node = qs.get("count");
//
//
//
//									String attnr = node.asLiteral().getValue().toString();
//									int attnrint = Integer.parseInt(attnr);
//									int totalnrint = Integer.parseInt(totalnr);
//									float result = attnrint / (float) totalnrint;
//									String stringResult = Float.toString(result);
//
//
//									completeInstanceQuery = "select distinct ?item ?label "
//											+ "where { "
//											+ centuryofbirthString
//											+ occupationString
//											+ nationalityString
//											+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+ gen + "> . "
//											+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//											+ " ?item <http://www.wikidata.org/prop/direct/"+ att + "> ?attribute . "
//											+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
//											+ "} limit 10";
//
//
//									qe = QueryExecutionFactory.sparqlService(endpoint, completeInstanceQuery);
//									rs = qe.execSelect();
//
//									String completeInstances = "";
//									String completeLabels = "";
//
//
//									while(rs.hasNext()){
//										qs = rs.next();
//										node = qs.get("item");
//										completeInstances += node.toString() + ",";
//										node = qs.get("label");
//										completeLabels += node.toString().replace(",", "").replace("'", " ") + ",";
//
//									}
//
//									incompleteInstanceQuery = "select distinct ?item ?label "
//													+ "where { "
//													+ centuryofbirthString
//													+ occupationString
//													+ nationalityString
//													+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+ gen + "> . "
//													+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
//													+ "filter not exists {?item <http://www.wikidata.org/prop/direct/"+att+"> ?attribute }"
//													+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
//													+ "} limit 10";
//
//									qe = QueryExecutionFactory.sparqlService(endpoint, incompleteInstanceQuery);
//									rs = qe.execSelect();
//
//									String incompleteInstances = "";
//									String incompleteLabels = "";
//
//									while(rs.hasNext()){
//										qs = rs.next();
//										node = qs.get("item");
//										incompleteInstances += node.toString() + " ,";
//										node = qs.get("label");
//										incompleteLabels += node.toString().replace(",", "").replace("'", " ") + ",";
//									}
//
//									String psqlQuery = "INSERT INTO human (occupation, nationality, centuryofbirth, gender, attribute, "
//											+ "totalnr, attnr, result, completeinstances, incompleteinstances, completequery, incompletequery,"
//											+ "completelabel, incompletelabel)"
//											+ "VALUES('" + occ + "', '" + nat + "','" + cob + "', '" + gen + "','" + att
//											+ "','" + totalnr + "', '" + attnr + "', '" + stringResult + "', '" + completeInstances + "', '"
//											+ incompleteInstances+ "', '" + completeInstanceQuery+ "', '" + incompleteInstanceQuery+ "', '"
//											+ completeLabels+ "', '" + incompleteLabels+"')";
//									stmt.executeUpdate(psqlQuery);
//									System.out.println("adding " + occ + " " + nat + " " + cob + " " + gen + " " + totalnr + " " + attnr + " " + result + "into database");
//
//								}
//							}
//						}
//					}
//				}
//				//end first query permutation
//				System.out.println("there were " + i + " queries made for 'human' class.");

//				//start second query permutation - album
				String[] genre = { "any", "Q8341", "Q11399", "Q11401", "Q235858", "Q9759", "other" };
				String[] language = { "any", "Q1860", "Q7737", "Q652", "Q1321", "other" };
				int[] date = {0,1,1960,1970,1980,1990,2000 };
				String[] albumattribute = {"P264", "P658", "P162", "P154"};

				totalNrQuery = "";
				attrNrQuery= "";
				completeInstanceQuery= "";
				incompleteInstanceQuery= "";

				i = 0;

				for (String gen : genre) {
					String genreString = "";
					if (!gen.equals("any") && !gen.equals("other")) {
						genreString = "?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/"+ gen + "> .";
					} else if (gen.equals("other")) {
						genreString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> ?genre } "
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q8341> } "
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q11399> } "
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q11401> } "
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q9759> } "
								+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P136> <http://www.wikidata.org/entity/Q235858> }";
					}
					for(String lang : language){
						String languageString = "";
						if (!lang.equals("any") && !lang.equals("other")) {
							languageString = "?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/"+ lang + "> .";
						} else if (lang.equals("other")) {
							languageString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> ?genre } "
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q1860> } "
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q7737> } "
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q652> } "
									+ "  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P364> <http://www.wikidata.org/entity/Q1321> } ";
						}
						for(int dat : date){
							String dateString;
							if(dat == 0){
								dateString = "";
							}
							else if(dat == 1){
								dateString = "?item <http://www.wikidata.org/prop/direct/P577> ?pubdate . filter(year(?pubdate) < 1960)";
							} else{
								dateString = "?item <http://www.wikidata.org/prop/direct/P577> ?pubdate . filter(year(?pubdate) <= "
							    + (dat+10) + "&& year(?pubdate) > " + dat + ")";
							}
							for(String att : albumattribute){


								totalNrQuery = "select (COUNT (distinct ?item) AS ?count) " + "where { "
										+ genreString
										+ languageString
										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
										+ dateString
										+ "}";

								i++;
								qe = QueryExecutionFactory.sparqlService(endpoint, totalNrQuery);
								rs = qe.execSelect();
								qs = rs.next();
								node = qs.get("count");
								String totalnr = node.asLiteral().getValue().toString();

								// maybe use the variable query instead of
								// rewriting(pasting) this
								attrNrQuery = "select (COUNT (distinct ?item) AS ?count) " + "where { "
										+ genreString
										+ languageString
										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
										+ dateString
										+ "?item <http://www.wikidata.org/prop/direct/" + att + "> ?attribute . "
										+ "}";

								qe = QueryExecutionFactory.sparqlService(endpoint, attrNrQuery);
								rs = qe.execSelect();
								qs = rs.next();
								node = qs.get("count");

								String attnr = node.asLiteral().getValue().toString();
								int attnrint = Integer.parseInt(attnr);
								int totalnrint = Integer.parseInt(totalnr);
								float result = attnrint / (float) totalnrint;
								String stringResult = Float.toString(result);


								completeInstanceQuery = "select distinct ?item ?label "
										+ "where { "
										+ genreString
										+ languageString
										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
										+ dateString
										+ "?item <http://www.wikidata.org/prop/direct/" + att + "> ?attribute . "
										+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
										+ "} limit 10";

								qe = QueryExecutionFactory.sparqlService(endpoint, completeInstanceQuery);
								rs = qe.execSelect();

								String completeInstances = "";
								String completeLabels = "";
								RDFNode item;
								while(rs.hasNext()){
									qs = rs.next();
									item = qs.get("item");
									completeInstances += item.toString() + ",";
									node = qs.get("label");
									if(node != null){
										completeLabels += node.toString().replace(",", "").replace("'", " ") + ",";
									} else{
										completeLabels += item.toString()+",";
									}

								}

								incompleteInstanceQuery = "select distinct ?item ?label "
										+ "where { "
										+ genreString
										+ languageString
										+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q482994> . "
										+ dateString
										+ "filter not exists {?item <http://www.wikidata.org/prop/direct/"+att+"> ?attribute }"
										+ "OPTIONAL { ?item <http://www.w3.org/2000/01/rdf-schema#label> ?label . FILTER (lang(?label)=\"en\") }"
										+ "} limit 10";

								String incompleteInstances = "";
								String incompleteLabels = "";

								qe = QueryExecutionFactory.sparqlService(endpoint, incompleteInstanceQuery);
								rs = qe.execSelect();

								while(rs.hasNext()){
									qs = rs.next();
									item = qs.get("item");
									incompleteInstances += item.toString() + " ,";
									node = qs.get("label");
									if(node != null){
										incompleteLabels += node.toString().replace(",", "").replace("'", " ") + ",";
									}else{
										incompleteLabels += item.toString()+ ",";
									}

								}


								System.out.println("adding " + gen + " " + lang + " " + dat + " " + att + " " + totalnr + " " + attnr + " " + result + "into database");

								String psqlQuery = "INSERT INTO album (genre, language, date, attribute, totalnr, attnr, result, completeinstances, incompleteinstances, "
										+ "completequery, incompletequery, completelabel, incompletelabel) "
										+ "VALUES('" + gen + "', '" + lang + "','" + dat + "', '" + att + "','"  + totalnr + "', '"
										+ attnr + "', '" + stringResult + "', '" + completeInstances+ "', '" + incompleteInstances
										+ "', '" + completeInstanceQuery + "','"  + incompleteInstanceQuery + "', '" + completeLabels +"', '" + incompleteLabels+"')";

								stmt.executeUpdate(psqlQuery);

							}
						}
					}
				}
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
