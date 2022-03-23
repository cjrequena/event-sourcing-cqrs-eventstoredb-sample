package com.cjrequena.sample.web.controller;

import com.cjrequena.sample.dto.BankAccountDTO;
import com.cjrequena.sample.exception.controller.NotFoundControllerException;
import com.cjrequena.sample.exception.service.BankAccountNotFoundServiceException;
import com.cjrequena.sample.service.BankAccountQueryHandlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Slf4j
@RestController
@RequestMapping(value = "/query-handler")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BankAccountQueryHandlerController {

  private final BankAccountQueryHandlerService bankAccountQueryService;

  @Operation(
    summary = "Retrieve bank account by account_id ",
    description = "Retrieve bank account by account_id ",
    parameters = {
      @Parameter(name = "accept-version", required = true, in = ParameterIn.HEADER, schema = @Schema(name = "accept-version", type = "string", allowableValues = {VND_SAMPLE_SERVICE_V1}))
    }
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Created - The request was successful, we created a new resource and the response body contains the representation."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "409", description = "Conflict - The request could not be processed because of conflict in the request"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't create the resource. Please try again."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
    }
  )
  @GetMapping(path = "/bank-accounts/{accountId}", produces = {APPLICATION_JSON_VALUE})
  public BankAccountDTO retrieveById(@PathVariable("accountId") UUID accountId) throws NotFoundControllerException {
    try {
      return this.bankAccountQueryService.retrieveById(accountId);
    } catch (BankAccountNotFoundServiceException ex) {
      throw new NotFoundControllerException();
    }
  }

  @Operation(
    summary = "Get a list of fooes.",
    description = "Get a list of fooes.",
    parameters = {@Parameter(name = "accept-version", required = true, in = ParameterIn.HEADER, schema = @Schema(name = "accept-version", type = "string", allowableValues = {
      VND_SAMPLE_SERVICE_V1}))}
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "OK - The request was successful and the response body contains the representation requested."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the GET failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't return the representation due to an internal server error."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable to return the representation. Please wait for a bit and try again."),
    }
  )
  @GetMapping(
    path = "/bank-accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<List<BankAccountDTO>> retrieve()  {
      final List<BankAccountDTO> bankAccountDTOList = this.bankAccountQueryService.retrieve();
      HttpHeaders responseHeaders = new HttpHeaders();
      return new ResponseEntity<>(bankAccountDTOList, responseHeaders, HttpStatus.OK);
  }
}
