package co.com.franchise.jpa.mapper;

public interface GenericMapper <M, D> {
    M domainToModel(D domain);
    D modelToDomain(M model);
}
