package org.example.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Fixture;

import java.io.InputStream;
import java.io.OutputStream;

public class FileFixtureDataSource implements FixtureDataSource {


    @Override
    public Fixture readFixture(InputStream inputStream) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, Fixture.class);
    }

    @Override
    public void writeFixture(Fixture fixture, OutputStream outputStream) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, fixture);
    }
}
