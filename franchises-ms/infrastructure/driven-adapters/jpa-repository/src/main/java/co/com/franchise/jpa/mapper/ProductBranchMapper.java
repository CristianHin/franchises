package co.com.franchise.jpa.mapper;

import co.com.franchise.jpa.model.ProductBranchId;
import co.com.franchise.jpa.model.ProductBranchModel;
import co.com.franchise.model.porduct.ProductBranch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ProductMapper.class})
public interface ProductBranchMapper extends GenericMapper<ProductBranchModel, ProductBranch> {
    ProductBranchMapper MAPPER = Mappers.getMapper(ProductBranchMapper.class);

    @Override
    @Mapping(target = "branchId", source = "branch.id")
    ProductBranch modelToDomain(ProductBranchModel model);

    @Override
    @Mapping(target = "branch.id", source = "branchId")
    @Mapping(target = "branch.name", ignore = true)
    @Mapping(target = "branch.franchise", ignore = true)
    @Mapping(target = "branch.productBranches", ignore = true)
    @Mapping(target = "id", source = "domain", qualifiedByName = "getProductBranchId")
    ProductBranchModel domainToModel(ProductBranch domain);

    @Named("getProductBranchId")
    default ProductBranchId getProductBranchId(ProductBranch domain) {
        return new ProductBranchId(domain.getProduct().getId(), domain.getBranchId());
    }
}
