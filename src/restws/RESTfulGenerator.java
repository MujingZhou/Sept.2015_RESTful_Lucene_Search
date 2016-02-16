package restws;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.lucene.queryparser.classic.ParseException;
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

import database.DataBaseConnector;
import lucene.Lucene_Search;
import model.SearchPublicationYear;
import model.SearchResult;

/**
 * 
 * @author pavithra
 * 
 */

// @Path here defines class level path. Identifies the URI path that
// a resource class will serve requests for.
@Path("UserInfoService")
public class RESTfulGenerator {

	// @GET here defines, this method will method will process HTTP GET
	// requests.
	@GET
	// @Path here defines method level path. Identifies the URI path that a
	// resource class method will serve requests for.
	@Path("/basicSearch/key={key}/skip={skip}/count={count}")
	// @Produces here defines the media type(s) that the methods
	// of a resource class can produce.
	@Produces(MediaType.TEXT_XML)
	// @PathParam injects the value of URI parameter that defined in @Path
	// expression, into the method.
	public String userName(@PathParam("key") String key,@PathParam("skip") String skip,@PathParam("count") String count) {
		 SearchResult[] result;
		 Lucene_Search s1 = new Lucene_Search();
		 result = s1.basicSearch2(key, Integer.valueOf(skip), Integer.valueOf(count));
		 StringBuilder sb = new StringBuilder();
		 sb.append("<dblp>\n");
		 for (int i=0;i<result.length;i++){
			 sb.append("<article>");
			 sb.append("<a_id>"+(i+1)+"</a_id>");
			 sb.append("<title>"+result[i].getTitle()+"</title>");
			 sb.append("<paperId>"+result[i].getPaperId()+"</paperId>");
			 sb.append("<year>"+result[i].getYear()+"</year>");
			 sb.append("<volume>"+result[i].getVolume()+"</volume>");
			 sb.append("<page>"+result[i].getPage()+"</page>");
			 sb.append("<journalName>"+result[i].getJournalName()+"</journalName>");
			 sb.append("</article>\n");
		 }
		 sb.append("</dblp>");
//		 System.out.println(result[0].getTitle());
		 return sb.toString();
//		return "<User>" + "<Name>" + key + "</Name>" + "</User>";
	}

	@GET
	@Path("/spatialSearch/key={key}/lower={lower}/upper={upper}/skip={skip}/count={count}")
	@Produces(MediaType.TEXT_XML)
	public String userAge(@PathParam("key") String key,@PathParam("lower") int lower,@PathParam("upper") int upper,
			@PathParam("skip") int skip,@PathParam("count") int count) {

		SearchResult[] result;
		 Lucene_Search s1 = new Lucene_Search();
		 SearchPublicationYear region = new
					 SearchPublicationYear(Integer.valueOf(lower), 1,
					 Integer.valueOf(upper), 1);
		 result = s1.spatialSearch2(key, region, Integer.valueOf(skip), Integer.valueOf(count));
		 StringBuilder sb = new StringBuilder();
		 sb.append("<dblp>");
		 for (int i=0;i<result.length;i++){
			 sb.append("<article>");
			 sb.append("<a_id>"+(i+1)+"</a_id>");
			 sb.append("<title>"+result[i].getTitle()+"</title>");
			 sb.append("<paperId>"+result[i].getPaperId()+"</paperId>");
			 sb.append("<year>"+result[i].getYear()+"</year>");
			 sb.append("<volume>"+result[i].getVolume()+"</volume>");
			 sb.append("<page>"+result[i].getPage()+"</page>");
			 sb.append("<journalName>"+result[i].getJournalName()+"</journalName>");
			 sb.append("</article>\n");
		 }
		 sb.append("</dblp>");
//		 System.out.println(result[0].getTitle());
		 return sb.toString();
	}
}