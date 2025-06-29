package tech.vat78.orchestrators.temporal.activity;

import io.temporal.activity.ActivityInterface;
import tech.vat78.orchestrators.core.eod.dto.EodContext;

@ActivityInterface
public interface EodDayActivity {
    EodContext startEod() throws Exception;
    void switchOpenDay(EodContext context);
}
