package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.OracleDAOFactory;
import com.cdjmdev.regex.service.AccountService;
import com.cdjmdev.oracle.exception.AuthtokenExpiredException;
import com.fnproject.fn.api.Headers;

import java.util.Map;

public class AccountStatusController {


    private DAOFactory factory;
    private AccountService service;

    public AccountStatusController() {
        factory = new OracleDAOFactory();
        service = new AccountService(factory);
    }

    public static class AccountStatusRequest {
        public String authtoken;
    }

    public static class AccountStatusResult {
        public Map<String, String> data;
        public String message;
        public int status = 200;
    }

    public AccountStatusResult handleRequest(AccountStatusRequest request) {

        try {
            return service.getUserStatus(request);
        } catch (NullPointerException e) {
            AccountStatusResult response = new AccountStatusResult();
            response.status = 404;
            response.message = e.getMessage();
            return response;
        } catch(AuthtokenExpiredException e) {
            AccountStatusResult response = new AccountStatusResult();
            response.status = 404;
            response.message = e.getMessage();
            return response;
        } catch (RuntimeException e) {
            AccountStatusResult response = new AccountStatusResult();
            response.status = 400;
            response.message = e.getMessage();
            return response;
        } catch (Exception e) {
            AccountStatusResult response = new AccountStatusResult();
            response.status = 500;
            response.message = e.getMessage();
            return response;
        }
    }

}