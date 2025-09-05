package br.com.mobicare.cielo.commons.utils.TimeUtilsTest;

import org.junit.Test;

import br.com.mobicare.cielo.BuildConfig;
import br.com.mobicare.cielo.commons.utils.TimeUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by benhur.souza on 07/04/2017.
 */

public class TimeUtilsTest {

    @Test
    public void needSync10Minutes() throws Exception {
        if(BuildConfig.DEBUG) {

            assertTrue(TimeUtils.needSync(
                    TimeRobot.nowAddMinutes(10)
            ));

        }else{

            assertFalse(TimeUtils.needSync(
                    TimeRobot.nowAddMinutes(10)
            ));
        }
    }


    @Test
    public void needSync1Day() throws Exception {
        assertTrue(TimeUtils.needSync(
                TimeRobot.nowAddDay(1)
        ));
    }

    @Test
    public void needSync2Days() throws Exception {
        assertTrue(TimeUtils.needSync(
                TimeRobot.nowAddDay(2)
        ));
    }

    @Test
    public void needSync() throws Exception {
        assertFalse(TimeUtils.needSync(
                TimeRobot.now()
        ));
    }

    @Test
    public void needSync0() throws Exception {
        assertTrue(TimeUtils.needSync(0));
    }
}
