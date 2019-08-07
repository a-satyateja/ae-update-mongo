import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(name = "Tasks", description = "Create Cloud Task", urlPatterns = "/invokeconversion")
public class Test extends HttpServlet {
  private static Logger log = Logger.getLogger(Test.class.getName());
  static MongoClientURI mongoClientURI = new MongoClientURI("mongodb://localhost:27017");
  static MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClientURI);
  static MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("Received task request: " + req.getServletPath());

    String body = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

    if (!body.isEmpty()) {
      log.info("Request payload: " + body);
      String output = String.format("Received task with payload %s", body);
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = null;
      try {
        jsonObject = (JSONObject) jsonParser.parse(body);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      String patentNumber = jsonObject.get("patent_number").toString();
      List<String> images = (List) jsonObject.get("images");
      List bulkUpdateOperation = new ArrayList<>();
      bulkUpdateOperation.add(
          Pair.of(
              Query.query(Criteria.where("_id").is(patentNumber)),
              Update.update("images", images)));
      mongoTemplate
          .bulkOps(BulkOperations.BulkMode.ORDERED, "Patent")
          .updateMulti(bulkUpdateOperation)
          .execute();
      //                List<String> images = new ArrayList<>();
      //                String patentNumber = "";
      //                try {
      //                    JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
      //                    images = (List) jsonObject.get("images");
      //                } catch (ParseException e) {
      //                    e.printStackTrace();
      //                }
      //                if(images.size()>0) {
      //                    String firstImage = images.get(0);
      //                    String first[] = firstImage.split("/");
      //                    String last  = first[first.length-1];
      //                    String lastName[] = last.split("-");
      //                    patentNumber= lastName[0];
      //                    patentNumber = patentNumber.substring(2);
      //                }
      //                JSONObject j = new JSONObject();
      //                j.put("p",patentNumber);
      //                images.forEach(e->{
      //                            URL obj = null;
      //                            try {
      //                                obj = new
      // URL("https://convertion-dot-ipweb-240115.appspot.com/convert");
      //                            } catch (MalformedURLException ex) {
      //                                ex.printStackTrace();
      //                            }
      //                            HttpURLConnection con = null;
      //                            try {
      //                                con = (HttpURLConnection) obj.openConnection();
      //                            } catch (IOException ex) {
      //                                ex.printStackTrace();
      //                            }
      //                            try {
      //                                con.setRequestMethod("POST");
      //                                con.setRequestProperty("Content-Type","application/json");
      //                            } catch (ProtocolException ex) {
      //                                ex.printStackTrace();
      //                            }
      //                            con.setDoOutput(true);
      //                            JSONObject jsonObject = new JSONObject();
      //                            jsonObject.put("image_url",e);
      //                            jsonObject.put("patent_number",j.get("p"));
      //                            OutputStream os = null;
      //                            try {
      //                                os = con.getOutputStream();
      //                                os.write(jsonObject.toJSONString().getBytes());
      //                                os.flush();
      //                                os.close();
      //                            } catch (IOException ex) {
      //                                ex.printStackTrace();
      //                            }
      //                            int responseCode = 0;
      //                            try {
      //                                responseCode = con.getResponseCode();
      //                            } catch (IOException ex) {
      //                                ex.printStackTrace();
      //                            }
      //                          log.info("POST Response Code :: " + responseCode);
      //                });
      resp.setStatus(HttpServletResponse.SC_OK);
    } else {
      log.warning("Null payload received in request to " + req.getServletPath());
    }
  }
}
