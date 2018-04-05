package uk.gov.justice.services.event.sourcing.subscription;

import static uk.gov.justice.services.core.interceptor.InterceptorContext.interceptorContextWithInput;

import uk.gov.justice.services.adapter.messaging.ISubscriptionManager;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.eventsourcing.source.core.EventSource;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.subscription.domain.Subscription;

public class SubscriptionManager implements ISubscriptionManager {

    private final Subscription subscription;
    private final EventSource eventSource;
    private final InterceptorChainProcessor interceptorChainProcessor;

    public SubscriptionManager(final Subscription subscription, final EventSource eventSource, final InterceptorChainProcessor interceptorChainProcessor) {
        this.subscription = subscription;
        this.eventSource = eventSource;
        this.interceptorChainProcessor = interceptorChainProcessor;
    }

    // Temporary until we decide what this class should do
    public Subscription getSubscription() {
        return subscription;
    }

    // Temporary until we decide what this class should do
    public EventSource getEventSource() {
        return eventSource;
    }

    @Override
    public void process(final JsonEnvelope jsonEnvelope) {
        interceptorChainProcessor.process(interceptorContextWithInput(jsonEnvelope));
    }
}
