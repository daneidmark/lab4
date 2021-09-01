# Lab4

Om du inte har gåt igenom spring security basics finns det en labb här github.com/daneidmark/lab4-user-service

Det jag vill är att du går igenom user-servicen och löser de uppgifterna sen får du välja
1. Kolla igenom denna kod och se vad den gör 
2. Testa att implementera detta i din app (knepigt)

Dvs du behöver inte göra alla steg nedan.

## Del 2_A: Securing your API with token based authentication 
Om du vill kan du få implementera spring security i din app. Det är ganska mycket och ganska knepigt. Om du vill göra ett försök kan du hitta lite mer info nedan.
Om du inte vill kan du läsa igenom koden i detta projekt. Det har satt upp ett komplett projekt med spring security.

1. Provkör appen
   1. Du kan logga in som ADMIN genom att tillhanda hålla `admin` och `password` som credentials
   2. Du kan nu skapa en CUSTOMER genom att ge den ett användarnamn och ett lösenord (felhanteringen här är dålig i UIt)
   3. Du kan nu logga in med din CUSTOMER om den passerade risk checken.
   4. CUSTOMER kan nu sätta in och ta ut pengar från sitt konto.

2. Kolla igenom koden och jämför den med det som fanns i user service.

### Spring security
Spring security är en en bra modul som hjälper dig att säkra din app! I denna delen av denna lab ska vi fokusera på authentication och authorization. 

Authenticaiton tar hand om problemet att lista ut att det är du som är du och det kan vi göra på en del olika sätt. I denna lab kommer vi titta på Username och Password samt JWT (tokens)
Authorization tar hand om problemet att lista ut om du har rättigheter att utföra operationen. Tex en admin kanske får göra andra saker än en normal användare.

Till er hjälp har ett annat team skapat en user service som tar hand om användare.

När den startas så läggs det alltid till en admin-användare (username - admin, password - password, role - ADMIN) denna användaren kan ni använda er av i eran applikation med.

Om en användare är en ADMIN -> visa Open account vyn där admin kan öppna ett konto åt någon annan genom att tillhandahålla ett användarnamn och ett lösenord.
Om en användare är en CUSTOMER -> visa Manage account vyn där customern kan sätta in och ta ut pengar.

En admin kan skapa konton åt kunder.
En kund kan enbart sätta in och ta ut pengar på sina egna konton.

I denna tutorial finns alla byggstenar som ni kan tänka er behöva! Börja med att läsa igenom den sen kan ni ha den till hjälp när ni för uppfifterna i denna labb

https://www.toptal.com/spring/spring-security-tutorial

#### Steg 1: Prata REST med backend
För att visa användningen av tokens är det att föredra att vi använder REST för att prata med vår backend istället för att gå all out thymeleaf.

Har du skapat en egen lösning som detta är det bara att fortsätta. Om inte, skapa en egen eller så kan du sno allt i resources/public i detta repo för att få en frontend.

Det färdiga GUIT förväntar sig följande endpoints 

1. /admin/open
2. /account/deposit
3. /account/withdraw

Kolla gärna runt i detta repo för att lista ut hur DTOer och sådant ser ut.

#### Steg 1.b Migrera användarhanteringen till user-service
Numera tas användarna hand om i user-service. Dvs migrera till att använda dess endpoints för att skapa användare.

Kolla swagger för att se hur APIt ser ut. 

#### Steg 2: Enable spring security i din app!

Efter slutfört moment ska du ha 
1. alla html-sidor ska vara publikt accessbara.
2. alla Rest-anrop ska kräva authentisering.

Skapa en SpringSecurityConfiguration och se till att du låser ned din app. Därefter öppnar du upp dina publika sidor.
Tex. borde det gå att komma åt login sidan utan att vara inloggad. Om du hostar din front-end separat kan det vara så att
allt bara funkar ändå då /login default är öppen och du har inga andra resurser som behöver öppnas.

#### Steg 3: Logga in din användare
Från GUIt eller från din app, ropa på user-service /login, där får du en token tillbaka som du kan spara och använda 
i alla requests till din app. Tokenen innehåller allt som behövs för att styrka att du är du och har dina
roller så att din app kan fokusera på authorization.


#### Steg 4 : Skapa ett AuthorizationFilter
Efter detta steg kan du i dina rest endpoints skriva `public ResponseEntity<AccountDto> getAccount(@AuthenticationPrincipal User activeUser)` för att läsa ut information om den inloggade användaren.

Vi kommer att skapa ett AuthorizationFilter som kommer authenticate din användare. 

Här kan du kolla i user-service hur dessa kan se ut och där finns det även lite hjälp om man vill
ta inspiration om hur man parsar och validerar JWT-tokens.

Kolla på JWTAuthorizationFilter och JWTIssuer.


## Del 3: TLS (om du får tid över)
Vi vill gärna slå på HTTPS i vår app. Och då behöver vi skapa certifikat. Vi kommer skapa self-signed certificates i denna lab. Men i verkligheten använder vi saker som Lets encrypt för att skapa dem.
Det finns en ny version av Risktjänsten daneidmark/risk:0.0.2 i den har teamet implementerat mTLS. Dvs för att du ska kunna ropa på dess endpoint måste di tillhandahålla ett certifikat och ett lösenord.

Om du vill testa detta måster du.
   
1. Skapa certifikat. Här finns det ett exempel på hur du gör det. Googla gärna och läs på innan du copy-pastar.
```
#!/bin/bash

keytool -genkeypair -alias client-app -keyalg RSA -keysize 2048 -storetype JKS -keystore client-app.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1
keytool -genkeypair -alias server-app -keyalg RSA -keysize 2048 -storetype JKS -keystore server-app.jks -validity 3650 -ext SAN=dns:localhost,ip:127.0.0.1

keytool -export -alias client-app -file client-app.crt -keystore client-app.jks

keytool -export -alias server-app -file server-app.crt -keystore server-app.jks

keytool -import -alias server-app -file server-app.crt -keystore client-app.jks
keytool -import -alias client-app -file client-app.crt -keystore server-app.jks
```

3. Mounta dessa certifikat i din docker container. Här kan man hitta inspiration om hur jag gjorde.
```
riskService:
    image: daneidmark/risk:0.0.2
    environment:
      - SERVER_SSL_KEY-STORE=file:/certs/server-app.jks
      - SERVER_SSL_TRUST-STORE=file:/certs/server-app.jks
    ports:
      - "8081:8081"
    volumes:
      - "/Users/dan/git/github/lab3-risk-service/src/main/resources:/certs"
  userService:
    image: daneidmark/user:0.0.1
    ports:
      - "8082:8082"
```

4. Konfigurera en SSL-kontext och preppa din RestTemplate att göra ett HTTPS andop. Dvs risk servicen lyssnar inte längre på http.
Det går att snegla lite i ApplicationConfiguration om man är sugen på att hitta lite inspriation.

5. Testa att slå på HTTPS för din app
   server.ssl.enabled: true


