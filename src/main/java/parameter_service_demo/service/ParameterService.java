package parameter_service_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parameter_service_demo.dto.NewParameterDto;
import parameter_service_demo.dto.ParameterDto;
import parameter_service_demo.exception.EntityNotFoundException;
import parameter_service_demo.model.ParameterEntity;
import parameter_service_demo.repository.ParameterRepository;

@Service
@RequiredArgsConstructor
public class ParameterService {

    private final ParameterRepository parameterRepository;

    @Transactional
    public ParameterDto save(NewParameterDto newParameterDto) {
        return parameterEntityToDto(parameterRepository.save(newParameterDtoToEntity(newParameterDto)));
    }

    @Transactional(readOnly = true)
    public ParameterDto loadById(Long id) {
        var parameterEntity = parameterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Parameter", id));
        return parameterEntityToDto(parameterEntity);
    }

    @Transactional
    public ParameterDto updateById(Long id, NewParameterDto newParameterDto) {
        if (!parameterRepository.existsById(id)) {
            throw new EntityNotFoundException("Parameter", id);
        }

        var parameterEntity = newParameterDtoToEntity(newParameterDto);
        parameterEntity.setId(id);
        return parameterEntityToDto(parameterRepository.save(parameterEntity));
    }

    @Transactional
    public void deleteById(Long id) {
        parameterRepository.deleteById(id);
    }

    private ParameterEntity newParameterDtoToEntity(NewParameterDto newParameterDto) {
        return ParameterEntity.builder()
                .name(newParameterDto.getName())
                .value(newParameterDto.getValue())
                .build();
    }

    private ParameterDto parameterEntityToDto(ParameterEntity parameterEntity) {
        return ParameterDto.builder()
                .id(parameterEntity.getId())
                .name(parameterEntity.getName())
                .value(parameterEntity.getValue())
                .build();
    }
}
