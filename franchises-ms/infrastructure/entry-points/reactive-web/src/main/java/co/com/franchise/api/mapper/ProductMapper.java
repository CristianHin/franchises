package co.com.franchise.api.mapper;

import co.com.franchise.api.dto.request.ProductRequestDto;
import co.com.franchise.api.dto.response.ProductResponseDto;
import co.com.franchise.model.porduct.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", ignore = true)
    Product requestDtoToProduct(ProductRequestDto productRequestDto);

    ProductResponseDto productToResponseDto(Product product);
}
