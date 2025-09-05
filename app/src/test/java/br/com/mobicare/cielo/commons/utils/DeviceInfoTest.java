package br.com.mobicare.cielo.commons.utils;


import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by benhur.souza on 07/04/2017.
 */

public class DeviceInfoTest {

    @Test
    public void deviceID_isCorrect() throws Exception {
        assertNotNull(DeviceInfo.getInstance().getDeviceId());
    }


}
