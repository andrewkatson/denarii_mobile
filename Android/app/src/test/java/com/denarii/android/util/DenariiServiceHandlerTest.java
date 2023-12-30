package com.denarii.android.util;

import static org.junit.Assert.assertEquals;

import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.network.StubbedDenariiService;

import org.junit.BeforeClass;
import org.junit.Test;

public class DenariiServiceHandlerTest {

    @BeforeClass
    public static void beforeClass() {
        Constants.DEBUG = true;
    }

    @Test
    public void getInstance_getsTestInstance() {
        DenariiService denariiService = DenariiServiceHandler.returnDenariiService();

        assertEquals(denariiService.getClass(), StubbedDenariiService.class);

        assertEquals(denariiService, DenariiServiceHandler.returnDenariiService());
    }

}
