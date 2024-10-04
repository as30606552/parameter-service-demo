package parameter_service_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parameter_service_demo.dto.NewParameterDto;
import parameter_service_demo.dto.ParameterDto;
import parameter_service_demo.dto.ParameterStatus;
import parameter_service_demo.exception.EntityNotFoundException;
import parameter_service_demo.exception.ImmutableParameterChangeException;
import parameter_service_demo.model.ParameterEntity;
import parameter_service_demo.repository.ParameterRepository;

import java.time.LocalDate;
import java.util.List;

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
        validateActive(id);

        var parameterEntity = newParameterDtoToEntity(newParameterDto);
        parameterEntity.setId(id);
        return parameterEntityToDto(parameterRepository.save(parameterEntity));
    }

    public List<ParameterDto> activateByName(String name, LocalDate activationDate) {
        return activate(name, activationDate).stream().map(this::parameterEntityToDto).toList();
    }

    @Transactional
    public void deleteById(Long id) {
        validateActive(id);
        parameterRepository.deleteById(id);
    }

    @Transactional
    protected List<ParameterEntity> activate(String name, LocalDate activationDate) {
        var parameterEntities = parameterRepository.findByName(name);

        for (var parameterEntity : parameterEntities) {
            if (parameterEntity.isActive()) {
                throw new ImmutableParameterChangeException(parameterEntity.getId());
            }
            parameterEntity.setActiveFrom(activationDate);
        }

        return parameterRepository.saveAll(parameterEntities);
    }

    private void validateActive(Long id) {
        parameterRepository.findById(id).ifPresent(parameterEntity -> {
            if (parameterEntity.isActive()) {
                throw new ImmutableParameterChangeException(id);
            }
        });
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
                .status(parameterEntity.isActive() ? ParameterStatus.ACTIVE : ParameterStatus.DRAFT)
                .activeFrom(parameterEntity.getActiveFrom())
                .build();
    }
}
