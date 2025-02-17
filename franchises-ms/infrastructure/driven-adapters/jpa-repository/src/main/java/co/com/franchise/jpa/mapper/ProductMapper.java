package co.com.franchise.jpa.mapper;

import co.com.franchise.jpa.model.ProductModel;
import co.com.franchise.model.porduct.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper extends GenericMapper<ProductModel, Product> {
    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    @Override
    @Mapping(target = "productBranches", ignore = true)
    ProductModel domainToModel(Product domain);
}
