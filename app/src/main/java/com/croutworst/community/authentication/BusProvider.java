package com.croutworst.community.authentication;

import com.squareup.otto.Bus;

/**
 * Created by adamc on 1/21/17.
 */

public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance(){
        return BUS;
    }

    public BusProvider(){}
}
