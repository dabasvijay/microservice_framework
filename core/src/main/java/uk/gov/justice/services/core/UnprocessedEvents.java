package uk.gov.justice.services.core;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.List;

public class UnprocessedEvents {

    private final long sequenceId;
    private final List<JsonEnvelope> unprocessedEvents;
    private final EventBufferingRepository eventBufferingRepository;

    public UnprocessedEvents(final long sequenceId,
                             final List<JsonEnvelope> unprocessedEvents,
                             final EventBufferingRepository eventBufferingRepository) {
        this.sequenceId = sequenceId;
        this.unprocessedEvents = unprocessedEvents;
        this.eventBufferingRepository = eventBufferingRepository;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public List<JsonEnvelope> getUnprocessedEvents() {
        return unprocessedEvents;
    }
}
