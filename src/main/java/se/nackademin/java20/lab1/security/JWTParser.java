package se.nackademin.java20.lab1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class JWTParser {

    private static final byte[] SIGNING_KEY = "s2meBcgRWnODgza34+abFcStUXx49Ozju+Pd532YT1mfDMS8/Twv6wnjhLHdUJBkwbRoWP+N0vlBw4hSmY2HZ/WJYNRyOzD5f0wr3J3gyZC5fiWgs5lEJcygzTEvTufmRWPB10A8Est3o6co0lxom0ALe4q/mAU3046lm4T0QXDlazelWHVRbaYg07cHQGiIBGNJzEdi8CvzlsU3ArNiYgPw2fIREMVDM5axmg==".getBytes();

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private byte[] apiKeySecretBytes = Base64.encodeBase64(SIGNING_KEY);
    private Key key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


    public User validate(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        String authorities = (String) claims.get("authorities");
        List<Role> roles = Arrays.stream(authorities.split(",")).map(r -> r.substring(5)).map(Role::valueOf).collect(Collectors.toList());
        return new User(claims.getSubject(), roles);
    }
}