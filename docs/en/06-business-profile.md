# Business profile

The Business Profile does not have its own ID: it is directly tied to the `phoneNumberId` configured on the client.

## Reading the profile

```java
import com.wsclient.api.business.response.BusinessProfileResponse;

BusinessProfileResponse profile = client.getBusinessProfile().join();

System.out.println(profile.about());
System.out.println(profile.description());
System.out.println(profile.email());
System.out.println(profile.websites());
System.out.println(profile.profilePictureUrl());
System.out.println(profile.vertical());
```

## Updating the profile

Only the fields you set on the builder are sent; everything else is left unchanged on Meta's side:

```java
import com.wsclient.api.business.request.BusinessProfile;

BusinessProfile update = BusinessProfile.builder()
        .description("24/7 customer support")
        .email("contact@mycompany.com")
        .websites(List.of("https://mycompany.com"))
        .vertical("RETAIL")
        .build();

client.updateBusinessProfile(update).join();
```

`updateBusinessProfile` throws `IllegalArgumentException` synchronously (not inside the `CompletableFuture`) if `profile` is `null`.
