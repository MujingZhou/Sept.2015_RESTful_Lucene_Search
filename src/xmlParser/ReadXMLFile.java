package xmlParser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import database.DataBaseConnector;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.sql.Statement;

/*
 * -- Lab2 18-655 --
 * Name: Mujing Zhou
 * AndrewID: mujingz
 * 
 */

/* 
 * This project will parse the file "newdl.xml" under the src folder and store related information
 * into the database. There are three tables regarding the authors, publications(journals) and the relation
 * table which store the mapping between authors and publications to reduce the duplication in publication.
 * 
 * Five types of searching can be implemented after storing all of the information. Specific about these functions
 * are illustrated below.
 */
public class ReadXMLFile {	
	public static void ReadXML() {

		try {
			File here = new File(".");
			//System.out.println(here.getAbsolutePath());
			File fXmlFile = new File("newdl.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList_article = doc.getElementsByTagName("article");
			// insert all the information about article into database			
			System.out.println("-------------------------------------------------------------------");
			System.out.println("0 --Start storing information into database");
			insertInformation(nList_article);			
			System.out.println("0 --Storing finishes");
			System.out.println("-------------------------------------------------------------------\n");
			System.out.println("0 --Storing finishes");
			
			
			// Print out the publication details given the exact paper name.
//			System.out.println("1 --Given an exact paper name, list its publication details");
//			System.out.println("-------------------------------------------------------------------\n");
//			System.out.println("The paper name given is \""+"Time Lower Bounds for Parallel Sorting on a Mesh-Conected Processor Array."+"\"\n");
//			StringBuilder sb=getPublicationDetail("Time Lower Bounds for Parallel Sorting on a Mesh-Conected Processor Array.");
//			System.out.println(sb.toString());
//			System.out.println("-------------------------------------------------------------------\n");
//			
//			
//			// Given a keyword, print out all the paper title that contains this keyword.
//			System.out.println("2 -- Paper key word searching");
//			System.out.println("The key word for searching is "+"Computer\n");
//			sb=keywordPublicationTitle("Computer;Com;Math");
//			System.out.println(sb.toString());
//			System.out.println("-------------------------------------------------------------------\n");
//			
//			
//			// Given an author name, list all of her publications and detailed publication information 
//			System.out.println("3 -- Given an author name, list all of her publications and detailed publication information");
//			System.out.println("The author name given for searching is "+"Hantao Zhang");
//			System.out.println("-------------------------------------------------------------------\n");
//			sb=authorPublications("Hantao Zhang");
//			System.out.println(sb.toString());
//			System.out.println("-------------------------------------------------------------------\n");
//			
//			//  Given author name A, list all of her co-authors
//			System.out.println("4 -- Given author name A, list all of her co-authors");
//			System.out.println("The author name given for finding co-authors is "+"Amr Elmasry");
//			System.out.println("-------------------------------------------------------------------\n");
//			sb=listCoAuthors("Amr Elmasry");
//			System.out.println(sb.toString());
//			System.out.println("-------------------------------------------------------------------\n");
//			
//			// Given two author names, find out whether they ever co-author some papers and if yes, the details
//			System.out.println("5 -- Given two author names, find out whether they ever co-author some papers and if yes, the details");
//			System.out.println("-------------------------------------------------------------------\n");
//			sb=checkIfCoAuthor("Jyrki Katajainen", "John Iacono");
//			System.out.println(sb);
//			System.out.println();
//			sb=checkIfCoAuthor("Amr Elmasry", "John Iacono");
//			System.out.println(sb);
//			System.out.println("-------------------------------------------------------------------\n");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error happened!");
		}
		
		
	}

	// Main method to insert information into database.
	public static void insertInformation(NodeList nList_article) {
		try {
			DataBaseConnector db1 = new DataBaseConnector();
			db1.connectDataBase();

			Statement myStatement = db1.getMyStatement();
			db1.createDataBase(myStatement);
			for (int temp = 0; temp < 1000; temp++) {
				Node nNode_article = nList_article.item(temp);

				if (nNode_article.getNodeType() == Node.ELEMENT_NODE) {

					String paperTitle = "";
					int year = 0;
					String publicationChannel="Journal";
					String authorName = "";
					String journalName="";
					int volume=0;
					String page="";
					
					
					
					Element eElement = (Element) nNode_article;
					paperTitle = eElement.getElementsByTagName("title").item(0).getTextContent();
					year = Integer.valueOf(eElement.getElementsByTagName("year").item(0).getTextContent());
					journalName=eElement.getElementsByTagName("journal").item(0).getTextContent();
					volume = Integer.valueOf(eElement.getElementsByTagName("volume").item(0).getTextContent());
					page=eElement.getElementsByTagName("pages").item(0).getTextContent();
					
					int j = 0;
					while (eElement.getElementsByTagName("author").item(j) != null) {
						authorName = eElement.getElementsByTagName("author").item(j).getTextContent();
						j++;
						db1.insertAuthor(authorName);
						db1.insertRelation(paperTitle, authorName);
					}

					db1.insertJournal(paperTitle, year,publicationChannel,journalName,volume,page);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error happened!");
			System.exit(1);
		}
	}
	
	// get detailed publication information using the paper title
	public static StringBuilder getPublicationDetail(String paperTitle){
		StringBuilder sb=new StringBuilder();
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		sb=db1.getPublicationDetail(paperTitle);
		return sb;
	}
	
	// get all the publication that their titles contain the keyword.
	public static StringBuilder keywordPublicationTitle(String keywords){
		StringBuilder sb=new StringBuilder();
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		sb=db1.keywordPublicationTitle(keywords);
		return sb;
	}
	
	// get lists of publication details of a specific author
	public static StringBuilder authorPublications(String authorName){
		StringBuilder sb=new StringBuilder();
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		sb=db1.authorPublications(authorName);
		return sb;
	}

	// list the co-author of a given author.
	public static StringBuilder listCoAuthors(String authorName){
		StringBuilder sb=new StringBuilder();
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		sb=db1.listCoAuthors(authorName);
		
		return sb;
	}
	
	// check if two authors have ever co-authored a publication.
	public static StringBuilder checkIfCoAuthor(String authorName1, String authorName2){
		StringBuilder sb=new StringBuilder();
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		sb=db1.checkIfCoAuthor(authorName1, authorName2);
		return sb;
	}
	
	
	
	
	
	
	
	
}