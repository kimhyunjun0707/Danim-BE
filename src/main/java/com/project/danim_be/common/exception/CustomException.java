package com.project.danim_be.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public class CustomException extends RuntimeException{

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getDetail());
		this.errorCode = errorCode;
	}
}
