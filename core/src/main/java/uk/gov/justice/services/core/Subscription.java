package uk.gov.justice.services.core;

import static java.util.stream.Stream.concat;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Subscription {

    private long lastProcessedSequenceId;
    private final UUID streamId;
    private final UnprocessedEventsService unprocessedEventsService;
    private final List<JsonEnvelope> unprocessedEvents;

    public Subscription(final long lastProcessedSequenceId,
                        final UUID streamId,
                        final UnprocessedEventsService unprocessedEventsService) {
        this.lastProcessedSequenceId = lastProcessedSequenceId;
        this.streamId = streamId;
        this.unprocessedEventsService = unprocessedEventsService;
        this.unprocessedEvents = new ArrayList<>();
    }

    public void append(final JsonEnvelope jsonEnvelope) {

        final long nextSequenceId = lastProcessedSequenceId + 1;

        if(jsonEnvelope.metadata().version().get() == nextSequenceId) {
            unprocessedEvents.add(jsonEnvelope);
            unprocessedEventsService.updateLastProcessedSequenceId(nextSequenceId);
        } else {
            unprocessedEventsService.appendToBuffer(jsonEnvelope);
        }
    }

    public Stream<JsonEnvelope> read() {
        final UnprocessedEvents unprocessedEvents = unprocessedEventsService.getUnprocessedEvents(streamId);
        lastProcessedSequenceId = unprocessedEvents.getSequenceId();
        return concat(this.unprocessedEvents.stream(), unprocessedEvents.getUnprocessedEvents().stream());
    }

}
