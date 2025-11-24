package co.zw.blexta.checkmate.common.dto;

import lombok.Builder;
import lombok.Data;

public record LoginDto (
	 String email,
	 String password
        ) {}