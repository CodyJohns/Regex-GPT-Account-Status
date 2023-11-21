package com.cdjmdev.regex;

import com.fnproject.fn.testing.*;
import com.google.gson.Gson;
import org.junit.*;

import static org.junit.Assert.*;

public class AccountStatusControllerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    public void shouldReturnResponse() {

        Gson gson = new Gson();

        AccountStatusController.AccountStatusRequest request = new AccountStatusController.AccountStatusRequest();

        testing.givenEvent().withBody(gson.toJson(request)).enqueue();
        testing.thenRun(AccountStatusController.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        System.out.println(result.getBodyAsString());
        assertNotNull(result.getBodyAsString());
    }

    @Test
    public void shouldReturn400() {
        Gson gson = new Gson();

        AccountStatusController.AccountStatusRequest request = new AccountStatusController.AccountStatusRequest();

        request.authtoken = "1234567890";

        testing.givenEvent().withBody(gson.toJson(request)).enqueue();
        testing.thenRun(AccountStatusController.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        System.out.println(result.getBodyAsString());
        assertNotNull(result.getBodyAsString());
    }

}