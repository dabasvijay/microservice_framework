package uk.gov.justice.services.core;

import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.spi.InterceptorContextProvider;
import uk.gov.justice.services.eventsourcing.source.core.EventSource;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class SubscriptionManager {

    @Inject
    InterceptorChain interceptorChain;

    @Inject
    SubscriptionsService subscriptionsService;

    @Inject
    SubscriptionSpec subscriptionSpec;

    @Inject
    EventSource eventSource;

    @Inject
    SubscriptionStrategy subscriptionStrategy;

    /**
     * This is the common method invoked by all adapters (MDB/JMS, or REST/Direct EventSources)
     */
    public void processEvent(final JsonEnvelope jsonEnvelope) {

        final Subscription subscription = subscriptionsService.getSubscriptionFor(sourceFrom(jsonEnvelope), streamIdFrom(jsonEnvelope));

        subscription.append(jsonEnvelope);
        subscription.read()
                .forEach(e -> interceptorChain.processNext(InterceptorContextProvider.provider().interceptorContextWithInput(e)));
    }

    private UUID streamIdFrom(final JsonEnvelope jsonEnvelope) {
        return jsonEnvelope.metadata().streamId().get();
    }

    private String sourceFrom(final JsonEnvelope jsonEnvelope) {
        return jsonEnvelope.metadata().name(); //should be something like jsonEnvelope.metadata().source
    }

    @PostConstruct
    public void onStartup() {
        // Check the strategy
        // Need to workout if the strategy just has various options true/false or if the subscription strategy actually includes the
        // the logic and this will call strategy.implementStrategy(this);
//        if (subscriptionStrategy.pullOnStartup()) {
//            // Do some stuff
//        }
        // Or is it the subscriptionManager itself that is sub-classed into knowing how to do things?
        // ProcessEvent being the default method
        subscriptionStrategy.runStrategy();



    }

}
