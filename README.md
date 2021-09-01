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

#### Steg 1: Login flödet
I denna lab ska vi skapa vår front-end som en single page applikation. Dvs vi ska inte låta spring boot rendera vyn utan låta klienten göra detta.

I katalogen `resources/public` kommer spring boot automatiskt leta efter en index.html och om den finns där kommer den att servas under root. eg localhost:8080/

Ni får använda vilket frontend-ramverk ni vill, men alla exempel kommer vara ganska basic med plain html och javascript och lite jQuery.

Det visar sig att det även har funnits ett annat team som har skapat en frontend åt er om ni inte vill skapa en egen (vilket kan vara ganska tidskrävande). Den är inte vacker men den fungerar hyffsat.

Det viktiga om ni väljer att använda den existerande frontenden är att ni läser koden och förstår vad den gör.

Hur hanteras tokens?
Hur används roller för att välja vyer?

Navigera till swaggerUI för userService och inspektera dess endpoints och testa att skapa en användare och logga in med den skapade användare.
När du loggar in får du en token tillbaka. Den är base64-enkodad. Decoda den och inspektera innehållet.

UserService är konfigurerad att signiera alla JWT-tokens med en nyckel. 
Skapa en klass JWTParser som tar emot en token, validerar den och skickar tillbaka en User.

```
public class JWTParser {

    private static final byte[] SIGNING_KEY = "s2meBcgRWnODgza34+abFcStUXx49Ozju+Pd532YT1mfDMS8/Twv6wnjhLHdUJBkwbRoWP+N0vlBw4hSmY2HZ/WJYNRyOzD5f0wr3J3gyZC5fiWgs5lEJcygzTEvTufmRWPB10A8Est3o6co0lxom0ALe4q/mAU3046lm4T0QXDlazelWHVRbaYg07cHQGiIBGNJzEdi8CvzlsU3ArNiYgPw2fIREMVDM5axmg==".getBytes();

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    private byte[] apiKeySecretBytes = Base64.encodeBase64(SIGNING_KEY);
    private Key key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


    public User validate(String token) {
       Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        String authorities = (String)claims.get("authorities");
        List<Role> roles = Arrays.stream(authorities.split(",")).map(r -> r.substring(5)).map(Role::valueOf).collect(Collectors.toList());
        return new User(claims.getSubject(),  roles);
    }
}
``` 

UserService har en motsvarighet där den använder samma SIGNING_KEY för att singa requestet.

Du kan nu skapa ett test och annotera det med `@Disabled` där du kan testa att ropa på validate, med tokens skapade från swagger för att se att allt leker.

#### Steg 2: Enable spring security i den app!

Efter slutfört moment ska du ha 
1. alla html-sidor ska vara publikt accessbara.
2. alla Rest-anrop ska kräva authentisering.

#### Steg 3: Skapa ett AuthorizationFilter
Efter detta steg kan du i dina rest endpoints skriva `public ResponseEntity<AccountDto> getAccount(@AuthenticationPrincipal User activeUser)` för att läsa ut information om den inloggade användaren.

Vi kommer att skapa ett AuthorizationFilter som kommer authenticate din användare. 

```
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JWTParser jwtIssuer;

    public JWTAuthorizationFilter(AuthenticationManager authManager, JWTParser jwtIssuer) {
        super(authManager);
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate the token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            // parse the token.
            User user = jwtIssuer.validate(token.substring(7));

            if (user != null) {
                // new arraylist means authorities
                return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            }

            return null;
        }

        return null;
    }
}
```

Därefter om vi lägger till vårat filter så kommer vi på varje authenticated request försöka lösa en token, validera den samt översätta den till en användare. Här kan ni även se hur roller kan användas för att restricta endpoint till bara en viss typ av användare.

```
@Configuration
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JWTParser jwtParser;

    @Autowired
    SecurityConfiguration(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final JWTAuthorizationFilter filter = new JWTAuthorizationFilter(authenticationManager(), jwtParser);

        http
                .csrf()
                .disable()
                .cors()
                .disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/partial/*").permitAll()
                .antMatchers("/admin/*").hasRole("ADMIN")
                .anyRequest().authenticated().and()
                .addFilter(filter)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
``` 

##### Implementera endpoints
1. /admin/open
Enbart admins ska kunna öppna konton. Förutom att öppna ett konto behöver vi enrolla en användare i userService. e.g anropa dess signup endpoint.
2. /account/deposit
3. /account/withdraw


## Del2: TLS (om du får tid över)
Vi vill gärna slå på HTTPS i vår app. Och då behöver vi skapa certifikat. Vi kommer skapa self-signed certificates i denna lab. Men i verkligheten använder vi saker som Lets encrypt för att skapa dem.
Det finns en ny version av Risktjänsten daneidmark/risk:0.0.2 i den har teamet implementerat mTLS. Dvs för att du ska kunna ropa på dess endpoint måste di tillhandahålla ett certifikat och ett lösenord.

### Skapa cerifikat

### TLS med self-signed certificates

Slå på HTTPS i din app


### mTLS med self-signed certificate


