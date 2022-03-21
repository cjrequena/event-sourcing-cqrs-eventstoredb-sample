package com.cjrequena.sample.web.controller;

import com.cjrequena.sample.command.CreateBankAccountCommand;
import com.cjrequena.sample.command.DepositBankAccountCommand;
import com.cjrequena.sample.command.WithdrawBankAccountCommand;
import com.cjrequena.sample.dto.BankAccountDTO;
import com.cjrequena.sample.dto.DepositBankAccountDTO;
import com.cjrequena.sample.dto.WithdrawBankAccountDTO;
import com.cjrequena.sample.exception.controller.BadRequestControllerException;
import com.cjrequena.sample.exception.controller.ConflictControllerException;
import com.cjrequena.sample.exception.controller.NotFoundControllerException;
import com.cjrequena.sample.exception.service.AggregateNotFoundServiceException;
import com.cjrequena.sample.exception.service.BankAccountServiceException;
import com.cjrequena.sample.exception.service.DuplicatedAggregateServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.service.BankAccountCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static org.apache.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@SuppressWarnings("unchecked")
@Slf4j
@RestController
@RequestMapping(value = "/command-handler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankAccountCommandHandlerController {

  private final BankAccountCommandService bankAccountCommandService;

  @Operation(
    summary = "Command for new bank account creation ",
    description = "Command for new bank account creation ",
    parameters = {
      @Parameter(
        name = "accept-version",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(
          name = "accept-version",
          type = "string",
          allowableValues = {VND_SAMPLE_SERVICE_V1}
        )
      ),
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BankAccountDTO.class)))
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "202", description = "Accepted - The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "409", description = "Conflict - The request could not be processed because of conflict in the request"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't create the resource. Please try again."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
    }
  )
  @PostMapping(
    path = "/bank-accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(@Parameter @Valid @RequestBody BankAccountDTO dto, BindingResult bindingResult, HttpServletRequest request, UriComponentsBuilder ucBuilder)
    throws NotFoundControllerException, BadRequestControllerException, ConflictControllerException {
    try {
      CreateBankAccountCommand createBankAccountCommand = CreateBankAccountCommand.builder().bankAccountDTO(dto).build();
      this.bankAccountCommandService.handler(createBankAccountCommand);
      // Headers
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      headers.set("account-id", createBankAccountCommand.getData().getId().toString());
      return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }catch (DuplicatedAggregateServiceException ex) {
      throw new ConflictControllerException(ex.getMessage());
    } catch (AggregateNotFoundServiceException ex) {
      throw new NotFoundControllerException(ex.getMessage());
    } catch (OptimisticConcurrencyServiceException | BankAccountServiceException ex) {
      throw new BadRequestControllerException(ex.getMessage());
    }
  }

  @Operation(
    summary = "Command for bank account deposit operation ",
    description = "Command for bank account deposit operation ",
    parameters = {
      @Parameter(
        name = "accept-version",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(
          name = "accept-version",
          type = "string", allowableValues = {VND_SAMPLE_SERVICE_V1}
        )
      ),
      @Parameter(
        name = "aggregate-version",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(
          name = "aggregate-version",
          type = "number"
        )
      )
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DepositBankAccountDTO.class)))
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "202", description = "Accepted - The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "409", description = "Conflict - The request could not be processed because of conflict in the request"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't create the resource. Please try again."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
    }
  )
  @PostMapping(path = "/bank-accounts/deposit", produces = {APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deposit(@RequestBody DepositBankAccountDTO dto, @RequestHeader("aggregate-version") Long version, HttpServletRequest request, BindingResult bindingResult, UriComponentsBuilder ucBuilder)
    throws NotFoundControllerException, BadRequestControllerException, ConflictControllerException {
    try {
      DepositBankAccountCommand command = DepositBankAccountCommand.builder().depositBankAccountDTO(dto).version(version).build();
      this.bankAccountCommandService.handler(command);
      // Headers
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    } catch (DuplicatedAggregateServiceException ex) {
      throw new ConflictControllerException(ex.getMessage());
    }catch (AggregateNotFoundServiceException ex) {
      throw new NotFoundControllerException(ex.getMessage());
    } catch (OptimisticConcurrencyServiceException | BankAccountServiceException ex) {
      throw new BadRequestControllerException(ex.getMessage());
    }
  }

  @Operation(
    summary = "Command for bank account withdraw operation ",
    description = "Command for bank account withdraw operation ",
    parameters = {
      @Parameter(name = "accept-version", required = true, in = ParameterIn.HEADER, schema = @Schema(name = "accept-version", type = "string", allowableValues = {
        VND_SAMPLE_SERVICE_V1})),
      @Parameter(name = "aggregate-version", required = true, in = ParameterIn.HEADER, schema = @Schema(name = "aggregate-version", type = "number"))
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WithdrawBankAccountDTO.class)))
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "202", description = "Accepted - The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "409", description = "Conflict - The request could not be processed because of conflict in the request"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't create the resource. Please try again."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
    }
  )
  @PostMapping(path = "/bank-accounts/withdraw", produces = {APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> withdraw(@RequestBody WithdrawBankAccountDTO dto, @RequestHeader("aggregate-version") Long version, HttpServletRequest request, BindingResult bindingResult,UriComponentsBuilder ucBuilder)
    throws NotFoundControllerException, BadRequestControllerException, ConflictControllerException {
    try {
      WithdrawBankAccountCommand command = WithdrawBankAccountCommand.builder().withdrawBankAccountDTO(dto).version(version).build();
      this.bankAccountCommandService.handler(command);
      // Headers
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    } catch (DuplicatedAggregateServiceException ex) {
      throw new ConflictControllerException(ex.getMessage());
    }catch (AggregateNotFoundServiceException ex) {
      throw new NotFoundControllerException(ex.getMessage());
    } catch (OptimisticConcurrencyServiceException | BankAccountServiceException ex) {
      throw new BadRequestControllerException(ex.getMessage());
    }
  }
}
