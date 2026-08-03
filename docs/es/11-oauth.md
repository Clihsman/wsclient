# OAuth (Facebook Login / embedded signup)

`FacebookAuthService` (`com.wsclient.api.services.FacebookAuthService`) cubre los tres flujos de tokens de la API de Meta que necesitas para el onboarding de clientes vía Embedded Signup u otro flujo de Facebook Login:

```java
import com.wsclient.api.services.FacebookAuthService;
import com.wsclient.api.services.FacebookAuthServiceImpl;

FacebookAuthService authService = new FacebookAuthServiceImpl();
// Opcional: solo necesario si apuntas a un endpoint distinto al de producción.
// authService.configure("https://graph.facebook.com/oauth/access_token");
```

## 1. App Access Token

Token de la app (no de un usuario), usado para llamadas server-to-server que no requieren autorización de un usuario específico:

```java
FBAccessToken appToken = authService.getAppAccessToken("APP_ID", "APP_SECRET").join();
```

## 2. Intercambiar código de autorización por token de usuario

Después de que un usuario completa el flujo de Facebook Login / Embedded Signup, Meta redirige a tu `redirect_uri` con un parámetro `code`. Intercámbialo por un token de usuario:

```java
FBAccessToken userToken = authService.exchangeCodeForUserToken(
        "APP_ID",
        "APP_SECRET",
        "https://tu-dominio.com/callback", // debe coincidir exactamente con el redirect_uri usado al iniciar el flujo
        "CODE_RECIBIDO_EN_EL_CALLBACK"
).join();
```

## 3. Token de larga duración

Los tokens de usuario de corta duración expiran en un par de horas. Intercámbialos por uno de larga duración (~60 días):

```java
FBAccessToken longLivedToken = authService.getLongLivedToken(
        "APP_ID",
        "APP_SECRET",
        userToken.accessToken()
).join();

System.out.println(longLivedToken.accessToken());
System.out.println(longLivedToken.expiresIn()); // segundos hasta que expire, p. ej. 5184000 (~60 días)
```

`FBAccessToken.expiresIn()` es `null` en la respuesta de `getAppAccessToken`/`exchangeCodeForUserToken` (Meta no lo incluye ahí) y solo viene poblado en la respuesta de `getLongLivedToken`.

## Manejo de errores

Los tres métodos comparten la misma lógica interna (`requestToken(...)`) y reportan errores de la misma forma que el resto de la librería: `CompletionException` envolviendo un `WhatsAppException` con `type`/`code`/`errorSubcode`/`fbtraceId` — ver [Manejo de errores](08-error-handling.md).
