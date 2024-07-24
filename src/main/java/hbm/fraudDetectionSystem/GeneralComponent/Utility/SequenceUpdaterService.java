package hbm.fraudDetectionSystem.GeneralComponent.Utility;

import java.util.concurrent.atomic.AtomicLong;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.LocalSequences.INCREMENT_UTRNNO;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.LocalSequences.UTRNNO;

public class SequenceUpdaterService {
    private static AtomicLong seq = new AtomicLong(UTRNNO);

    public static synchronized long nextSeq() {
        return seq.addAndGet(INCREMENT_UTRNNO);
    }
}
