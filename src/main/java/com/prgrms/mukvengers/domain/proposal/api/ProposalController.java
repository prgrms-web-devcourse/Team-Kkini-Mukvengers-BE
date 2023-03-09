package com.prgrms.mukvengers.domain.proposal.api;

import static org.springframework.http.MediaType.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgrms.mukvengers.domain.proposal.dto.request.CreateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalResponses;
import com.prgrms.mukvengers.domain.proposal.service.ProposalService;
import com.prgrms.mukvengers.global.common.dto.ApiResponse;
import com.prgrms.mukvengers.global.common.dto.IdResponse;
import com.prgrms.mukvengers.global.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProposalController {

	private final ProposalService proposalService;

	/**
	 * <pre>
	 *     사용자가 신청서를 작성합니다.
	 * </pre>
	 * @param crewId 신청하고자 하는 밥모임 아이디
	 * @param proposalRequest 신청서 생성 DTO
	 * @param user 유저 정보
	 * @return status : 201, body : 생성된 신청서 조회 redirectUrl
	 */
	@PostMapping(value = "/crews/{crewId}/proposals", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<IdResponse> create
	(
		@PathVariable Long crewId,
		@RequestBody @Valid CreateProposalRequest proposalRequest,
		@AuthenticationPrincipal JwtAuthentication user
	) {
		IdResponse proposal = proposalService.create(proposalRequest, user.id(), crewId);
		URI location = UriComponentsBuilder.fromUriString("/api/v1/proposals/" + proposal.id()).build().toUri();
		return ResponseEntity.created(location).body(proposal);
	}

	/**
	 * <pre>
	 *     사용자가 방장인 모임의 신청서를 모두 조회합니다.
	 * </pre>
	 * @param user 사용자 정보
	 * @return status : 200, body : 조회된 모든 신청서 데이터
	 */
	@GetMapping(value = "/proposals/leader", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ProposalResponses>> getProposalsByLeaderId
	(
		@AuthenticationPrincipal JwtAuthentication user
	) {
		ProposalResponses responses = proposalService.getProposalsByLeaderId(user.id());

		return ResponseEntity.ok().body(new ApiResponse<>(responses));
	}

	/**
	 * <pre>
	 *     사용자가 방장인 아니고 참여자인 모임의 신청서를 모두 조회합니다.
	 * </pre>
	 * @param user 사용자 정보
	 * @return status : 200, body : 조회된 모든 신청서 데이터
	 */
	@GetMapping(value = "/proposals/member", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ProposalResponses>> getProposalsByMemberId
	(
		@AuthenticationPrincipal JwtAuthentication user
	) {
		ProposalResponses responses = proposalService.getProposalsByMemberId(user.id());

		return ResponseEntity.ok().body(new ApiResponse<>(responses));
	}

}