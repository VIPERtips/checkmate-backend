package co.zw.blexta.checkmate.common.dto;

public record CompanyRequestDto (
		String name,
		String phoneNumber,
		String address,
		String email
		) {}