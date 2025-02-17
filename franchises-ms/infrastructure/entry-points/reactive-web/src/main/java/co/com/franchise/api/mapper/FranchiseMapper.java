package co.com.franchise.api.mapper;

import co.com.franchise.api.dto.response.FranchiseBranchResponseDto;
import co.com.franchise.api.dto.response.FranchiseResponseDto;
import co.com.franchise.model.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {BranchMapper.class})
public interface FranchiseMapper {
    FranchiseMapper MAPPER = Mappers.getMapper(FranchiseMapper.class);

    FranchiseResponseDto franchiseToResponseDto(Franchise franchise);

    List<FranchiseBranchResponseDto> listFranchisesToResponseDto(List<Franchise> franchises);


}
