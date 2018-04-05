package uk.gov.justice.services.event.sourcing.subscription;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;

import uk.gov.justice.subscription.domain.Subscription;
import uk.gov.justice.subscription.domain.SubscriptionDescriptor;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class SubscriptionDescriptorRegistry {

    private final Map<String, SubscriptionDescriptor> registry;

    public SubscriptionDescriptorRegistry(final Map<String, SubscriptionDescriptor> registry) {
        this.registry = registry;
    }

    public Optional<SubscriptionDescriptor> getSubscriptionDescriptorFor(final String serviceName) {
        return ofNullable(registry.get(serviceName));
    }

    public Subscription getSubscription(final String subscriptionName) {

        return registry.values().stream()
                .map(SubscriptionDescriptor::getSubscriptions)
                .flatMap(Collection::stream)
                .filter(subscription -> subscriptionNameFrom(subscription).equals(subscriptionName))
                .findFirst()
                .orElseThrow(() -> new SubscriptionManagerProducerException(format("Failed to find subscription '%s' in registry", subscriptionName)));
    }

    private String subscriptionNameFrom(final Subscription subscription) {
        String jmsUri = subscription.getEventsource().getLocation().getJmsUri();
        return of(jmsUri.split(":")[2].split("[/.:]"))
                .map(StringUtils::capitalize)
                .collect(joining());
    }
}
