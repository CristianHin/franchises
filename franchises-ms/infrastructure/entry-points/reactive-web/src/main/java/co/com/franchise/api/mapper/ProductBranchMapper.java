package co.com.franchise.api.mapper;

import co.com.franchise.api.dto.response.BranchResponseDto;
import co.com.franchise.api.dto.response.ProductBranchResponseDto;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.porduct.ProductBranch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ProductMapper.class})
public interface ProductBranchMapper {

    ProductBranchMapper MAPPER = Mappers.getMapper(ProductBranchMapper.class);

    List<BranchResponseDto> listBranchesToResponseDto(List<Branch> branches);

    @Mapping(target = "franchiseId", ignore = true)
    BranchResponseDto branchToResponseDto(Branch branch);

    ProductBranchResponseDto productBranchToResponseDto(ProductBranch product);
}
