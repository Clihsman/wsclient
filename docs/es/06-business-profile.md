# Perfil de negocio (Business Profile)

El Business Profile no tiene un ID propio: está directamente asociado al `phoneNumberId` configurado en el cliente.

## Leer el perfil

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

## Actualizar el perfil

Solo se envían los campos que establezcas en el builder; el resto se deja sin modificar en Meta:

```java
import com.wsclient.api.business.request.BusinessProfile;

BusinessProfile update = BusinessProfile.builder()
        .description("Atención al cliente 24/7")
        .email("contacto@miempresa.com")
        .websites(List.of("https://miempresa.com"))
        .vertical("RETAIL")
        .build();

client.updateBusinessProfile(update).join();
```

`updateBusinessProfile` lanza `IllegalArgumentException` de forma síncrona (no dentro del `CompletableFuture`) si `profile` es `null`.
