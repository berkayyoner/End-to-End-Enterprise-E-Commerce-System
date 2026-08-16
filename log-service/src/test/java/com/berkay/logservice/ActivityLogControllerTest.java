package com.berkay.logservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityLogController.class)
class ActivityLogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ActivityLogService service;

	@Test
	void rejectsRequestMissingRequiredFields() throws Exception {
		mockMvc.perform(post("/logs")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.actorType").exists())
				.andExpect(jsonPath("$.errors.sourceService").exists())
				.andExpect(jsonPath("$.errors.action").exists());
	}

	@Test
	void recordsAValidActivityLog() throws Exception {
		ActivityLogResponse response = new ActivityLogResponse(1L, ActorType.USER, 42L, "auth-service",
				"LOGIN_SUCCESS", null, "127.0.0.1", Instant.parse("2026-08-17T00:00:00Z"));
		when(service.record(any())).thenReturn(response);

		mockMvc.perform(post("/logs")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "actorType": "USER",
								  "actorId": 42,
								  "sourceService": "auth-service",
								  "action": "LOGIN_SUCCESS",
								  "ipAddress": "127.0.0.1"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.action").value("LOGIN_SUCCESS"));
	}

	@Test
	void searchesByActorTypeAndActorId() throws Exception {
		ActivityLogResponse response = new ActivityLogResponse(2L, ActorType.PERSONNEL, 7L, "auth-service",
				"PERMISSION_GRANTED", null, null, Instant.parse("2026-08-17T00:00:00Z"));
		when(service.search(eq(ActorType.PERSONNEL), eq(7L), isNull(), any()))
				.thenReturn(new PageImpl<>(List.of(response)));

		mockMvc.perform(get("/logs").param("actorType", "PERSONNEL").param("actorId", "7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(2));
	}
}
