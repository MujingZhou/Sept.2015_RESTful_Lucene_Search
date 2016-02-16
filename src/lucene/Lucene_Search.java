package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import database.DataBaseConnector;
import model.SearchPublicationYear;
import model.SearchResult;
import xmlParser.ReadXMLFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Lucene_Search {
//	 public void lucene_Search() throws IOException, ParseException {
//	 ReadXMLFile.ReadXML();
//	 System.out.println("Please input keywords");
//	 BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//	 String input = br.readLine();
//	 String query = input;
//	 SearchResult[] basicResult = basicSearch(query, 0, 30);
//	 displayResult(basicResult);
//	 System.out.println("\n\n");
//	
//	
//	 System.out.println("Please enter year lower bound(e.g 1988)");
//	 String lowBound = br.readLine();
//	 System.out.println("Please enter year upper bound(e.g 1995)");
//	 String upBound = br.readLine();
//	
//	 SearchPublicationYear region = new
//	 SearchPublicationYear(Integer.valueOf(lowBound), 1,
//	 Integer.valueOf(upBound), 1);
//	 SearchResult[] spatialRsult = spatialSearch(query, region, 0, 30);
//	 displayResult(spatialRsult);
//	 }

	public void addDoc(IndexWriter w, String title, String paperID, String year, String journalName, String volume,
			String page) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		// use a string field for isbn because we don't want it tokenized
		doc.add(new StringField("paperId", paperID, Field.Store.YES));
		doc.add(new StringField("year", year, Field.Store.YES));
		doc.add(new StringField("journalName", journalName, Field.Store.YES));
		doc.add(new StringField("volume", volume, Field.Store.YES));
		doc.add(new StringField("page", page, Field.Store.YES));

		w.addDocument(doc);
	}

	public SearchResult[] basicSearch2(String query, int numResultsToSkip, int numResultsToReturn) {
		
		try {
			return basicSearch(query, numResultsToSkip, numResultsToReturn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SearchResult[] basicSearch(String query, int numResultsToSkip, int numResultsToReturn)
			throws IOException, ParseException {

		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		HashMap<String, ArrayList<String>> map = db1.getAllInfo();

		for (int i = 0; i < map.get("title").size(); i++) {
			addDoc(w, map.get("title").get(i), map.get("paperId").get(i), map.get("year").get(i),
					map.get("journalName").get(i), map.get("volume").get(i), map.get("page").get(i));
		}

		w.close();

		Query q = new QueryParser("title", analyzer).parse(query);
		// 3.search

		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(numResultsToSkip + numResultsToReturn);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		if (hits.length <= numResultsToSkip) {
			SearchResult[] result = new SearchResult[0];
			return null;
		}
		SearchResult[] result = new SearchResult[Math.min(hits.length, numResultsToReturn)];
		// 4.display results
		// System.out.println("Found " + hits.length + " hits.");
		// System.out.println(args[0]);
		for (int i = numResultsToSkip; i < Math.min(hits.length, numResultsToSkip + numResultsToReturn); ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			result[i - numResultsToSkip] = new SearchResult(Integer.valueOf(d.get("paperId")), d.get("title"),
					Integer.valueOf(d.get("year")), d.get("journalName"), Integer.valueOf(d.get("volume")),
					d.get("page"));
		}
		// reader can only be closed when there
		// is no need to access the documents anymore.
		reader.close();

		return result;
	}

	public SearchResult[] spatialSearch2(String query, SearchPublicationYear yearRegion, int numResultsToSkip,
			int numResultsToReturn) {

		try {
			return spatialSearch(query, yearRegion,numResultsToSkip, numResultsToReturn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SearchResult[] spatialSearch(String query, SearchPublicationYear yearRegion, int numResultsToSkip,
			int numResultsToReturn) throws IOException, ParseException {
		DataBaseConnector db1 = new DataBaseConnector();
		db1.connectDataBase();
		db1.insertIndex();

		int lowerYear = yearRegion.getLowerLeft();
		int upperYear = yearRegion.getUpperLeft();
		ArrayList<Integer> paperIdList = db1.spacialSearch(lowerYear, upperYear);
		// System.out.println(paperIdList.size());
		// for (int i=0;i<paperIdList.size();i++){
		// System.out.println(paperIdList.get(i));
		// }

		SearchResult[] result = basicSearch2(query, numResultsToSkip, numResultsToReturn);
		ArrayList<SearchResult> resultList = new ArrayList<>();
		for (int i = 0; i < result.length; i++) {
			if (paperIdList.contains(result[i].getPaperId())) {
				resultList.add(result[i]);
			}
		}

		SearchResult[] finalResult = new SearchResult[resultList.size()];
		for (int i = 0; i < resultList.size(); i++) {
			finalResult[i] = resultList.get(i);
		}

		return finalResult;
	}

	public void displayResult(SearchResult[] result) {
		for (int i = 0; i < result.length; i++) {
			System.out.println(i + 1 + ". " + " (ID: " + result[i].getPaperId() + ") " + result[i].getTitle() + "   "
					+ "(" + result[i].getYear() + result[i].getJournalName() + ")" + "Volume: " + result[i].getVolume()
					+ "Page: " + result[i].getPage());
		}
	}
}
