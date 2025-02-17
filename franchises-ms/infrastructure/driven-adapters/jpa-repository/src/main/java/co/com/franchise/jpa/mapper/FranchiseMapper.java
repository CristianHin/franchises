package co.com.franchise.jpa.mapper;

import co.com.franchise.jpa.model.BranchModel;
import co.com.franchise.jpa.model.FranchiseModel;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {BranchMapper.class})
public interface FranchiseMapper extends GenericMapper<FranchiseModel, Franchise> {

    FranchiseMapper MAPPER = Mappers.getMapper(FranchiseMapper.class);

    @Override
    FranchiseModel domainToModel(Franchise domain);

    @Override
    Franchise modelToDomain(FranchiseModel model);

}
