package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterTest {
    ServerFacade serverFacade;

    @BeforeAll
    public void setup() {
        this.serverFacade = new ServerFacade();
    }

    @Test
    public void RegisterPass() {
        RegisterRequest request = new RegisterRequest("fn", "ln", "ua", "p", null);
        String urlPath = "/register";
        try {
            RegisterResponse response = serverFacade.register(request, urlPath);
            assert (response.isSuccess());
            assertNotNull(response.getUser());
        } catch (Exception e) { //register throws exception
            System.out.println("Exception: "+ e.getMessage());
        }
    }

    @Test
    public void RegisterFail() {
        RegisterRequest request = new RegisterRequest("fn", "ln", "ua", "p", null);
        String urlPath = "/notregister";
        try {
            RegisterResponse response = this.serverFacade.register(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception: "+ e.getMessage());
        }
    }
}
