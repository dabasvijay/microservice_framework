package uk.gov.justice.raml.jms.config;

import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorProperties;
import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;

public class GeneratorPropertiesFactory {

    private static final String CUSTOM_MDB_POOL = "customMDBPool";

    public static GeneratorPropertiesFactory generatorProperties() {
        return new GeneratorPropertiesFactory();
    }

    public GeneratorProperties withCustomMDBPool() {
        final CommonGeneratorProperties properties = new CommonGeneratorProperties();
        try {
            setField(properties, CUSTOM_MDB_POOL, "TRUE");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
