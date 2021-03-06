package uk.gov.justice.services.eventsourcing.repository.jdbc.event;

import static java.lang.String.format;

import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.inject.Inject;

public class OpenEjbAwareEventJdbcRepository  {

    @Inject
    EventJdbcRepository eventJdbcRepository;

    private static final String SQL_EVENT_LOG_COUNT_BY_STREAM_ID = "SELECT count(*) FROM event_log WHERE stream_id=? ";

    public int eventCount(final UUID streamId) {

        try (final Connection connection = eventJdbcRepository.dataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SQL_EVENT_LOG_COUNT_BY_STREAM_ID)) {
            preparedStatement.setObject(1, streamId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(format("Exception getting count of event log entries for %s", streamId), e);
        }
    }
}
