package uk.gov.justice.services.core;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;
import java.util.stream.Stream;

public interface SubscriptionsService {

    Subscription getSubscriptionFor(final String source, final UUID streamId);

}
