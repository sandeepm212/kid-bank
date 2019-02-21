package com.learnwithted.kidbank.adapter.sms;

import com.learnwithted.kidbank.adapter.web.FakeTransactionRepository;
import com.learnwithted.kidbank.domain.Account;
import com.learnwithted.kidbank.domain.PhoneNumberAuthorizer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SmsBalanceRequestTest {

  private static final String FROM_UNKNOWN_NUMBER = "+12125551212";
  private static final String FROM_KNOWN_NUMBER = "+14155551212";
  private static final PhoneNumberAuthorizer PHONE_NUMBER_AUTHORIZER
      = new PhoneNumberAuthorizer(FROM_KNOWN_NUMBER);

  @Test
  public void fromUnknownNumberShouldReturnEmptyResponse() throws Exception {
    Account account = new Account(new FakeTransactionRepository());
    SmsController smsController = new SmsController(account, PHONE_NUMBER_AUTHORIZER);

    TwilioIncomingRequest twilioIncomingRequest = new TwilioIncomingRequest();
    twilioIncomingRequest.setBody("balance");
    twilioIncomingRequest.setFrom(FROM_UNKNOWN_NUMBER);

    String response = smsController.incomingSms(twilioIncomingRequest);

    assertThat(response)
        .isEmpty();
  }

  @Test
  public void fromKnownAuthorizedNumberShouldReturnBalanceMessage() throws Exception {
    Account account = new Account(new FakeTransactionRepository());
    SmsController smsController = new SmsController(account, PHONE_NUMBER_AUTHORIZER);

    TwilioIncomingRequest twilioIncomingRequest = new TwilioIncomingRequest();
    twilioIncomingRequest.setBody("balance");
    twilioIncomingRequest.setFrom(FROM_KNOWN_NUMBER);

    String response = smsController.incomingSms(twilioIncomingRequest);

    assertThat(response)
        .contains("Your balance is $0.00");
  }

  @Test
  public void fromKnownNumberWithUnknownMessageShouldReturnErrorMessage() throws Exception {
    Account account = new Account(new FakeTransactionRepository());
    SmsController smsController = new SmsController(account, PHONE_NUMBER_AUTHORIZER);

    TwilioIncomingRequest twilioIncomingRequest = new TwilioIncomingRequest();
    twilioIncomingRequest.setBody("wrong message");
    twilioIncomingRequest.setFrom(FROM_KNOWN_NUMBER);

    String response = smsController.incomingSms(twilioIncomingRequest);

    assertThat(response)
        .contains("Did not understand \"wrong message\", use BALANCE to check your account balance.");
  }
}