import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            String body = req.getReader()
                    .lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);

            if (!body.isEmpty()) {
                log.info("Request payload: " + body);
                String output = String.format("Received task with payload %s", body);
                resp.getOutputStream().write(output.getBytes());
                log.info("Sending response: " + output);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                log.warning("Null payload received in request to " + req.getServletPath());
            }
        }
    }

