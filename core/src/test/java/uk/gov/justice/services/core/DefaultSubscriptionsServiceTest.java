package uk.gov.justice.services.core;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSubscriptionsServiceTest {

    private static final UUID UUID = randomUUID();
    private static final String SOURCE = "HP Sauce";

    @Mock
    private JsonEnvelope jsonEnvelope;

    @Mock
    private Subscription subscription;

    @Mock
    private Stream<JsonEnvelope> unprocessedEvents;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private DefaultSubscriptionsService subscriptionsService;

    @Test
    public void shouldCreateNewSubscription() {

        when(subscriptionRepository.getOrCreateSubscription(SOURCE, UUID)).thenReturn(subscription);

        final Subscription returnedSubscription = subscriptionsService.getSubscriptionFor(SOURCE, UUID);

        assertThat(returnedSubscription, is(subscription));

    }

}