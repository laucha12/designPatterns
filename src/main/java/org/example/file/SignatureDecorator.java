package org.example.file;

import org.example.models.Fixture;

import java.io.InputStream;
import java.io.OutputStream;

public class SignatureDecorator extends FixtureDataSourceDecorator{

//    https://www.baeldung.com/java-read-pem-file-keys


    public SignatureDecorator(FixtureDataSource fixtureDataSource) {
        super(fixtureDataSource);
    }

    @Override
    public Fixture readFixture(InputStream inputStream) throws Exception {
        return null;
    }

    @Override
    public void writeFixture(Fixture fixture, OutputStream outputStream) throws Exception {

    }
}
