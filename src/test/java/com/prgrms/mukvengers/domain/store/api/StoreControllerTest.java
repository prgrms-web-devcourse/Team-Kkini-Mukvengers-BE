package com.prgrms.mukvengers.domain.store.api;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.epages.restdocs.apispec.Schema;
import com.prgrms.mukvengers.base.ControllerTest;
import com.prgrms.mukvengers.domain.store.dto.request.CreateStoreRequest;
import com.prgrms.mukvengers.utils.StoreObjectProvider;

class StoreControllerTest extends ControllerTest {

	public static final Schema CREATE_STORE_REQUEST = new Schema("createStoreRequest");
	public static final Schema STORE_RESPONSE = new Schema("storeResponse");

	@Test
	@DisplayName("[성공] 가게를 저장한다.")
	void create_success() throws Exception {

		CreateStoreRequest createStoreRequest = StoreObjectProvider.getCreateStoreRequest("123456789");

		String jsonRequest = objectMapper.writeValueAsString(createStoreRequest);

		mockMvc.perform(post("/api/v1/stores")
				.contentType(APPLICATION_JSON)
				.content(jsonRequest)
				.accept(APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/v1/stores")))
			.andExpect(redirectedUrlPattern("/api/v1/stores/*"))
			.andDo(print())
			.andDo(document("store-create",
				resource(
					builder()
						.tags(STORE)
						.summary("가게 저장 API")
						.requestSchema(CREATE_STORE_REQUEST)
						.description("가게 정보를 저장합니다.")
						.requestFields(
							fieldWithPath("latitude").type(NUMBER).description("위도"),
							fieldWithPath("longitude").type(NUMBER).description("경도"),
							fieldWithPath("placeId").type(STRING).description("지도 api 제공 id"),
							fieldWithPath("placeName").type(STRING).description("가게 이름"),
							fieldWithPath("categories").type(STRING).description("가게 카테고리"),
							fieldWithPath("roadAddressName").type(STRING).description("가게 도로명 주소"),
							fieldWithPath("photoUrls").type(STRING).description("가게 사진 URL"),
							fieldWithPath("kakaoPlaceUrl").type(STRING).description("가게 상세 페이지 URL"),
							fieldWithPath("phoneNumber").type(STRING).description("가게 전화번호"))
						.responseHeaders(
							headerWithName("Location").description("조회해볼 수 있는 요청 주소"))
						.build()
				)
			));
	}

	@Test
	@DisplayName("[성공] 가게 아이디로 Store 조회를 성공한다.")
	void getByPlaceId_success() throws Exception {

		mockMvc.perform(get("/api/v1/stores/{storeId}", savedStore.getId())
				.accept(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("store-getByStoreId",
				resource(
					builder()
						.tags(STORE)
						.summary("가게 조회 API")
						.responseSchema(STORE_RESPONSE)
						.description("가게 정보를 조회합니다.")
						.pathParameters(
							parameterWithName("storeId").description("가게 아이디")
						)
						.responseFields(
							fieldWithPath("data.id").type(NUMBER).description("가게 아이디"),
							fieldWithPath("data.latitude").type(NUMBER).description("위도"),
							fieldWithPath("data.longitude").type(NUMBER).description("경도"),
							fieldWithPath("data.placeId").type(STRING).description("지도 api 제공 id"),
							fieldWithPath("data.placeName").type(STRING).description("가게 이름"),
							fieldWithPath("data.categories").type(STRING).description("가게 카테고리"),
							fieldWithPath("data.roadAddressName").type(STRING).description("가게 도로명 주소"),
							fieldWithPath("data.photoUrls").type(STRING).description("가게 사진 URL"),
							fieldWithPath("data.kakaoPlaceUrl").type(STRING).description("가게 상세 페이지 URL"),
							fieldWithPath("data.phoneNumber").type(STRING).description("가게 전화번호")
						)
						.build()
				)
			));
	}

}