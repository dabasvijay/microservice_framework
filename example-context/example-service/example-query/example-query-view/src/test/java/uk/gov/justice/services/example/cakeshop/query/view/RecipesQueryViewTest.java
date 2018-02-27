package uk.gov.justice.services.example.cakeshop.query.view;


import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.QUERY_VIEW;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMatcher.isHandler;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.method;

import uk.gov.justice.services.common.converter.ObjectToJsonValueConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.example.cakeshop.query.view.request.SearchRecipes;
import uk.gov.justice.services.example.cakeshop.query.view.response.PhotoView;
import uk.gov.justice.services.example.cakeshop.query.view.response.RecipeView;
import uk.gov.justice.services.example.cakeshop.query.view.response.RecipesView;
import uk.gov.justice.services.example.cakeshop.query.view.service.RecipeService;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;
import uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory;

import java.util.Optional;
import java.util.UUID;

import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RecipesQueryViewTest {

    @Spy
    private Enveloper enveloper = new EnveloperFactory().create();

    @Mock
    ObjectToJsonValueConverter objectToJsonValueConverter = new ObjectToJsonValueConverter(new ObjectMapperProducer().objectMapper());

    @Mock
    private RecipeService service;

    @InjectMocks
    private RecipesQueryView queryView;

    @Before
    public void setUp() throws Exception {
        queryView = new RecipesQueryView(service, enveloper);
    }

    @Test
    public void shouldHaveCorrectHandlerMethod() throws Exception {
        assertThat(queryView, isHandler(QUERY_VIEW)
                .with(allOf(
                        method("findRecipe").thatHandles("example.get-recipe"),
                        method("listRecipes").thatHandles("example.search-recipes")))
        );
    }

    @Test
    public void shouldReturnRecipe() {

        final UUID recipeId = UUID.randomUUID();
        final String recipeName = "some recipe name";
        when(service.findRecipe(recipeId.toString())).thenReturn(new RecipeView(recipeId, recipeName, false));

        JsonObject jsonObject = createObjectBuilder().add("recipeId", recipeId.toString()).build();

        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);
        final Envelope<RecipeView> response = queryView.findRecipe(envelope);
        RecipeView payload = response.payload();
        assertThat(payload.getId(), is(recipeId));
        assertThat(payload.getName(), is(recipeName));

    }

  @Test
    public void shouldReturnResponseWithMetadataWhenQueryingForRecipe() {

      final UUID recipeId = UUID.randomUUID();
      final String recipeName = "some recipe name";
      when(service.findRecipe(recipeId.toString())).thenReturn(new RecipeView(recipeId, recipeName, false));

      JsonObject jsonObject = createObjectBuilder().add("recipeId", recipeId.toString()).build();
      Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);

      final Envelope<RecipeView> response = queryView.findRecipe(envelope);
      RecipeView payload = response.payload();

      assertThat(payload.getName(), is(response.payload().getName()));
      assertThat(payload.getId(), is(response.payload().getId()));
    }

    @Test
    public void shouldReturnRecipes() throws Exception {

        final UUID recipeId = UUID.randomUUID();
        final UUID recipeId2 = UUID.randomUUID();
        final String recipeName = "some recipe name";
        final String recipeName2 = "some other recipe name";

        final int pagesize = 5;
        when(service.getRecipes(pagesize, Optional.empty(), Optional.empty()))
                .thenReturn(new RecipesView(asList(new RecipeView(recipeId, recipeName, false), new RecipeView(recipeId2, recipeName2, false))));

        JsonObject jsonObject = createObjectBuilder().add("pagesize", pagesize).build();

        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);
        final Envelope<RecipesView> response = queryView.listRecipes(envelope);

        assertThat(response.payload().getRecipes().size(), is(2));
        assertThat(response.payload().getRecipes().get(0).getId(), is(recipeId));
        assertThat(response.payload().getRecipes().get(0).getName(), is(recipeName));
        assertThat(response.payload().getRecipes().get(1).getId(), is(recipeId2));
        assertThat(response.payload().getRecipes().get(1).getName(), is(recipeName2));

    }

    @Test
    public void shouldQueryForRecipesOfGivenName() throws Exception {

        final UUID recipeId = UUID.randomUUID();
        final String recipeName = "some recipe name";

        final String nameUsedInQuery = "some recipe";

        final int pagesize = 5;
        when(service.getRecipes(pagesize, Optional.of(nameUsedInQuery), Optional.empty()))
                .thenReturn(new RecipesView(singletonList(new RecipeView(recipeId, recipeName, false))));

        JsonObject jsonObject = createObjectBuilder()
                .add("pagesize", pagesize)
                .add("name", nameUsedInQuery)
                .build();

        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);
        final Envelope<RecipesView> response = queryView.listRecipes(envelope);

        assertThat(response.payload().getRecipes().get(0).getId(), is(recipeId));
        assertThat(response.payload().getRecipes().get(0).getName(), is(recipeName));
    }


    @Test
    public void shouldQueryForGlutenFreeRecipes() throws Exception {

        final UUID recipeId = UUID.randomUUID();
        final String recipeName = "some recipe name";

        final int pagesize = 5;
        final boolean glutenFree = true;

        when(service.getRecipes(pagesize, Optional.empty(), Optional.of(glutenFree))).thenReturn(
                new RecipesView(singletonList(new RecipeView(recipeId, recipeName, glutenFree))));


        JsonObject jsonObject = createObjectBuilder()
                .add("pagesize", pagesize)
                .add("glutenFree", glutenFree)
                .build();

        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);
        final Envelope<RecipesView> response = queryView.listRecipes(envelope);

        assertThat(response.payload().getRecipes().get(0).getId(), is(recipeId));
        assertThat(response.payload().getRecipes().get(0).getName(), is(recipeName));
        assertThat(response.payload().getRecipes().get(0).isGlutenFree(), is(glutenFree));

    }

    @Test
    public void shouldReturnResponseWithMetadataWhenQueryingForRecipes() {

        JsonObject jsonObject = createObjectBuilder().add("pagesize", 1).build();
        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);
        final Envelope<RecipesView> response = queryView.listRecipes(envelope);

        assertThat(response.metadata().name(), is("example.search-recipes"));

    }

    @Test
    public void shouldReturnRecipesForQuery() throws Exception {

        final UUID recipeId = UUID.randomUUID();
        final UUID recipeId2 = UUID.randomUUID();
        final String recipeName = "some recipe name";
        final String recipeName2 = "some other recipe name";

        final int pagesize = 5;
        when(service.getRecipes(pagesize, Optional.of(recipeName), Optional.of(false)))
                .thenReturn(new RecipesView(asList(new RecipeView(recipeId, recipeName, false), new RecipeView(recipeId2, recipeName2, false))));

        SearchRecipes searchRecipes = new SearchRecipes(pagesize);
        searchRecipes.setName(recipeName);
        searchRecipes.setGlutenFree(false);

        Envelope<SearchRecipes> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), searchRecipes);

        final Envelope<RecipesView> response = queryView.queryRecipes(envelope);
        RecipesView payload = response.payload();
        assertThat(payload.getRecipes().get(0).getName(), is(searchRecipes.getName()));

    }

    @Test
    public void shouldReturnFileId() {

        final UUID recipeId = UUID.randomUUID();
        final UUID fileId = UUID.randomUUID();

        when(service.findRecipePhoto(recipeId.toString())).thenReturn(new PhotoView(fileId));

        JsonObject jsonObject = createObjectBuilder().add("recipeId", recipeId.toString()).build();

        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);

        final Envelope<PhotoView> response = queryView.findRecipePhoto(envelope);

        assertThat(response.metadata().name(), is("example.get-recipe-photograph"));
        assertThat(response.payload().getFileId(), is(fileId));

    }

    @Test
    public void shouldReturnJsonValueNullIfNullPhotoId() {

        final UUID recipeId = UUID.randomUUID();

        when(service.findRecipePhoto(recipeId.toString())).thenReturn(null);

        JsonObject jsonObject = createObjectBuilder().add("recipeId", recipeId.toString()).build();

        Envelope<JsonObject> envelope = new LocalDefaultNewEnvelope<>(createMetadata(), jsonObject);

        final Envelope<PhotoView> response = queryView.findRecipePhoto(envelope);
        assertThat(response.metadata().name(), is("example.get-recipe-photograph"));
        assertThat(response.payload(), is(nullValue()));

    }

    private Metadata createMetadata() {
        return JsonEnvelope.metadataBuilder()
                .withName("Name")
                .withId(UUID.randomUUID())
                .withClientCorrelationId("asdsfd")
                .build();
    }

    class LocalDefaultNewEnvelope<T> implements Envelope<T> {

        private final Metadata metadata;

        private final T payload;

        LocalDefaultNewEnvelope(final Metadata metadata, final T payload) {
            this.metadata = metadata;
            this.payload = payload;
        }

        @Override
        public Metadata metadata() {
            return metadata;
        }

        @Override
        public T payload() {
            return payload;
        }

    }

}
