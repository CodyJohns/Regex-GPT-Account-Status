package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.exception.AuthtokenExpiredException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.regex.AccountStatusController;

import java.util.Map;

public class AccountService {

	private DAOFactory factory;

	public AccountService(DAOFactory factory) {
		this.factory = factory;
	}

	public AccountStatusController.AccountStatusResult getUserStatus(AccountStatusController.AccountStatusRequest request) throws AuthtokenExpiredException {

		if(request.authtoken == null)
			throw new RuntimeException("Authorization token is required.");

		Authtoken authtoken = factory.getAuthtokenDAO().getByID(request.authtoken);

		if(authtoken.isExpired())
			throw new AuthtokenExpiredException("User must log in again.");

		User user = factory.getUserDAO().getByID(authtoken);

		AccountStatusController.AccountStatusResult response = new AccountStatusController.AccountStatusResult();

		response.message = "Ok";
		response.data = Map.of(
			"tier", user.tier,
			"daily_uses", String.valueOf(user.dailyUses),
			"email", user.email
		);

		return response;
	}
}
