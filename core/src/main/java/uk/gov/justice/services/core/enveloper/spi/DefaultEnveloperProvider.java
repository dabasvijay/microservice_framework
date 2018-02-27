package uk.gov.justice.services.core.enveloper.spi;

import uk.gov.justice.services.core.enveloper.DefaultEnveloper;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.function.Function;

public class DefaultEnveloperProvider implements EnveloperProvider {

    @Override
    public <T> Enveloper.EnveloperBuilder<T> envelop(final T payload) {
        return new DefaultEnveloper().envelop(payload);
    }

    @Override
    public Function<Object, JsonEnvelope> toEnvelopeWithMetadataFrom(final Envelope<?> envelope) {
        return new DefaultEnveloper().toEnvelopeWithMetadataFrom(envelope);
    }

}
