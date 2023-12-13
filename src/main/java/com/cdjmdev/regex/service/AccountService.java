package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.exception.AuthtokenExpiredException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.Tiers;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.AccountStatusController;

import java.util.Map;

public class AccountService {

	private DAOFactory factory;

	public AccountService(DAOFactory factory) {
		this.factory = factory;
	}

	public AccountStatusController.AccountStatusResult getUserStatus(AccountStatusController.AccountStatusRequest request) throws AuthtokenExpiredException {

		if(request.authtoken == null)
			throw new IllegalArgumentException("Authorization token is required.");

		Authtoken authtoken = factory.getAuthtokenDAO().getByID(request.authtoken);

		if(authtoken.isExpired())
			throw new AuthtokenExpiredException("User must log in again.");

		User user = factory.getUserDAO().getByID(authtoken);

		if(!Utilities.isSameDay(user.lastUse, Utilities.getCurrentTimestamp()))
			user.dailyUses = 0; //does not save just updates to actual daily uses

		AccountStatusController.AccountStatusResult response = new AccountStatusController.AccountStatusResult();

		response.message = "Ok";
		response.data = Map.of(
			"tier", user.tier,
			"daily_uses", String.valueOf(user.dailyUses),
			"max_uses", String.valueOf(user.tier.equalsIgnoreCase(Tiers.FREE) ? Tiers.MAX_USES_FREE : Tiers.MAX_USES_PAID),
			"email", user.email
		);

		return response;
	}
}
