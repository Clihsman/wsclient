# OAuth (Facebook Login / embedded signup)

`FacebookAuthService` (`com.wsclient.api.services.FacebookAuthService`) covers the three token flows from the Meta API you need for onboarding customers via Embedded Signup or another Facebook Login flow:

```java
import com.wsclient.api.services.FacebookAuthService;
import com.wsclient.api.services.FacebookAuthServiceImpl;

FacebookAuthService authService = new FacebookAuthServiceImpl();
// Optional: only needed if you're pointing at an endpoint other than production.
// authService.configure("https://graph.facebook.com/oauth/access_token");
```

## 1. App Access Token

The app's own token (not a user's), used for server-to-server calls that don't require a specific user's authorization:

```java
FBAccessToken appToken = authService.getAppAccessToken("APP_ID", "APP_SECRET").join();
```

## 2. Exchange an authorization code for a user token

After a user completes the Facebook Login / Embedded Signup flow, Meta redirects to your `redirect_uri` with a `code` parameter. Exchange it for a user access token:

```java
FBAccessToken userToken = authService.exchangeCodeForUserToken(
        "APP_ID",
        "APP_SECRET",
        "https://your-domain.com/callback", // must exactly match the redirect_uri used to start the flow
        "CODE_RECEIVED_ON_THE_CALLBACK"
).join();
```

## 3. Long-lived token

Short-lived user tokens expire in a couple of hours. Exchange them for a long-lived one (~60 days):

```java
FBAccessToken longLivedToken = authService.getLongLivedToken(
        "APP_ID",
        "APP_SECRET",
        userToken.accessToken()
).join();

System.out.println(longLivedToken.accessToken());
System.out.println(longLivedToken.expiresIn()); // seconds until expiry, e.g. 5184000 (~60 days)
```

`FBAccessToken.expiresIn()` is `null` in the response from `getAppAccessToken`/`exchangeCodeForUserToken` (Meta doesn't include it there) and is only populated in the `getLongLivedToken` response.

## Error handling

All three methods share the same internal logic (`requestToken(...)`) and report errors the same way as the rest of the library: `CompletionException` wrapping a `WhatsAppException` with `type`/`code`/`errorSubcode`/`fbtraceId` — see [Error handling](08-error-handling.md).
