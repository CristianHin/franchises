package co.com.franchise.api.mapper;

import co.com.franchise.api.dto.request.BranchRequestDto;
import co.com.franchise.api.dto.response.BranchResponseDto;
import co.com.franchise.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BranchMapper {
    BranchMapper MAPPER = Mappers.getMapper(BranchMapper.class);

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "id", ignore = true)
    Branch requestDtoToBranch(BranchRequestDto branchRequestDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "franchiseId", source = "franchiseId")
    @Mapping(target = "products", ignore = true)
    BranchResponseDto branchToResponseDto(Branch branch);
}
