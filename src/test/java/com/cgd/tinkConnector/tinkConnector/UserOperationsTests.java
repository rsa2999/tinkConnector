package com.cgd.tinkConnector.tinkConnector;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.net.URISyntaxException;


@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserOperationsTests {

    @Autowired
    private TestRestTemplate restTemplate;



    private String BASE_URL = "http://localhost:8081/api/users/";

    @Test
    public void testeCreateUser() throws URISyntaxException
    {
        /*

        final String baseUrl = BASE_URL+"create/";
        URI uri = new URI(baseUrl);

        String authorizationHeader = "Basic " + DatatypeConverter.printBase64Binary(("admin" + ":" + "admin").getBytes());

        BasicAuthUser newUser = new BasicAuthUser("rsa","pass",true);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<BasicAuthUser> request = new HttpEntity<>(newUser, headers);

        ResponseEntity<BasicResponse> result = this.restTemplate.postForEntity(uri, request, BasicResponse.class);

        //Verify request succeed
     //   Assert.assertEquals(201, result.getStatusCodeValue());

     */

    }




}
