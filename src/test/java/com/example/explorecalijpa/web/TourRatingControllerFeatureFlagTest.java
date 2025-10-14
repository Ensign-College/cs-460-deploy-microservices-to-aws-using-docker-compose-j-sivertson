package com.example.explorecalijpa.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.explorecalijpa.business.TourRatingService;

/**
 * Tests that verify the tour rating API can be disabled via feature flags.
 * Tests all 7 rating endpoints to ensure they return 404 when feature flag is disabled.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "features.tour-ratings=false")
public class TourRatingControllerFeatureFlagTest {

  private static final int TOUR_ID = 999;
  private static final int CUSTOMER_ID = 123;
  private static final String TOUR_RATINGS_URL = "/tours/" + TOUR_ID + "/ratings";
  private static final String TOUR_RATINGS_AVERAGE_URL = "/tours/" + TOUR_ID + "/ratings/average";
  private static final String TOUR_RATINGS_DELETE_URL = "/tours/" + TOUR_ID + "/ratings/" + CUSTOMER_ID;
  private static final String TOUR_RATINGS_BATCH_URL = "/tours/" + TOUR_ID + "/ratings/batch?score=5";

  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private TourRatingService serviceMock;

  private HttpEntity<String> createAuthenticatedEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("user", "password");
    return new HttpEntity<>(headers);
  }

  private HttpEntity<String> createAdminAuthenticatedEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("admin", "admin123");
    return new HttpEntity<>(headers);
  }

  private HttpEntity<String> createAuthenticatedEntityWithBody(String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("user", "password");
    headers.set("Content-Type", "application/json");
    return new HttpEntity<>(body, headers);
  }

  private HttpEntity<String> createAdminAuthenticatedEntityWithBody(String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("admin", "admin123");
    headers.set("Content-Type", "application/json");
    return new HttpEntity<>(body, headers);
  }

  @Test
  void getAllRatingsDisabledReturnsNotFound() {
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_URL, HttpMethod.GET, 
        createAuthenticatedEntity(), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void getAverageRatingDisabledReturnsNotFound() {
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_AVERAGE_URL, HttpMethod.GET, 
        createAuthenticatedEntity(), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void createRatingDisabledReturnsNotFound() {
    String ratingJson = "{\"score\":5,\"comment\":\"Great tour!\",\"customerId\":" + CUSTOMER_ID + "}";
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_URL, HttpMethod.POST, 
        createAdminAuthenticatedEntityWithBody(ratingJson), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void updateRatingWithPutDisabledReturnsNotFound() {
    String ratingJson = "{\"score\":4,\"comment\":\"Updated comment\",\"customerId\":" + CUSTOMER_ID + "}";
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_URL, HttpMethod.PUT, 
        createAdminAuthenticatedEntityWithBody(ratingJson), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void updateRatingWithPatchDisabledReturnsNotFound() {
    String ratingJson = "{\"score\":3,\"customerId\":" + CUSTOMER_ID + "}";
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_URL, HttpMethod.PATCH, 
        createAdminAuthenticatedEntityWithBody(ratingJson), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void deleteRatingDisabledReturnsNotFound() {
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_DELETE_URL, HttpMethod.DELETE, 
        createAdminAuthenticatedEntity(), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void batchCreateRatingsDisabledReturnsNotFound() {
    String customersJson = "[456, 789, 101]";
    ResponseEntity<String> res = restTemplate.exchange(TOUR_RATINGS_BATCH_URL, HttpMethod.POST, 
        createAdminAuthenticatedEntityWithBody(customersJson), String.class);
    assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }
}
