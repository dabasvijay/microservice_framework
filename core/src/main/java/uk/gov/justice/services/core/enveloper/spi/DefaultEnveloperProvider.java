package uk.gov.justice.services.core.enveloper.spi;

import static javax.enterprise.inject.spi.CDI.current;

import uk.gov.justice.services.common.converter.ObjectToJsonValueConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.core.enveloper.DefaultEnveloper;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.function.Function;

public class DefaultEnveloperProvider implements EnveloperProvider {

    private DefaultEnveloper defaultEnveloper;

    @Override
    public <T> Enveloper.EnveloperBuilder<T> envelop(final T payload) {
        return getDefaultEnveloper().envelop(payload);
    }

    @Override
    public Function<Object, JsonEnvelope> toEnvelopeWithMetadataFrom(final Envelope<?> envelope) {
        return getDefaultEnveloper().toEnvelopeWithMetadataFrom(envelope);
    }

    private DefaultEnveloper getDefaultEnveloper() {

        if (defaultEnveloper == null) {
            if (isCdiAvailable()) {
                defaultEnveloper = current().select(DefaultEnveloper.class).get();
            } else {
                defaultEnveloper = new DefaultEnveloper(new UtcClock(), new ObjectToJsonValueConverter(new ObjectMapperProducer().objectMapper()));
            }
        }

        return defaultEnveloper;
    }

    private boolean isCdiAvailable() {

        try {
            current();
        } catch (IllegalStateException e) {
            return false;
        }

        return true;
    }
}
