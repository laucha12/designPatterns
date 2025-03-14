package org.example.file;

public abstract class FixtureDataSourceDecorator implements FixtureDataSource {

    protected FixtureDataSource wrappedDataSource;

    public FixtureDataSourceDecorator(FixtureDataSource wrappedDataSource) throws Exception{
        this.wrappedDataSource = wrappedDataSource;
    }
}
