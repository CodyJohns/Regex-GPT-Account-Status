package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.AuthtokenDAO;
import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.PromptHistoryDAO;
import com.cdjmdev.oracle.dao.UserDAO;
import com.cdjmdev.oracle.exception.AuthtokenExpiredException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.PromptHistory;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.service.AccountService;
import com.fnproject.fn.testing.*;
import com.google.gson.Gson;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

public class AccountStatusControllerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    @Disabled
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
    @Disabled
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

    private DAOFactory dFactory;
    private AuthtokenDAO aDAO;
    private PromptHistoryDAO pDAO;
    private UserDAO uDAO;
    private User user;
    private Authtoken token;

    @BeforeEach
    void setup() {
        dFactory = mock(DAOFactory.class);
        aDAO = mock(AuthtokenDAO.class);
        uDAO = mock(UserDAO.class);
        pDAO = mock(PromptHistoryDAO.class);

        user = new User("", "", "", "");
        token = new Authtoken();
        token.expires = Utilities.getFutureTimestamp();
        token.user_id = user.id;

        PromptHistory prompt1 = new PromptHistory(user, "1", "1");
        PromptHistory prompt2 = new PromptHistory(user, "2", "2");
        PromptHistory prompt3 = new PromptHistory(user, "3", "3");

        Mockito.when(aDAO.getByID(Mockito.any())).thenReturn(token);
        Mockito.when(uDAO.getByID(Mockito.any())).thenReturn(user);
        Mockito.when(pDAO.getByUser(Mockito.any(), Mockito.anyInt())).thenReturn(List.of(prompt1, prompt2, prompt3));

        Mockito.when(dFactory.getAuthtokenDAO()).thenReturn(aDAO);
        Mockito.when(dFactory.getUserDAO()).thenReturn(uDAO);
        Mockito.when(dFactory.getPromptHistoryDAO()).thenReturn(pDAO);
    }

    @Test
    @DisplayName("Test get account info successful")
    public void testAccountInfo() {

        AccountService service = new AccountService(dFactory);

        AccountStatusController.AccountStatusRequest request = new AccountStatusController.AccountStatusRequest();
        request.authtoken = "1234567890";

        assertDoesNotThrow(() -> {
            AccountStatusController.AccountStatusResult result = service.getUserStatus(request);
            assertEquals(0, result.data.get("daily_uses"));
        });
    }

    @Test
    @DisplayName("Test get account info prompt history")
    public void testAccountInfoPromptHistory() {

        AccountService service = new AccountService(dFactory);

        AccountStatusController.AccountStatusRequest request = new AccountStatusController.AccountStatusRequest();
        request.authtoken = "1234567890";

        assertDoesNotThrow(() -> {
            AccountStatusController.AccountStatusResult result = service.getUserStatus(request);
            assertEquals(3, ((List<PromptHistory>) result.data.get("history")).size());
        });
    }

    @Test
    @DisplayName("Test get account with no given authtoken")
    public void testAccountInfoNoToken() {

        AccountService service = new AccountService(dFactory);

        AccountStatusController.AccountStatusRequest request = new AccountStatusController.AccountStatusRequest();
        request.authtoken = null;

        assertThrows(IllegalArgumentException.class, () -> {
            service.getUserStatus(request);
        });
    }

    @Test
    @DisplayName("Test get account with expired authtoken")
    public void testAccountInfoExpiredToken() {

        token.expires = 0;

        AccountService service = new AccountService(dFactory);

        AccountStatusController.AccountStatusRequest request = new AccountStatusController.AccountStatusRequest();
        request.authtoken = "1234567890";

        assertThrows(AuthtokenExpiredException.class, () -> {
            service.getUserStatus(request);
        });
    }
}