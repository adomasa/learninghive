package distributed.monolith.learninghive.service;


import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.RestrictionRequest;
import distributed.monolith.learninghive.model.response.RestrictionResponse;
import distributed.monolith.learninghive.repository.RestrictionRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.restrictions.RestrictionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestrictionServiceImpl implements RestrictionService {
	private final RestrictionRepository restrictionRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	public RestrictionResponse createRestriction(RestrictionRequest restrictionRequest) {
		throwIfDuplicate(restrictionRequest.getUserId(), restrictionRequest.getRestrictionType());

		Restriction restriction = new Restriction();
		mountEntity(restriction, restrictionRequest);

		return modelMapper.map(restrictionRepository.save(restriction), RestrictionResponse.class);
	}

	@Override
	public RestrictionResponse updateRestriction(Long id, RestrictionRequest restrictionRequest) {
		Restriction restriction = restrictionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						Restriction.class.getSimpleName(),
						id));

		Long userId = restriction.getUser() == null ? null : restriction.getUser().getId();
		if (userId != restrictionRequest.getUserId() ||
				restriction.getRestrictionType() != restrictionRequest.getRestrictionType()) {
			throwIfDuplicate(restrictionRequest.getUserId(), restrictionRequest.getRestrictionType());
		}

		mountEntity(restriction, restrictionRequest);
		return modelMapper.map(restrictionRepository.save(restriction), RestrictionResponse.class);
	}

	@Override
	public List<RestrictionResponse> findByUserId(Long userId, boolean includeGlobal) {
		List<Restriction> restrictions = includeGlobal ?
				restrictionRepository.findByUserIdOrUserIdIsNull(userId) :
				restrictionRepository.findByUserId(userId);

		return restrictions.parallelStream()
				.map(r -> modelMapper.map(r, RestrictionResponse.class))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteRestriction(Long id) {
		Restriction restriction = restrictionRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class.getSimpleName(), id));


		restrictionRepository.delete(restriction);
	}

	private void mountEntity(Restriction destination, RestrictionRequest source) {
		destination.setRestrictionType(source.getRestrictionType());
		destination.setDaysLimit(source.getDaysLimit());

		User user = null;
		if (source.getUserId() != null) {
			user = userRepository.findById(source.getUserId())
					.orElseThrow(() -> new ResourceNotFoundException(
							User.class.getSimpleName(),
							source.getUserId()
					));
		}
		destination.setUser(user);
	}

	private void throwIfDuplicate(Long userId, RestrictionType type) {
		restrictionRepository.findByUserIdAndRestrictionType(userId, type)
				.ifPresent(restriction -> {
					throw new DuplicateResourceException(Restriction.class.getSimpleName(),
							"userId and restrictionType",
							userId + " and " + type.toString());
				});
	}
}