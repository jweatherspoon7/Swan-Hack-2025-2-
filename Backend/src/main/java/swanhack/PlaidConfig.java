package swanhack;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.plaid.client.ApiClient;
//import com.plaid.client.PlaidApi;
//import com.plaid.client.request.PlaidEnvironment;
//
//@Configuration
//public class PlaidConfig {
//
//    @Value("${plaid.client-id}")
//    private String clientId;
//
//    @Value("${plaid.secret}")
//    private String secret;
//
//    @Bean
//    public PlaidApi plaidApi() {
//        // Create ApiClient with the PlaidEnvironment enum
//        ApiClient client = new ApiClient(PlaidEnvironment.SANDBOX);
//
//        // Set your credentials
//        client.setPlaidClientId(clientId);
//        client.setPlaidSecret(secret);
//
//        // Create the PlaidApi service
//        return client.createService(PlaidApi.class);
//    }
//}
