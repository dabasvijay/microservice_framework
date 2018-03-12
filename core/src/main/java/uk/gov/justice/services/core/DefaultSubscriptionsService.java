package uk.gov.justice.services.core;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;

public class DefaultSubscriptionsService implements SubscriptionsService {

    @Inject
    SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription getSubscriptionFor(final String source, final UUID streamId) {
        return subscriptionRepository.getOrCreateSubscription(source, streamId);
    }
}
