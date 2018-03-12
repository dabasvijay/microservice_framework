package uk.gov.justice.services.core;

import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.metadataBuilder;

import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.eventsourcing.source.core.EventSource;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionManagerTest {

    public static final UUID STREAM_ID = fromString("ecf6a49e-b079-4064-ab7a-301c01367115");
    private static final String SOURCE = "HP Sauce";


    private JsonEnvelope firstJsonEnvelope = JsonEnvelope.envelopeFrom(metadataBuilder().withName(SOURCE).withId(randomUUID()).withStreamId(STREAM_ID).build(), createObjectBuilder().build());

    private Stream<JsonEnvelope> events;

    @Mock
    private InterceptorChain interceptorChain;

    @Mock
    private SubscriptionsService subscriptionsService;

    @Mock
    private Subscription subscription;

    @Mock
    private SubscriptionSpec subscriptionSpec;

    @Mock
    private EventSource eventSource;
    
    @InjectMocks
    private SubscriptionManager subscriptionManager;

    @Captor
    private ArgumentCaptor<InterceptorContext> interceptorContextArgumentCaptor;

    @Before
    public void setUp() {
        events = Stream.of(firstJsonEnvelope);
        when(subscriptionsService.getSubscriptionFor(SOURCE, STREAM_ID)).thenReturn(subscription);
    }

    @Test
    public void shouldPassEventToInterceptorChain() {

        when(subscription.read()).thenReturn(events);

        subscriptionManager.processEvent(firstJsonEnvelope);

        verify(subscription).append(firstJsonEnvelope);
        verify(interceptorChain).processNext(interceptorContextArgumentCaptor.capture());
        assertThat(interceptorContextArgumentCaptor.getValue().inputEnvelope(), is(firstJsonEnvelope));

    }
}