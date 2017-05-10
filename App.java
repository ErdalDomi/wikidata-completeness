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


public class App 
{
    public static void main( String[] args )
    {
    	
    	System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;
		Statement stmt = null;
		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/wikidata", "postgres",
					"password");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}
		
        String endpoint = "https://query.wikidata.org/sparql";
        String[] occupation = {"Q82955", "Q40348", "Q937857", "Q10800557", "other","any"};
        String[] nationality = {"Q30", "Q183", "Q148", "Q38", "other","any"};
        String[] attribute = {"P40", "P13", "P102", "P69"};
        String[] gender = {"Q6581097", "Q6581072"};
        int[] centuryofbirth = {0,1,17,18,19,20};
        
        int i = 0;
        String query;
        QueryExecution qe;
        ResultSet rs;
        QuerySolution qs;
        RDFNode node;
        
		if (connection != null) {
			try {
				stmt = connection.createStatement();				
		        for(String occ : occupation){
		        	String occupationString = "";
		        	if(!occ.equals("any") && !occ.equals("other")){
		        		occupationString = "?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occ +"> .";
		        	}else if(occ.equals("other")){
		        		occupationString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> ?occupation } " // with an occupation
										  +"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q82955> } " // but that occupation is not politician
										  +"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q40348> } " // nor lawyer
										  +"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q937857> } "// nor footballer";
										  +"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/Q10800557> }"; //nor film actor
		        	}
		        	for(String nat : nationality){
		        		String nationalityString = "";
		        		if(!nat.equals("any")&& !nat.equals("other")){
		        			nationalityString = "?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nat +"> . ";
		        		}else if(nat.equals("other")){
		        			nationalityString = "  FILTER EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> ?nationality } " // with a nationality
									  		+"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q30> } " // but that nationality is not usa
									  		+"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q183> } " // nor germany
									  		+"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q148> } " // nor china"
									  		+"  FILTER NOT EXISTS { ?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/Q38> } "; //nor italy
		        		}
		        		for(String gen : gender){
		        			for(String att: attribute){
		        				for(int cob: centuryofbirth){
		        					String centuryofbirthString = "";
		        					if(cob != 0 && cob != 1){
		        						centuryofbirthString = "?item <http://www.wikidata.org/prop/direct/P569> ?dob ."
		    					        		+ "FILTER(YEAR(?dob) >= " + ((cob-1)*100) + "&& YEAR(?dob) < " + (cob*100) + ")";
		        					}else if(cob == 1){
		        						centuryofbirthString = "?item <http://www.wikidata.org/prop/direct/P569> ?dob ."
		        								+ "FILTER(YEAR(?dob) < 1700)";
		        					}
			        		    	query = "select (COUNT(?item) AS ?count) "
					        		+ "where { "		        		
					        		+ occupationString
					        		+ nationalityString
					        		+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+gen +"> . "
					        		+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
					        		+ centuryofbirthString
					        		+ "}";
			        				i++;
			        		        qe = QueryExecutionFactory.sparqlService(endpoint, query);
			        		        rs = qe.execSelect();
			        		        qs = rs.next();
			        		        node = qs.get("count");
			        		        String totalnr = node.asLiteral().getValue().toString();	
			        		        		        		        		        				
			        				
			        				
			        				//maybe use the variable query instead of rewriting(pasting) this
			        				query = "select (COUNT (distinct(?item)) AS ?count) "
							        		+ "where { "		        		
							        		+ occupationString
							        		+ nationalityString
							        		+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+gen +"> . "
							        		+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
							        		+ centuryofbirthString
							        		+ " ?item <http://www.wikidata.org/prop/direct/"+att +"> ?attribute . "
							        		+ "}";
			        		        qe = QueryExecutionFactory.sparqlService(endpoint, query);
			        		        rs = qe.execSelect();
			        		        qs = rs.next();
			        		        node = qs.get("count");
			        		        
			        		        String attnr = node.asLiteral().getValue().toString();	
			        				int attnrint = Integer.parseInt(attnr);
			        				int totalnrint = Integer.parseInt(totalnr);
			        				float result = attnrint/(float)totalnrint;
			        				String stringResult = Float.toString(result);
			        				
			        				String psqlQuery = "INSERT INTO human (occupation, nationality, centuryofbirth, gender, attribute, totalnr, attnr, result)"
			        						+"VALUES('"+occ+"', '"+nat+"','"+cob+"', '"+gen+"','"+att+"','"+totalnr +"', '"+attnr +"', '"+ stringResult +"')";
			        				stmt.executeUpdate(psqlQuery);
			        				System.out.println("Row number " + i +" "+ occ + " " + nat + " " + cob + " " + gen + " " + att + " "+ totalnr+ " "+ attnr + " " + result);
		        				
		        				}
		        			}
		        		}
		        	}
		        }
		        System.out.println("there were " + i + " queries made.");

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
