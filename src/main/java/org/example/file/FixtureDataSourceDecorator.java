package org.example.file;

public abstract class FixtureDataSourceDecorator implements FixtureDataSource {

    protected FixtureDataSource wrappedDataSource;

    public FixtureDataSourceDecorator(FixtureDataSource wrappedDataSource) {
        this.wrappedDataSource = wrappedDataSource;
    }
}
