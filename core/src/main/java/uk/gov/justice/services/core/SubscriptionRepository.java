package uk.gov.justice.services.core;

import java.util.UUID;

public interface SubscriptionRepository {

    Subscription getSubscription(final String source, final UUID streamId);
    void createSubscription(final String source, final UUID streamId);
    Subscription getOrCreateSubscription(final String source, final UUID streamId);
}
