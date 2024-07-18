package org.kakaopay.coffee.api.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Converter
public class PasswordConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String raw) {
        return encode(raw);
    }

    @Override
    public String convertToEntityAttribute(String encoded) {
        return encoded;
    }

    public static String encode(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return String.format("%0128x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
