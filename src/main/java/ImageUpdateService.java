
import com.mongodb.MongoClientURI;
import com.mongodb.bulk.BulkWriteResult;
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
import org.springframework.data.util.Pair;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(
        name = "UpdateImagePath",
        description = "Update Image Paths In MongoDb",
        urlPatterns = "/updateImagePath")
public class ImageUpdateService extends HttpServlet {
    private static Logger log = Logger.getLogger(ImageUpdateService.class.getName());
    static MongoClientURI mongoClientURI =
            new MongoClientURI(
                    "mongodb://35.192.151.206:27017/demoapp");
    static MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClientURI);
    static MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("received req params of : " + req.getParameter("job"));
        if (req.getParameter("job").equals("imageupdate")) {

            log.info("Received task request: " + req.getServletPath());
            String body =
                    req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            if (!body.isEmpty()) {
                log.info("Request payload: " + body);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                try {
                    jsonObject = (JSONObject) jsonParser.parse(body);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String patentNumber = jsonObject.get("patent_number").toString();
                String patent_year = jsonObject.get("patent_year").toString();
                List<String> images = (List) jsonObject.get("images");
                List<String> updatedImages = new ArrayList<>();
                log.info("Received Image length:" + images.size());
                images.forEach(
                        image -> {
                            String[] splitImagePath = image.split("/");
                            String imageName = splitImagePath[splitImagePath.length - 1];
                            String[] imagePathWithDashes = imageName.split("-");
                            String finalImagepath = imagePathWithDashes[imagePathWithDashes.length - 1];
                            String finalImageName = finalImagepath.replace(".TIF", ".png");
                            String finalImagePath =
                                    "https://storage.googleapis.com/"
                                            + "ipweb-data/images"
                                            + "/"
                                            + patent_year
                                            + "/"
                                            + patentNumber
                                            + "/"
                                            + finalImageName;
                            updatedImages.add(finalImagePath);
                        });
                List bulkUpdateOperation = new ArrayList<>();
                bulkUpdateOperation.add(
                        Pair.of(
                                Query.query(Criteria.where("_id").is(patentNumber)),
                                Update.update("images", updatedImages)));
                BulkWriteResult bulkWriteResult =
                        mongoTemplate
                                .bulkOps(BulkOperations.BulkMode.ORDERED, "test")
                                .updateMulti(bulkUpdateOperation)
                                .execute();
                log.info("final result:" + bulkWriteResult.getModifiedCount());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                log.warning("Null payload received in request to " + req.getServletPath());
            }
        }
        else if(req.getParameter("job").equals("transfer")){
            log.info("executing transfer job");
            List totalDocs = mongoTemplate.findAll( Patent.class);
            mongoTemplate.save(totalDocs,"Patent");
            mongoTemplate.remove(Patent.class);
            resp.setStatus(HttpServletResponse.SC_OK);

        }
    }
}
