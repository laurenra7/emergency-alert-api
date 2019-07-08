package edu.byu.emergency;

import edu.byu.emergency.domain.AlertMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * Map database query results to Java object. Used with JdbcTemplate.query method.
 */
public class AlertMessageRowMapper implements RowMapper<AlertMessage> {

    @Override
    public AlertMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        AlertMessage alertMessage = new AlertMessage();
        alertMessage.setId(rs.getInt("ann_id"));
        alertMessage.setTitle(rs.getString("title"));
        alertMessage.setDescription(rs.getString("abstract"));
        alertMessage.setMessage(rs.getString("message"));
        alertMessage.setLink(rs.getString("link"));
        return alertMessage;
    }

}
