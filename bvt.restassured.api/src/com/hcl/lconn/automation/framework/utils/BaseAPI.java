package com.hcl.lconn.automation.framework.utils;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.lconn.automation.framework.payload.BasePayload;
import com.ibm.atmn.waffle.utils.Assert;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.ParamConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public abstract class BaseAPI {

	private static Logger log = LoggerFactory.getLogger(BaseAPI.class);
	private static TestAPIConfigCustom cfg = TestAPIConfigCustom.getInstance();
	private static CustomRequestFilter crf = new CustomRequestFilter();

	protected String serverHost;
	protected String username;
	protected String password;

	private Assert cnxAssert;

	private RequestSpecBuilder requestSpecBuilder;
	private ResponseSpecBuilder responseSpecBuilder;
	private RestAssuredConfig requestConfig;

	public BaseAPI(String user, String pwd)  {
		username = user;
		password = pwd;

		// Note: must call useRelaxedHTTPSValidation like this, it's ignored when added to sslConfig().
		RestAssured.useRelaxedHTTPSValidation("TLS");
		requestConfig = config()
		    .sslConfig(SSLConfig.sslConfig().allowAllHostnames())
		    .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails())
		    .paramConfig(ParamConfig.paramConfig().replaceAllParameters());

		// set default configurations
		requestSpecBuilder = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setConfig(requestConfig);

		// set authentication
		String authType = cfg.getTestConfig().AuthenticationType();
		if (authType.equalsIgnoreCase("basic")) {
			log.info("Basic Auth will be used");
			requestSpecBuilder.addRequestSpecification(given().auth()
					.preemptive()
					.basic(username, password));

		} else if (authType.equalsIgnoreCase("form")) {
			log.info("Form Based Auth will be used");
			requestSpecBuilder.addRequestSpecification(given().auth()
					.form(username, password, new FormAuthConfig("/news/j_security_check", "j_username", "j_password")));

		} else {
			throw new IllegalArgumentException("Unsupported auth type, check auth_type in testTemplate");
		}

		responseSpecBuilder = new ResponseSpecBuilder();
		cnxAssert = new Assert(log);
	}


	//For Get
	public BaseAPI addQueryParam(String paramName, Object... paramValues) {
		log.info("Add query parameter: " + paramName + "=" + Arrays.toString(paramValues));
		requestSpecBuilder.addQueryParam(paramName, paramValues);
		return this;
	}

	//For Post
	public BaseAPI addFormParam(String paramName, Object... paramValues) {
		log.info("Add form parameter: " + paramName + "=" + Arrays.toString(paramValues));
		requestSpecBuilder.addFormParam(paramName, paramValues);
		return this;
	}

	//For Get
	public BaseAPI removeQueryParam(String paramName) {
		log.info("Remove query parameter: " + paramName);
		requestSpecBuilder.removeQueryParam(paramName);
		return this;
	}

	//For Post
	public BaseAPI removeFormParam(String paramName) {
		log.info("Remove form parameter: " + paramName);
		requestSpecBuilder.removeQueryParam(paramName);
		return this;
	}

	public BaseAPI setHeader(String headerName, String headerValue) {
		log.info("Add header: " + headerName + "= " + headerValue);
		requestSpecBuilder.addHeader(headerName, headerValue);
		return this;
	}

	public BaseAPI setContentType(String contentType) {
		log.info("Set ContentType = " + contentType);
		requestSpecBuilder.setContentType(contentType);
		return this;
	}

	protected BaseAPI setExpectedStatusCode(int statusCode) {
		log.info("Set expected return code: " + statusCode);
		responseSpecBuilder.expectStatusCode(statusCode);
		return this;
	}

	public BaseAPI setRequestSpec(RequestSpecBuilder requestSpec) {
		requestSpecBuilder = requestSpec;
		return this;
	}

	public BaseAPI setResponseSpec(ResponseSpecBuilder responseSpec) {
		responseSpecBuilder = responseSpec;
		return this;
	}

	public BaseAPI setServerHost(String serverHostName)  {
		serverHost = serverHostName;
		return this;
	}

	public BaseAPI setUserName(String user)  {
		username = user;
		return this;
	}

	public BaseAPI setPassword(String pwd)  {
		password = pwd;
		return this;
	}

	public Response get(String uri) {
		log.info("GET request called, user:" + username + " " + uri);
		try {
			return
					given()
					  .filter(crf)
					  .spec(requestSpecBuilder.build()).
					expect()
					  .spec(responseSpecBuilder.build()).
					when()
					  .get(uri);
		} catch (Exception | AssertionError ae)  {
			log.error("GET exception: " + ae.getMessage());
			log.error(CustomRequestFilter.publishResponse());
			throw ae;
		}
	}

	public <T extends BasePayload> Response put(String uri, T body)  {
		log.info("PUT request called, user:" + username + " " + uri);

		RequestSpecification requestSpec;
		if (body == null) {
			requestSpec = given()
					        .filter(crf)
					        .spec(requestSpecBuilder.build());
		} else {
			requestSpec = given()
			                .filter(crf)
					        .spec(requestSpecBuilder.build())
					        .body(body);
		}

		try {
			return
					requestSpec.
					expect()
					  .spec(responseSpecBuilder.build()).
					when()
					  .put(uri);
		} catch (Exception | AssertionError ae)  {
			log.error("PUT exception: " + ae.getMessage());
			log.error(CustomRequestFilter.publishResponse());
			throw ae;
		}
	}


	public <T extends BasePayload> Response post(String uri, T body)  {
		log.info("POST request called, user:" + username + " " + uri);

		RequestSpecification requestSpec;
		if (body == null) {
			requestSpec = given()
			        		.filter(crf)
					        .spec(requestSpecBuilder.build());
		} else {
			requestSpec = given()
	        				.filter(crf)
					        .spec(requestSpecBuilder.build())
					        .body(body);
		}

		try {
			return
					requestSpec.
					expect()
				  	  .spec(responseSpecBuilder.build()).
					when()
					  .post(uri);
		} catch (Exception | AssertionError ae)  {
			log.error("POST exception: " + ae.getMessage());
			log.error(CustomRequestFilter.publishResponse());
			throw ae;
		}
	}

	public Response delete(String uri) {
		log.info("Delete request called, user:" + username + " " + uri);
		try {
			return
					given()
					  .filter(crf)
					  .spec(requestSpecBuilder.build()).
					expect()
					  .spec(responseSpecBuilder.build()).
					when()
					  .delete(uri);
		} catch (Exception | AssertionError ae)  {
			log.error("Delete exception: " + ae.getMessage());
			log.error(CustomRequestFilter.publishResponse());
			throw ae;
		}
	}

	public void assertStatusCode(Response resp, int statusCode, String assertMsg) {
		if (resp.getStatusCode() != statusCode)  {
			log.error(CustomRequestFilter.publishResponse());
		}
		cnxAssert.assertEquals(resp.getStatusCode(), statusCode, assertMsg);
	}


}
