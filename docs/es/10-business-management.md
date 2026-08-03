# Gestión de cuenta de negocio (números y QR codes)

`WhatsAppBusinessManagementService` (`com.wsclient.api.services.WhatsAppBusinessManagementService`) administra los números de teléfono de la WhatsApp Business Account (WABA) y los códigos QR de mensajes (message shortlinks). Se configura de forma independiente, inyectando el mismo `WhatsAppService` que uses en el resto de la app:

```java
import com.wsclient.api.services.WhatsAppBusinessManagementService;
import com.wsclient.api.services.WhatsAppBusinessManagementServiceImpl;

WhatsAppBusinessManagementService managementService = new WhatsAppBusinessManagementServiceImpl(service);
managementService.configure(
        "https://graph.facebook.com/v20.0",
        "WABA_ID",
        "ACCESS_TOKEN");
```

## Números de teléfono

```java
import com.wsclient.api.messages.response.PhoneNumbersListResponse;
import com.wsclient.api.messages.response.PhoneNumberResponse;

// Listar todos los números de la WABA
PhoneNumbersListResponse numbers = managementService.listPhoneNumbers().join();
numbers.data().forEach(n -> System.out.println(n.displayPhoneNumber() + " - " + n.qualityRating()));

// Obtener el detalle de uno
PhoneNumberResponse number = managementService.getPhoneNumber("PHONE_NUMBER_ID").join();
```

### Registro y verificación

Flujo típico al agregar un número nuevo a la WhatsApp Cloud API:

```java
// 1. Solicitar un código de verificación (SMS o llamada)
managementService.requestVerificationCode("PHONE_NUMBER_ID", "SMS", "es_CO").join();

// 2. Verificar el código recibido en el número
managementService.verifyCode("PHONE_NUMBER_ID", "123456").join();

// 3. Registrar el número para uso con la Cloud API (PIN de verificación en dos pasos)
managementService.registerPhoneNumber("PHONE_NUMBER_ID", "123456").join();
```

Los tres retornan `SuccessResponse(boolean success)`.

## Códigos QR (message shortlinks)

```java
import com.wsclient.api.messages.response.QrCodeResponse;
import com.wsclient.api.messages.response.QrCodesListResponse;

// Crear un código con un mensaje pre-llenado
QrCodeResponse qr = managementService.createQrCode("PHONE_NUMBER_ID", "Hola, quiero más información").join();
System.out.println(qr.deepLinkUrl());

// Listar los códigos existentes
QrCodesListResponse qrCodes = managementService.listQrCodes("PHONE_NUMBER_ID").join();

// Obtener uno específico
QrCodeResponse existing = managementService.getQrCode("PHONE_NUMBER_ID", qr.code()).join();

// Actualizar el mensaje pre-llenado
managementService.updateQrCode("PHONE_NUMBER_ID", qr.code(), "Nuevo mensaje").join();

// Eliminarlo
managementService.deleteQrCode("PHONE_NUMBER_ID", qr.code()).join();
```

## Validación

Todos los métodos que reciben un `phoneNumberId`/`qrCodeId` lanzan `IllegalArgumentException` (envuelta en `CompletionException`) si el ID es nulo o vacío. No hay más validación de negocio de por medio — los errores de la API de Meta (número ya registrado, código incorrecto, etc.) llegan como `WhatsAppException`.
