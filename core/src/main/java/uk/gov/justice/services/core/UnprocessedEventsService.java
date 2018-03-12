package uk.gov.justice.services.core;


import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;

public interface UnprocessedEventsService {
    UnprocessedEvents getUnprocessedEvents(UUID streamId);

    void updateLastProcessedSequenceId(long lastProcessedSequenceId);

    void appendToBuffer(final JsonEnvelope jsonEnvelope);
}
