package org.example.file;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.example.models.Fixture;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class SignatureDecorator extends FixtureDataSourceDecorator{

    private static final String PRIVATE_KEY_RESOURCE = "keys/key.pem";
    private static final String PUBLIC_KEY_RESOURCE = "keys/public.pem";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String ALGORITHM = "SHA256withRSA";
    private static final String DELIMITER = "::SIGNATURE::";

    private final PrivateKey privateKey;
    private final PublicKey  publicKey;

    public SignatureDecorator(FixtureDataSource fixtureDataSource) throws Exception {
        super(fixtureDataSource);
        privateKey = readPrivateKey(this.getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_RESOURCE));
        publicKey = readPublicKey(this.getClass().getClassLoader().getResourceAsStream(PUBLIC_KEY_RESOURCE));
    }

    private PublicKey readPublicKey(InputStream stream) throws Exception{
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
        try (PemReader  pemReader = new PemReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(content);
            return kf.generatePublic(publicKeySpec);
        }
    }

    private PrivateKey readPrivateKey(InputStream stream) throws Exception{
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
        try (PemReader pemReader = new PemReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(content);
            return kf.generatePrivate(privateKeySpec);
        }
    }

    private boolean verifySignature(String contents, String signature, PublicKey publicKey) throws Exception {
        Signature signAlgorithm = Signature.getInstance(ALGORITHM);
        signAlgorithm.initVerify(publicKey);
        signAlgorithm.update(contents.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return signAlgorithm.verify(signatureBytes);
    }

    private String sign(byte[] contents, PrivateKey privateKey) throws Exception {
        Signature signAlgorithm = Signature.getInstance(ALGORITHM);
        signAlgorithm.initSign(privateKey);
        signAlgorithm.update(contents);

        byte[] signatureBytes = signAlgorithm.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    @Override
    public Fixture readFixture(InputStream inputStream) throws Exception {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)
                .useDelimiter(DELIMITER);
        String signature = scanner.next();
        if(!scanner.hasNext()){
            throw new BadFileException("No delimiter found for signature");
        }
        String content = scanner.next();
        try{
            if(!verifySignature(content, signature, publicKey)){
                throw new BadFileException("Invalid signature for file");
            }
        }catch (Exception e){
            throw new BadFileException("Invalid signature for file");
        }
        return wrappedDataSource.readFixture(new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public void writeFixture(Fixture fixture, OutputStream outputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        wrappedDataSource.writeFixture(fixture, byteArrayOutputStream);
        byte[] contents = byteArrayOutputStream.toByteArray();
        String signature = sign(contents, privateKey);

        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        writer.write(signature);
        writer.write(DELIMITER);
        writer.write(new String(contents, StandardCharsets.UTF_8));
        writer.flush();
    }
}
