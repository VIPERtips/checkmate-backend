package co.zw.blexta.checkmate.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyTrendDto {
    private String month;
    private long checkouts;
    private long checkins;
}
