package edu.byu.emergency.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.byu.emergency.AlertMessageRowMapper;
import edu.byu.emergency.domain.AlertMessage;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Allow all cross origin requests since this test server will not
 * be deployed in a production environment.
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class EmergencyAlertController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String announceString;
    private long timerStart;
    private long timerHandlerTotal;

    @GetMapping(value = "/ok")
    public ResponseEntity getTest(HttpServletRequest request) {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("I'm fine. How are you?");
    }

    @PostMapping(value = "/alerts")
    public ResponseEntity publishAlert(HttpServletRequest request, @RequestBody AlertMessage alertMessage) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(alertMessage);
    }

    @GetMapping(value = "/alerts")
    public ResponseEntity getAlertTest(HttpServletRequest request) {

        if (log.isDebugEnabled()) {
            timerHandlerTotal = System.currentTimeMillis();
        }

        ResponseEntity responseEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getAnnouncementsAsJson());
        log.debug("Handled request in " + new Long(System.currentTimeMillis() - timerHandlerTotal) + " milliseconds.");
        return responseEntity;
    }

    private String getAnnouncementsAsJson() {
        announceString = "";
        ArrayNode root = objectMapper.createArrayNode();

        if (log.isDebugEnabled()) {
            timerStart = System.currentTimeMillis(); // timing only
        }

        for(AlertMessage alertMessage : getAnnouncementData()) {
            ObjectNode announceNode = root.addObject();
            announceNode.put("id", alertMessage.getId());
            announceNode.put("title", alertMessage.getTitle());
            announceNode.put("abstract", alertMessage.getDescription());
            announceNode.put("message", alertMessage.getMessage());
            announceNode.put("link", alertMessage.getLink());
        }
        log.debug("Finished mapping results in " + new Long(System.currentTimeMillis() - timerStart) + " milliseconds"); // timing only
        if (root.size() > 0) {
            announceString = root.toString();
        }
        return announceString;
    }

    private List<AlertMessage> getAnnouncementData() {

        if (log.isDebugEnabled()) {
            timerStart = System.currentTimeMillis(); // timing only
        }

        String sqlQuery = "SELECT a.ann_id, a.title, a.abstract, a.message, a.link " +
                "FROM uportal.ANNOUNCEMENT a " +
                "INNER JOIN uportal.TOPIC t ON t.topic_id = a.parent_id " +
                "WHERE t.title = 'EMERGENCY' " +
                "AND a.published = true " +
                "AND curdate() BETWEEN a.start_display AND a.end_display";

        RowMapper<AlertMessage> rowMapper = new AlertMessageRowMapper();

        List<AlertMessage> messages = jdbcTemplate.query(sqlQuery, rowMapper);

        log.debug("Finished querying database in " + new Long(System.currentTimeMillis() - timerStart) + " milliseconds"); // timing only
//        return jdbcTemplate.query(sqlQuery, rowMapper);
        return messages;
    }

}
