package org.example.file;

import org.example.models.Fixture;

import java.io.InputStream;
import java.io.OutputStream;

public interface FixtureDataSource {

    Fixture readFixture(InputStream inputStream) throws Exception;

    void writeFixture(Fixture fixture, OutputStream outputStream) throws Exception;

}
