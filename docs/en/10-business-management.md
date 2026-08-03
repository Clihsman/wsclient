# Business account management (phone numbers and QR codes)

`WhatsAppBusinessManagementService` (`com.wsclient.api.services.WhatsAppBusinessManagementService`) manages a WhatsApp Business Account's (WABA) phone numbers and message QR codes (shortlinks). It's configured independently, injecting the same `WhatsAppService` you use elsewhere in your app:

```java
import com.wsclient.api.services.WhatsAppBusinessManagementService;
import com.wsclient.api.services.WhatsAppBusinessManagementServiceImpl;

WhatsAppBusinessManagementService managementService = new WhatsAppBusinessManagementServiceImpl(service);
managementService.configure(
        "https://graph.facebook.com/v20.0",
        "WABA_ID",
        "ACCESS_TOKEN");
```

## Phone numbers

```java
import com.wsclient.api.messages.response.PhoneNumbersListResponse;
import com.wsclient.api.messages.response.PhoneNumberResponse;

// List every number on the WABA
PhoneNumbersListResponse numbers = managementService.listPhoneNumbers().join();
numbers.data().forEach(n -> System.out.println(n.displayPhoneNumber() + " - " + n.qualityRating()));

// Get one number's details
PhoneNumberResponse number = managementService.getPhoneNumber("PHONE_NUMBER_ID").join();
```

### Registration and verification

Typical flow when adding a new number to the WhatsApp Cloud API:

```java
// 1. Request a verification code (SMS or voice call)
managementService.requestVerificationCode("PHONE_NUMBER_ID", "SMS", "en_US").join();

// 2. Verify the code received on the phone number
managementService.verifyCode("PHONE_NUMBER_ID", "123456").join();

// 3. Register the number for use with the Cloud API (two-step verification PIN)
managementService.registerPhoneNumber("PHONE_NUMBER_ID", "123456").join();
```

All three return `SuccessResponse(boolean success)`.

## QR codes (message shortlinks)

```java
import com.wsclient.api.messages.response.QrCodeResponse;
import com.wsclient.api.messages.response.QrCodesListResponse;

// Create a code with a pre-filled message
QrCodeResponse qr = managementService.createQrCode("PHONE_NUMBER_ID", "Hi, I'd like more info").join();
System.out.println(qr.deepLinkUrl());

// List existing codes
QrCodesListResponse qrCodes = managementService.listQrCodes("PHONE_NUMBER_ID").join();

// Get a specific one
QrCodeResponse existing = managementService.getQrCode("PHONE_NUMBER_ID", qr.code()).join();

// Update its pre-filled message
managementService.updateQrCode("PHONE_NUMBER_ID", qr.code(), "New message").join();

// Delete it
managementService.deleteQrCode("PHONE_NUMBER_ID", qr.code()).join();
```

## Validation

Every method that takes a `phoneNumberId`/`qrCodeId` throws `IllegalArgumentException` (wrapped in `CompletionException`) if the ID is null or blank. There's no further business validation beyond that — errors from the Meta API (number already registered, wrong code, etc.) arrive as `WhatsAppException`.
