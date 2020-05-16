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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
				.orElseThrow(() -> new ResourceNotFoundException(Restriction.class, id));

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
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class, id));

		restrictionRepository.delete(restriction);
	}

	@Override
	@Transactional
	public List<RestrictionResponse> copyToTeam(Long supervisorId, Long restrictionId) {
		User supervisor = userRepository.findById(supervisorId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, supervisorId));
		Restriction sourceRestriction = restrictionRepository.findById(restrictionId)
				.orElseThrow(() -> new ResourceNotFoundException(Restriction.class, restrictionId));

		List<Restriction> restrictions = supervisor.getSubordinates()
				.stream()
				.map(subordinate -> findOrCreateRestriction(subordinate, sourceRestriction))
				.collect(Collectors.toList());

		return restrictionRepository.saveAll(restrictions)
				.stream()
				.map(r -> modelMapper.map(r, RestrictionResponse.class))
				.collect(Collectors.toList());
	}

	private Restriction findOrCreateRestriction(User user, Restriction sourceRestriction) {
		Restriction targetRestriction = restrictionRepository
				.findByUserIdAndRestrictionType(user.getId(), sourceRestriction.getRestrictionType())
				.orElse(new Restriction());

		targetRestriction.setUser(user);
		targetRestriction.setDaysLimit(sourceRestriction.getDaysLimit());
		targetRestriction.setRestrictionType(sourceRestriction.getRestrictionType());
		return targetRestriction;
	}

	private void mountEntity(Restriction destination, RestrictionRequest source) {
		destination.setRestrictionType(source.getRestrictionType());
		destination.setDaysLimit(source.getDaysLimit());

		User user = null;
		if (source.getUserId() != null) {
			user = userRepository.findById(source.getUserId())
					.orElseThrow(() -> new ResourceNotFoundException(User.class, source.getUserId()));
		}
		destination.setUser(user);
	}

	private void throwIfDuplicate(Long userId, RestrictionType type) {
		restrictionRepository.findByUserIdAndRestrictionType(userId, type)
				.ifPresent(restriction -> {
					throw new DuplicateResourceException(Restriction.class,
							"userId and restrictionType",
							userId + " and " + type.toString());
				});
	}
}
