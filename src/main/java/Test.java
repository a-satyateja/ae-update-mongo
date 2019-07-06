import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;
import org.json.simple.parser.JSONParser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(
            name = "Tasks",
            description = "Create Cloud Task",
            urlPatterns = "/tasks/create"
    )
    public class Test extends HttpServlet {
        private static Logger log = Logger.getLogger(Test.class.getName());

        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            log.info("Received task request: " + req.getServletPath());
            JSONParser jsonParser = new JSONParser();
            int i =0;
            int start = 1;
            String projectId = "ipweb-240115";
            String subscriptionId = "patent-image-data";
            while(start>0){
                try{
                    List<ReceivedMessage> messages = createSubscriberWithSyncPull(projectId,subscriptionId,20);
                    Map<String,List<String>> imagesMap = new HashMap<>();
                    Map<String,List<String>> xmlMap = new HashMap<>();
                    Map<String,List<String>> otherMap = new HashMap<>();
                    List bulkUpdateOperation = new ArrayList<>();
                    messages.forEach(message->{
                        try {
                            String messageString = message.getMessage().getData().toStringUtf8();
                            log.info("***********"+messageString);
                            //						JSONObject jsonObject = (JSONObject) jsonParser.parse(messageString);
                            //						String patentNumber  = jsonObject.get("patentNumber").toString();
                            //						List<String> images  = (List)jsonObject.get("images");
                            //						List<String> xml  =    (List)jsonObject.get("xml");
                            //						List<String> others  = (List)jsonObject.get("others");
                            //bulkUpdateOperation.add(Pair.of(Query.query(Criteria.where("_id").is(patentNumber)),Update.update("images",images) ));
                        } catch (Exception e) {
                            e.printStackTrace();
                           log.info("********************************************************************************");
                        }

                    });
                    //	mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,"Patent").updateMulti(bulkUpdateOperation).execute();
                    i = i+messages.size();
                    System.out.println(i);
                    if(messages.size()==0){
                        start =0;
                    }
                }
                catch (Exception e){
                    log.log(Level.INFO,"*************"+e.getMessage());
                }
            }
            log.info("Done");
        }

    static List<ReceivedMessage> createSubscriberWithSyncPull(
            String projectId, String subscriptionId, int numOfMessages) throws Exception {
        // [START pubsub_subscriber_sync_pull]
        SubscriberStubSettings subscriberStubSettings =
                SubscriberStubSettings.newBuilder()
                        .setTransportChannelProvider(
                                SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
                                        .setMaxInboundMessageSize(20 << 20) // 20MB
                                        .build())
                        .build();

        try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
            // String projectId = "my-project-id";
            // String subscriptionId = "my-subscription-id";
            // int numOfMessages = 10;   // max number of messages to be pulled
            String subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
            PullRequest pullRequest =
                    PullRequest.newBuilder()
                            .setMaxMessages(numOfMessages)
                            .setReturnImmediately(false) // return immediately if messages are not available
                            .setSubscription(subscriptionName)
                            .build();

            // use pullCallable().futureCall to asynchronously perform this operation
            PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
            List<String> ackIds = new ArrayList<>();
            for (ReceivedMessage message : pullResponse.getReceivedMessagesList()) {
                // handle received message
                // ...
                ackIds.add(message.getAckId());
            }
            // acknowledge received messages
            AcknowledgeRequest acknowledgeRequest =
                    AcknowledgeRequest.newBuilder()
                            .setSubscription(subscriptionName)
                            .addAllAckIds(ackIds)
                            .build();
            // use acknowledgeCallable().futureCall to asynchronously perform this operation
            subscriber.acknowledgeCallable().call(acknowledgeRequest);
            return pullResponse.getReceivedMessagesList();
        }
    }
    }

