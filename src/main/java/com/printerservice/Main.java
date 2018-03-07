package com.printerservice;

import com.printerservice.service.SnmpManager;
import com.printerservice.service.impl.SnmpManagerImpl;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        //ipAddress = "153.19.121.167"
        //port = "161"
        //mib = "1.3.6.1.2.1.1.1.0"
        //community public
        //TODO: delete the above comments

        SnmpManager snmpManager = new SnmpManagerImpl("153.19.121.167", "161", "1.3.6.1.2.1.1.1.0", "public");

    }
}
