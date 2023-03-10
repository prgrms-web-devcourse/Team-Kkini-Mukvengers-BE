package com.prgrms.mukvengers.domain.proposal.service;

import static com.prgrms.mukvengers.domain.proposal.model.vo.ProposalStatus.*;
import static com.prgrms.mukvengers.utils.CrewObjectProvider.*;
import static com.prgrms.mukvengers.utils.ProposalObjectProvider.*;
import static com.prgrms.mukvengers.utils.UserObjectProvider.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgrms.mukvengers.base.ServiceTest;
import com.prgrms.mukvengers.domain.crew.model.Crew;
import com.prgrms.mukvengers.domain.crewmember.model.CrewMember;
import com.prgrms.mukvengers.domain.crewmember.model.vo.CrewMemberRole;
import com.prgrms.mukvengers.domain.proposal.dto.request.CreateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.request.UpdateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalResponse;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalResponses;
import com.prgrms.mukvengers.domain.proposal.exception.CrewMemberOverCapacity;
import com.prgrms.mukvengers.domain.proposal.exception.DuplicateProposalException;
import com.prgrms.mukvengers.domain.proposal.exception.ExistCrewMemberRoleException;
import com.prgrms.mukvengers.domain.proposal.exception.InvalidProposalStatusException;
import com.prgrms.mukvengers.domain.proposal.model.Proposal;
import com.prgrms.mukvengers.domain.proposal.model.vo.ProposalStatus;
import com.prgrms.mukvengers.domain.user.model.User;
import com.prgrms.mukvengers.global.common.dto.IdResponse;
import com.prgrms.mukvengers.utils.CrewMemberObjectProvider;
import com.prgrms.mukvengers.utils.ProposalObjectProvider;

class ProposalServiceImplTest extends ServiceTest {

	private User leader;
	private Crew crew;

	@BeforeEach
	void setUp() {
		User createUser = createUser("12121212");
		leader = userRepository.save(createUser);

		Crew createCrew = createCrew(savedStore);
		crew = crewRepository.save(createCrew);

		CrewMember createCrewMember = CrewMemberObjectProvider.createCrewMember(leader.getId(), crew,
			CrewMemberRole.LEADER);
		crewMemberRepository.save(createCrewMember);
	}

	@Test
	@DisplayName("[??????] ???????????? ???????????? ????????? ??? ??????.")
	void createProposal_success() {

		//given
		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(leader.getId());

		// when
		IdResponse response = proposalService.create(proposalRequest, savedUserId, crew.getId());

		// then
		Optional<Proposal> findProposal = proposalRepository.findById(response.id());
		assertThat(findProposal).isPresent();
		assertThat(findProposal.get())
			.hasFieldOrPropertyWithValue("user", savedUser)
			.hasFieldOrPropertyWithValue("leaderId", leader.getId())
			.hasFieldOrPropertyWithValue("crewId", crew.getId())
			.hasFieldOrPropertyWithValue("content", proposalRequest.content());
	}

	@Test
	@DisplayName("[??????] ?????? ???????????? ???????????? ???????????? ?????? ??????????????? ???????????? ????????? ??? ??????.")
	void createProposal_fail_duplicate() {

		// given
		Proposal createProposal = createProposal(savedUser, leader.getId(), crew.getId());
		proposalRepository.save(createProposal);

		CreateProposalRequest worstRequest = createProposalRequest(leader.getId());

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(worstRequest, savedUserId, crew.getId())
			).isInstanceOf(DuplicateProposalException.class);
	}

	@Test
	@DisplayName("[??????] ?????? ????????? ??? ??? ??????????????? ???????????? ????????? ??? ??????.")
	void createProposal_fail_countOverCapacity() {

		//given
		List<CrewMember> crewMembers = CrewMemberObjectProvider.createCrewMembers(savedUserId, crew,
			CrewMemberRole.MEMBER,
			crew.getCapacity());

		crewMemberRepository.saveAll(crewMembers);

		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(leader.getId());

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, savedUserId, crew.getId())
			)
			.isInstanceOf(CrewMemberOverCapacity.class);
	}

	@Test
	@DisplayName("[??????] ?????? ???????????? ????????? ??????????????? ???????????? ????????? ??? ??????.")
	void createProposal_fail_blockedUser() {

		//given
		CrewMember createCrewMember = CrewMemberObjectProvider.createCrewMember(savedUserId, crew,
			CrewMemberRole.BLOCKED);
		crewMemberRepository.save(createCrewMember);

		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(savedUserId);

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, savedUserId, crew.getId())
			)
			.isInstanceOf(ExistCrewMemberRoleException.class);
	}

	@Test
	@DisplayName("[??????] ???????????? ?????? ???????????? ???????????? ???????????? ????????? ??? ??????.")
	void createProposal_fail_LeaderUser() {

		//given
		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(leader.getId());

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, leader.getId(), crew.getId())
			)
			.isInstanceOf(ExistCrewMemberRoleException.class);
	}

	@Test
	@DisplayName("[??????] ???????????? ?????? ?????? ???????????? ??????????????? ???????????? ????????? ??? ??????.")
	void createProposal_fail_DuplicatedUser() {

		//given
		CrewMember createCrewMember = CrewMemberObjectProvider.createCrewMember(savedUserId, crew,
			CrewMemberRole.MEMBER);
		crewMemberRepository.save(createCrewMember);

		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(savedUserId);

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, savedUserId, crew.getId())
			)
			.isInstanceOf(ExistCrewMemberRoleException.class);
	}

	@Test
	@DisplayName("[??????] ????????? ???????????? '??????'?????? ?????? ??????????????? ????????????.")
	void update_proposalStatus_approve_success() {

		//given
		String inputProposalStatus = "??????";

		User createUser = createUser("1232456789");
		User user = userRepository.save(createUser);

		Proposal createProposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		Proposal proposal = proposalRepository.save(createProposal);

		UpdateProposalRequest proposalRequest = new UpdateProposalRequest(inputProposalStatus);

		// when
		proposalService.updateProposalStatus(proposalRequest, leader.getId(), proposal.getId());
		Optional<CrewMember> result = crewMemberRepository.findCrewMemberByCrewIdAndUserId(
			crew.getId(), user.getId());

		// then
		assertThat(proposal.getStatus()).isEqualTo(APPROVE);
		assertThat(result).isPresent();
		assertThat(result.get().getUserId()).isEqualTo(user.getId());
	}

	@Test
	@DisplayName("[??????] ????????? ???????????? '??????'?????? ?????? ??????????????? ???????????? ?????????.")
	void update_proposalStatus_refuse_success() {

		//given
		String inputProposalStatus = "??????";

		User createUser = createUser("1232456789");
		User user = userRepository.save(createUser);

		Proposal createProposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		Proposal proposal = proposalRepository.save(createProposal);

		UpdateProposalRequest proposalRequest = new UpdateProposalRequest(inputProposalStatus);

		// when
		proposalService.updateProposalStatus(proposalRequest, leader.getId(), proposal.getId());
		Optional<CrewMember> saveCrewMember = crewMemberRepository.findCrewMemberByCrewIdAndUserId(
			crew.getId(), user.getId());

		// then
		assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.REFUSE);
		assertThat(saveCrewMember).isEmpty();
	}

	@Test
	@DisplayName("[??????] ????????? ???????????? ?????? ????????? ???????????? ????????? ????????????.")
	void update_proposalStatus_fail_otherProposalStatus() {

		//given
		String inputProposalStatus = "??????";

		User createUser = createUser("1232456789");
		User user = userRepository.save(createUser);

		Proposal createProposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		Proposal proposal = proposalRepository.save(createProposal);

		UpdateProposalRequest proposalRequest = new UpdateProposalRequest(inputProposalStatus);

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.updateProposalStatus(proposalRequest, leader.getId(), proposal.getId())
			)
			.isInstanceOf(InvalidProposalStatusException.class);
	}

	@Test
	@DisplayName("[??????] ????????? ???????????? ???????????? ????????????.")
	void getById() {
		//given
		User user = createUser("1232456789");
		userRepository.save(user);

		Proposal proposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		proposalRepository.save(proposal);

		//when
		ProposalResponse response = proposalService.getById(proposal.getId());

		assertThat(response)
			.hasFieldOrPropertyWithValue("id", proposal.getId())
			.hasFieldOrPropertyWithValue("leaderId", leader.getId())
			.hasFieldOrPropertyWithValue("crewId", crew.getId())
			.hasFieldOrPropertyWithValue("content", proposal.getContent())
			.hasFieldOrPropertyWithValue("status", proposal.getStatus());

		assertThat(response.user())
			.hasFieldOrPropertyWithValue("id", user.getId())
			.hasFieldOrPropertyWithValue("nickname", user.getNickname())
			.hasFieldOrPropertyWithValue("profileImgUrl", user.getProfileImgUrl())
			.hasFieldOrPropertyWithValue("introduction", user.getIntroduction())
			.hasFieldOrPropertyWithValue("leaderCount", user.getLeaderCount())
			.hasFieldOrPropertyWithValue("crewCount", user.getCrewCount())
			.hasFieldOrPropertyWithValue("tasteScore", user.getTasteScore())
			.hasFieldOrPropertyWithValue("mannerScore", user.getMannerScore());
	}

	@Test
	@DisplayName("[??????] ???????????? ????????? ????????? ?????? ???????????? ????????????.")
	void getProposalsByLeaderId_success() {

		//given
		User user = createUser("1232456789");
		userRepository.save(user);

		List<Proposal> proposals = createProposals(user, leader.getId(), crew.getId());
		proposalRepository.saveAll(proposals);

		//when
		ProposalResponses responses = proposalService.getProposalsByLeaderId(leader.getId());

		//then
		assertThat(responses.responses()).hasSize(proposals.size());
	}

	@Test
	@DisplayName("[??????] ???????????? ????????? ????????? ???????????? ???????????? ???????????? ?????? ???????????????.")
	void getProposalsByMemberId_success() {

		//given
		User user = createUser("1232456789");
		userRepository.save(user);

		List<Proposal> proposals = createProposals(user, leader.getId(), crew.getId());
		proposalRepository.saveAll(proposals);

		//when
		ProposalResponses responses = proposalService.getProposalsByMemberId(user.getId());

		//then
		assertThat(responses.responses()).hasSize(proposals.size());
	}

}