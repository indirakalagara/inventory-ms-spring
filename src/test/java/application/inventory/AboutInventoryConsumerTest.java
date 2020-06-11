package application.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;

public class AboutInventoryConsumerTest {
	
	@Rule
	public PactProviderRuleMk2 mockProvider
	  = new PactProviderRuleMk2("inventory_provider", "localhost", 8080, this);
    
    @Pact(consumer = "inventory_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        
        PactDslJsonBody bodyResponse = new PactDslJsonBody()
                .stringValue("name", "Inventory Service")
                .stringValue("parentRepo", "Storefront")
                .stringValue("description", "Stores all the inventory data");
        
        return builder
        	      .given("test GET")
        	        .uponReceiving("GET REQUEST")
        	        .path("/micro/about")
        	        .method("GET")
        	      .willRespondWith()
        	        .status(200)
        	        .headers(headers)
        	        .body(bodyResponse)
        	        .toPact();
    }
    
    @Test
    @PactVerification()
    public void givenGet_whenSendRequest_shouldReturn200WithProperHeaderAndBody() throws IOException{
      
    	// when
        ResponseEntity<String> response = new RestTemplate().getForEntity(mockProvider.getUrl() + "/micro/about", String.class);
//     
        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Inventory Service");
    }


}
