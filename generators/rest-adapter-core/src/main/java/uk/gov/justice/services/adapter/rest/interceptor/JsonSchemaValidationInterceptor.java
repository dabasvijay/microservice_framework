package uk.gov.justice.services.adapter.rest.interceptor;

import static java.lang.String.format;
import static java.util.Optional.of;
import static uk.gov.justice.services.adapter.rest.envelope.MediaTypes.JSON_MEDIA_TYPE_SUFFIX;
import static uk.gov.justice.services.adapter.rest.envelope.MediaTypes.charsetFrom;

import uk.gov.justice.services.adapter.rest.exception.BadRequestException;
import uk.gov.justice.services.core.json.JsonSchemaValidationException;
import uk.gov.justice.services.core.json.JsonSchemaValidator;
import uk.gov.justice.services.core.json.JsonValidationLoggerHelper;
import uk.gov.justice.services.core.mapping.MediaType;
import uk.gov.justice.services.core.mapping.NameToMediaTypeConverter;
import uk.gov.justice.services.messaging.exception.InvalidMediaTypeException;
import uk.gov.justice.services.messaging.logging.HttpTraceLoggerHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 * Intercepts incoming REST requests and if they are POSTs, check that the JSON payload is valid
 * against the relevant JSON schema.
 */
@Provider
public class JsonSchemaValidationInterceptor implements ReaderInterceptor {

    @Inject
    Logger logger;

    @Inject
    JsonSchemaValidator restJsonSchemaValidator;

    @Inject
    NameToMediaTypeConverter nameToMediaTypeConverter;

    @Inject
    JsonValidationLoggerHelper jsonValidationLoggerHelper;

    @Inject
    HttpTraceLoggerHelper httpTraceLoggerHelper;

    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {

        final MediaType mediaType = new MediaType(context.getMediaType().toString());

        if (mediaType.getSubtype().endsWith(JSON_MEDIA_TYPE_SUFFIX)) {
            final String charset = charsetFrom(context.getMediaType());
            final String payload = IOUtils.toString(context.getInputStream(), charset);

            try {
                restJsonSchemaValidator.validate(payload, nameToMediaTypeConverter.convert(mediaType), of(mediaType));
            } catch (final JsonSchemaValidationException jsonSchemaValidationException) {
                final String message = format("JSON schema validation has failed on %s due to %s ",
                        httpTraceLoggerHelper.toHttpHeaderTrace(context.getHeaders()),
                        jsonValidationLoggerHelper.toValidationTrace(jsonSchemaValidationException));
                logger.debug(message);
                throw new BadRequestException(message, jsonSchemaValidationException);

            } catch (InvalidMediaTypeException ex) {
                throw new BadRequestException(ex.getMessage(), ex);
            }

            context.setInputStream(new ByteArrayInputStream(payload.getBytes(charset)));
        }

        return context.proceed();
    }
}
