package uk.gov.justice.services.core;

import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.core.interceptor.spi.InterceptorContextProvider;
import uk.gov.justice.services.messaging.JsonEnvelope;

public class InterceptorContextHelper {

    public InterceptorContext interceptorContextFrom(final JsonEnvelope envelope) {
        return InterceptorContextProvider.provider().interceptorContextWithInput(envelope);
    }

}
