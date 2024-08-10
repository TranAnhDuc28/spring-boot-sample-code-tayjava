package com.demo.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle exception when validate data
     * + MethodArgumentNotValidException =>  Failed to convert value
     * + ConstraintViolationException => Invalid PathVariable
     * + MissingServletRequestParameterException => request parameter bắt buộc không được cung cấp trong HTTP request
     *
     * @param e
     * @param request
     * @return errorResponse
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when the data invalid. (@RequestBody, @RequestParam, @PathVariable)",
                                    summary = "Handle Bad request",
                                    value = """
                                            {
                                                "timestamp": "2024-05-16T17:52:43.514+00:00",
                                                "status": 400,
                                                "path": "/user/",
                                                "error": "Invalid Payload",
                                                "message": "{field} must be not blank"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        System.out.println("==========> HandlerValidatorException");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[") + 1;
            int end = message.lastIndexOf("]") - 1;
            errorResponse.setMessage(message.substring(start, end));
            errorResponse.setError("Invalid Payload");
        } else if (e instanceof ConstraintViolationException) {
            errorResponse.setMessage(message.substring(message.indexOf(" ") + 1));
            errorResponse.setError("Invalid PathVariable");
        } else if (e instanceof MissingServletRequestParameterException) {
            errorResponse.setMessage(message);
            errorResponse.setError("Invalid Parameter");
        } else {
            errorResponse.setMessage(message);
            errorResponse.setError("Invalid Data");
        }

        return errorResponse;
    }


    /**
     * Handle exception when the request not found data
     *
     * @param e
     * @param request
     * @return errorResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                            {
                                            "timestamp": "2023-10-19T06:07:35.321+00:00",
                                            "status": 404,
                                            "path": "/api/v1/...",
                                            "error": "Not Found",
                                            "message": "{data} not found"
                                            }

                                            """
                            ))})
    })
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(NOT_FOUND.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    /**
     * Handle exception when the data is conflicted
     *
     * @param e
     * @param request
     * @return errorResponse
     */
    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(CONFLICT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
                    name = "409 Response",
                    summary = "Handle exception when input data is conflicted",
                    value = """
                            {
                            "timestamp": "2023-10-19T06:07:35.321+00:00",
                            "status": 409,
                            "path": "/api/v1/...",
                            "error": "Conflict",
                            "message": "{data} exists, Please try again!"
                            }
                            """
            ))})
    })
    public ErrorResponse handleInvalidDataException(InvalidDataException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(CONFLICT.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(CONFLICT.getReasonPhrase());
        errorResponse.setError(e.getMessage());
        return errorResponse;
    }


    /**
     * Handle exception when internal server error
     *
     * @param e
     * @param request
     * @return errorResponse
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "500 Reponse",
                                    summary = "Handle exception when internal server error",
                                    value = """
                                             {
                                             "timestamp": "2023-10-19T06:35:52.333+00:00",
                                             "status": 500,
                                             "path": "/api/v1/...",
                                             "error": "Internal Server Error",
                                             "message": "Connection timeout, please try again"
                                             }
                                            """
                            ))})
    })
    public ErrorResponse handleInterbalServerErrorException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }
}
