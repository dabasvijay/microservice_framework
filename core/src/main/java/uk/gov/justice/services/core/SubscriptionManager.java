package uk.gov.justice.services.core;

import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.spi.InterceptorContextProvider;
import uk.gov.justice.services.eventsourcing.source.core.EventSource;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by matt on 08/03/2018.
 */
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

        // Would this need to return whether or not we continue processing?
        // If it is stored in buffer (out of sequence) then we don't do any more.
        // If it is the missing even in a buffer would it need to return all the events that are to be processed?
        // Maybe it returns stream<JsonEnvelope> ?
        Stream<JsonEnvelope> events = subscriptionsService.updateSubscriptionFor(jsonEnvelope);

        events.forEach(e->interceptorChain.processNext(InterceptorContextProvider.provider().interceptorContextWithInput(e)));
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
