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
        String[] occupation = {"Q82955", "Q40348", "Q937857"};
        String[] nationality = {"Q30", "Q183", "Q148"};
        String[] attribute = {"P40", "P569", "P102"};
        String[] gender = {"Q6581097", "Q6581072"};
        
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
		        	for(String nat : nationality){
		        		for(String gen : gender){
		        			for(String att: attribute){
		        		    	query = "select (COUNT(?item) AS ?count) "
				        		+ "where { "		        		
				        		+ "?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occ +"> ."
				        		+ "?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nat +"> . "
				        		+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+gen +"> . "
				        		+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
				        		+ "}";
		        				i++;
		        		        qe = QueryExecutionFactory.sparqlService(endpoint, query);
		        		        rs = qe.execSelect();
		        		        qs = rs.next();
		        		        node = qs.get("count");
		        		        String totalnr = node.asLiteral().getValue().toString();	
		        		        System.out.println("totalnr is: " + totalnr);		        		        		        				
		        				System.out.println("ok totalnr " + i);
		        				
		        				query = "select (COUNT (distinct(?item)) AS ?count) "
						        		+ "where { "		        		
						        		+ "?item <http://www.wikidata.org/prop/direct/P106> <http://www.wikidata.org/entity/"+occ +"> ."
						        		+ "?item <http://www.wikidata.org/prop/direct/P27> <http://www.wikidata.org/entity/"+nat +"> . "
						        		+ "?item <http://www.wikidata.org/prop/direct/P21> <http://www.wikidata.org/entity/"+gen +"> . "
						        		+ "?item <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q5> . "
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
		        				
		        				String psqlQuery = "INSERT INTO human (occupation, nationality, gender, attribute, totalnr, attnr, result)"
		        						+"VALUES('"+occ+"', '"+nat+"', '"+gen+"','"+att+"','"+totalnr +"', '"+attnr +"', '"+ stringResult +"')";
		        				stmt.executeUpdate(psqlQuery);
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
