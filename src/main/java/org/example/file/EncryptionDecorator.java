package org.example.file;

import org.example.models.Fixture;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionDecorator extends FixtureDataSourceDecorator{

    private static final String ALGORITHM = "AES/OFB/NoPadding";
    private static final int IV_LENGTH = 16; // AES block size
    private static final int SALT_LENGTH = 16;
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 65536;

    private final String password;

    public EncryptionDecorator(FixtureDataSource wrappedDataSource, String password) throws Exception {
        super(wrappedDataSource);
        this.password = password;
    }

    private SecretKey deriveKey(String keyPassword, byte[] salt) throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        KeySpec spec = new PBEKeySpec(keyPassword.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        byte[] keyBytes = keyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    private String encrypt(byte[] content) throws Exception {
        //Derive password and generate IV
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKey secretKey = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(content);
        byte[] ans = new byte[salt.length + iv.length + encrypted.length];
        //Append the salt and IV to recover later
        System.arraycopy(salt, 0, ans, 0, salt.length);
        System.arraycopy(iv, 0, ans, salt.length, iv.length);
        System.arraycopy(encrypted, 0, ans, salt.length + iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(ans);
    }


    private byte[] decrypt(String content) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(content);

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        byte[] encryptedBytes = new byte[encrypted.length - SALT_LENGTH - IV_LENGTH];

        System.arraycopy(encrypted, 0, salt, 0, SALT_LENGTH);
        System.arraycopy(encrypted, SALT_LENGTH, iv, 0, IV_LENGTH);
        System.arraycopy(encrypted, SALT_LENGTH + IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        SecretKey key = deriveKey(password, salt);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        return cipher.doFinal(encryptedBytes);
    }

    @Override
    public Fixture readFixture(InputStream inputStream) throws Exception {
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try{
            byte[] decrypted = decrypt(content);
            return wrappedDataSource.readFixture(new ByteArrayInputStream(decrypted));
        }catch (Exception e){
            throw new BadFileException("Failed to decrypt fixture");
        }

    }

    @Override
    public void writeFixture(Fixture fixture, OutputStream outputStream) throws Exception {
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        wrappedDataSource.writeFixture(fixture, byteArrayInputStream);
        byte[] contents =  byteArrayInputStream.toByteArray();

        String encrypted = encrypt(contents);

        outputStream.write(encrypted.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
