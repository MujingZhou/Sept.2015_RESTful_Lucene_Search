
package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/*
 * -- Lab2 18-655 --
 * Name: Mujing Zhou
 * AndrewID: mujingz
 * 
 */

/* 
 * This class is used to interact with database using request information sent from the ReadXMLFile class.
 */
public class DataBaseConnector {
	private Connection myConnection;
	private Statement myStatement;

	public Connection getMyConnection() {
		return myConnection;
	}

	public Statement getMyStatement() {
		return myStatement;
	}

	// connect to database using username: root and no password
	public void connectDataBase() {

		String url = "jdbc:mysql://localhost";
		try

		{
			Class.forName("com.mysql.jdbc.Driver");
			myConnection = DriverManager.getConnection(url, "root", "");
			myStatement = myConnection.createStatement();
		} catch (

		ClassNotFoundException e)

		{
			e.printStackTrace();
		} catch (

		SQLException e1)

		{
			e1.printStackTrace();
		}
	}

	/*
	 * createDataBase -- create the database from the given text file..
	 */
	public void createDataBase(Statement myStatement) {
		String filename = "create_database.txt";
		FileReader file;
		try {
			file = new FileReader(filename);

			BufferedReader buff = new BufferedReader(file);
			String line;
			while ((line = buff.readLine()) != null) {
				myStatement.executeUpdate(line);
			}
			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	// check if the author name has duplicated
	public boolean checkDuplicateName(String authorName) {

		String author = null;

		try {
			if (authorName.indexOf('\'') != -1) {
				authorName = authorName.replace("'", "''");
			}
			ResultSet result1 = myStatement
					.executeQuery("select authorName from Publication.Author where authorName = '" + authorName + "'");

			while (result1.next()) {
				author = result1.getString("authorName");

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (author != null)
			return false;
		else
			return true;

	}

	// check if the publication has duplicated
	public boolean checkDuplicatePublication(String publicationName) {

		String publication = null;

		try {
			if (publicationName.indexOf('\'') != -1) {
				publicationName = publicationName.replace("'", "''");
			}
			ResultSet result1 = myStatement.executeQuery(
					"select paperTitle from Publication.Journal where paperTitle = '" + publicationName + "'");

			while (result1.next()) {
				publication = result1.getString("paperTitle");

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (publication != null)
			return false;
		else
			return true;

	}

	// insert author information into the author table in the database
	public void insertAuthor(String authorName) {
		if (checkDuplicateName(authorName) == true) {
			try {
				if (authorName.indexOf('\'') != -1) {
					authorName = authorName.replace("'", "''");
				}
				myStatement
						.executeUpdate(" insert into Publication.Author values(" + null + ",'" + authorName + "'); ");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// insert journal information into journal table in database
	public void insertJournal(String paperTitle, int year, String publicationChannel, String journalName, int volume,
			String page) {

		try {
			if (paperTitle.indexOf('\'') != -1) {
				paperTitle = paperTitle.replace("'", "''");
			}
			String subCommand = "insert into Publication.Journal(paperTitle,year,publicationChannel,journalName,volume,page) "
					+ "values(?,?,?,?,?,?);";
			PreparedStatement ps1 = myConnection.prepareStatement(subCommand);
			ps1.setString(1, paperTitle);
			ps1.setInt(2, year);
			ps1.setString(3, publicationChannel);
			ps1.setString(4, journalName);
			ps1.setInt(5, volume);
			ps1.setString(6, page);

			ps1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// insert relationship between publication and author into relation table in
	// database
	public void insertRelation(String paperTitle, String authorName) {

		try {
			if (authorName.indexOf('\'') != -1) {
				authorName = authorName.replace("'", "''");
			}
			if (paperTitle.indexOf('\'') != -1) {
				paperTitle = paperTitle.replace("'", "''");
			}
			String subCommand = "insert into Publication.Relation(paperTitle,authorName) " + "values(?,?);";
			PreparedStatement ps1 = myConnection.prepareStatement(subCommand);

			ps1.setString(1, paperTitle);
			ps1.setString(2, authorName);
			ps1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	// retrieve detailed publication information
	public StringBuilder getPublicationDetail(String paperTitle) {
		StringBuilder sb = new StringBuilder();
		try {
			if (paperTitle.indexOf('\'') != -1) {
				paperTitle = paperTitle.replace("'", "''");
			}
			ResultSet result0 = myStatement
					.executeQuery("select * from Publication.Relation where paperTitle = '" + paperTitle + "'");

			ArrayList<String> authorNameList = new ArrayList<>();
			String publicationChannel = "";
			String year = "";
			String journalName = "";
			String volume = "";
			String page = "";
			while (result0.next()) {
				String authorName = result0.getString("authorName");
				authorNameList.add(authorName);
			}

			ResultSet result1 = myStatement
					.executeQuery("select * from Publication.Journal where paperTitle = '" + paperTitle + "'");

			while (result1.next()) {
				publicationChannel = result1.getString("publicationChannel");
				year = String.valueOf(result1.getInt("year"));
				journalName = result1.getString("journalName");
				volume = String.valueOf(result1.getInt("volume"));
				page = result1.getString("page");
			}
			sb.append("Publication Name: " + paperTitle + "\n");
			sb.append("Author(s): ");
			for (int i = 0; i < authorNameList.size(); i++) {
				sb.append((i + 1) + ". " + authorNameList.get(i) + "   ");
			}
			sb.append("\nYear: ");
			sb.append(year);
			sb.append("\nPublication Channel: ");
			sb.append(publicationChannel);
			sb.append("\njournal Name: ");
			sb.append(journalName);
			sb.append("\nvolume: ");
			sb.append(volume);
			sb.append("\npage: ");
			sb.append(page);
			sb.append("\n");
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		return sb;
	}

	// using keywords to find publication title in database
	// keywords are separated with comma
	public StringBuilder keywordPublicationTitle(String keywords) {
		StringBuilder sb = new StringBuilder();
		String[] arrayKeyword = keywords.split(";");

		for (int j = 0; j < arrayKeyword.length; j++) {
			String keyword = arrayKeyword[j];
			sb.append("The keyword is " + keyword + "\n");
			try {
				String paperTitle = "";
				ResultSet result1 = myStatement
						.executeQuery("select * from Publication.Journal where paperTitle LIKE '%" + keyword + "%'");
				int i = 0;
				while (result1.next()) {
					i++;
					paperTitle = result1.getString("paperTitle");
					sb.append((i) + "  " + paperTitle);
					sb.append("\n");
				}
			} catch (SQLException e) {
				e.printStackTrace();

			}
			sb.append("\n");
			sb.append("\n");
		}
		return sb;
	}

	// find publication related with an author
	public StringBuilder authorPublications(String authorName) {
		StringBuilder sb = new StringBuilder();
		sb.append("Author: " + authorName + "\n");
		try {
			ResultSet result0 = myStatement
					.executeQuery("select * from Publication.Relation where authorName = '" + authorName + "'");

			ArrayList<String> paperList = new ArrayList<>();
			String publicationChannel = "";
			String year = "";
			String journalName = "";
			String volume = "";
			String page = "";
			while (result0.next()) {
				String paperTitle = result0.getString("paperTitle");
				paperList.add(paperTitle);
			}

			for (int i = 0; i < paperList.size(); i++) {
				ResultSet result1 = myStatement.executeQuery(
						"select * from Publication.Journal where paperTitle = '" + paperList.get(i) + "'");

				while (result1.next()) {
					publicationChannel = result1.getString("publicationChannel");
					year = String.valueOf(result1.getInt("year"));
					journalName = result1.getString("journalName");
					volume = String.valueOf(result1.getInt("volume"));
					page = result1.getString("page");
					sb.append((i + 1) + " Paper Title:  " + paperList.get(i) + "\n");
					sb.append("Year: ");
					sb.append(year);
					sb.append("\nPublication Channel: ");
					sb.append(publicationChannel);
					sb.append("\njournal Name: ");
					sb.append(journalName);
					sb.append("\nvolume: ");
					sb.append(volume);
					sb.append("\npage: ");
					sb.append(page);
					sb.append("\n\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb;
	}

	// list the co-authors with an author
	public StringBuilder listCoAuthors(String authorName) {
		StringBuilder sb = new StringBuilder();
		HashSet<String> authorSet = new HashSet<>();
		try {
			ResultSet result1 = myStatement
					.executeQuery("select * from Publication.Relation where authorName = '" + authorName + "'");
			ArrayList<String> paperList = new ArrayList<>();
			while (result1.next()) {
				String paperTitle = result1.getString("paperTitle");
				paperList.add(paperTitle);
			}

			for (int i = 0; i < paperList.size(); i++) {
				String paperTitle = paperList.get(i);
				ResultSet result2 = myStatement
						.executeQuery("select * from Publication.Relation where paperTitle = '" + paperTitle + "'");
				while (result2.next()) {
					String author = result2.getString("authorName");
					authorSet.add(author);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		authorSet.remove(authorName);
		Iterator<String> i1 = authorSet.iterator();
		int i = 1;
		while (i1.hasNext()) {
			sb.append(i + ".  " + i1.next() + "  ");
			i++;
		}

		return sb;
	}

	// check whether two author has co-authored any book. If yes, print those
	// boos information.
	public StringBuilder checkIfCoAuthor(String authorName1, String authorName2) {

		boolean flag = false;
		StringBuilder sb = new StringBuilder();
		try {

			sb.append(authorName1 + " and " + authorName2 + " has co-author the following publication:\n");

			ResultSet result1 = myStatement
					.executeQuery("select * from Publication.Relation where authorName = '" + authorName1 + "'");
			ArrayList<String> paperList1 = new ArrayList<>();
			while (result1.next()) {
				String paperTitle = result1.getString("paperTitle");
				paperList1.add(paperTitle);
			}

			ResultSet result2 = myStatement
					.executeQuery("select * from Publication.Relation where authorName = '" + authorName2 + "'");
			ArrayList<String> paperList2 = new ArrayList<>();
			while (result2.next()) {
				String paperTitle = result2.getString("paperTitle");
				paperList2.add(paperTitle);
			}

			int count = 1;
			for (int i = 0; i < paperList1.size(); i++) {
				if (paperList2.contains(paperList1.get(i))) {
					flag = true;
					StringBuilder tmp = getPublicationDetail(paperList1.get(i));
					sb.append(count + " . ");
					count++;
					sb.append(tmp.toString());
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (flag == false) {
			StringBuilder sb2 = new StringBuilder();
			sb2.append(authorName1 + " and " + authorName2 + " did not co-author any publication");
			return sb2;
		} else
			return sb;
	}

	public HashMap<String,ArrayList<String>> getAllInfo() {
		ResultSet result1;
		HashMap<String,ArrayList<String>> map = new HashMap<>();
		map.put("title", new ArrayList<String>());
		map.put("paperId",new ArrayList<String>());
		map.put("year", new ArrayList<String>());
		map.put("journalName", new ArrayList<String>());
		map.put("volume", new ArrayList<String>());
		map.put("page", new ArrayList<String>());
		
		try {
			result1 = myStatement.executeQuery("select * from Publication.Journal");
			String paperID ="";
			String title = "";
			String year = "";
			String journalName = "";
			String volume = "";
			String page="";
			while (result1.next()) {
				title = result1.getString("paperTitle");
				paperID = String.valueOf(result1.getInt("journalId"));
				year = String.valueOf(result1.getInt("year"));
				journalName = result1.getString("journalName");
				volume = String.valueOf(result1.getString("volume"));
				page = result1.getString("page");
				
				map.get("title").add(title);
				map.get("paperId").add(paperID);
				map.get("year").add(year);
				map.get("journalName").add(journalName);
				map.get("volume").add(volume);
				map.get("page").add(page);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public void insertIndex(){
		try {
		ResultSet result1 = myStatement.executeQuery("select * from Publication.Journal");
		ArrayList<String> setOfQueries = new ArrayList<>();
		int paperId;
		int year;
			while(result1.next()){
				paperId = result1.getInt("journalId");
				year = result1.getInt("year");
				String query="INSERT INTO Publication.JournalGeo(g,paperID) VALUES (GeomFromText('POINT("+year+" 1)'),"+paperId+");";
				setOfQueries.add(query);
			}
			
			for (int i=0;i<setOfQueries.size();i++){
				myStatement.execute(setOfQueries.get(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		spacialSearch(1988, 1992);
	}
	
	public ArrayList<Integer> spacialSearch(int lowerYear, int upperYear){
		ArrayList<Integer> paperIdList = new ArrayList<>();
		try {
		String query = "select * from Publication.JournalGeo where MBRContains(GeomFromText"
				+ "('Polygon(("+lowerYear+" 1, "+lowerYear+" 1, "+upperYear+" 1, "+upperYear+" 1, "+lowerYear+" 1))'),g);";
//		System.out.println(query);
		ResultSet result1=myStatement.executeQuery(query);

		while(result1.next()){
//			System.out.println(result1.getInt("paperID"));
			paperIdList.add(result1.getInt("paperID"));
		}
		
		
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paperIdList;
		
	}

}