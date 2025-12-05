package co.zw.blexta.checkmate.common.dto;

public record ChangePasswordDto(
		String oldPassword,
		String newPassword,
		String confirmPassword
		) {

}
