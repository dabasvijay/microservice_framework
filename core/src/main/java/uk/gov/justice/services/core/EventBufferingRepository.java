package uk.gov.justice.services.core;

import uk.gov.justice.services.messaging.JsonEnvelope;

public interface EventBufferingRepository {

    void updateEventBuffer(JsonEnvelope jsonEnvelope);
}
