package co.com.franchise.jpa.mapper;

import co.com.franchise.jpa.model.BranchModel;
import co.com.franchise.jpa.model.FranchiseModel;
import co.com.franchise.jpa.model.ProductBranchModel;
import co.com.franchise.jpa.model.ProductModel;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.porduct.ProductBranch;
import co.com.franchise.model.porduct.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BranchMapper extends GenericMapper<BranchModel, Branch> {

    BranchMapper MAPPER = Mappers.getMapper(BranchMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "franchiseId", source = "franchise.id")
    @Mapping(target = "products", source = "productBranches")
    Branch modelToDomain(BranchModel model);

    Product modelToProduct(ProductModel productModel);

    @Mapping(target = "branchId", source = "branch.id")
    ProductBranch modelToProductBranch(ProductBranchModel productModel);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "franchise", source = "franchiseId", qualifiedByName = "getFranchiseModel")
    @Mapping(target = "productBranches", ignore = true)
    BranchModel domainToModel(Branch domain);

    @Named("getFranchiseModel")
    default FranchiseModel getFranchiseModel(Long id) {
        FranchiseModel franchiseModel = new FranchiseModel();
        franchiseModel.setId(id);
        return franchiseModel;
    }
}
