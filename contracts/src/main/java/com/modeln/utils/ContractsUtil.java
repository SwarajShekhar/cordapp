package com.modeln.utils;

import net.corda.core.identity.Party;

public class ContractsUtil {

    private static String MANUFACTURER = "manufacturer";
    private static String WHOLESALER = "wholesaler";
    private static String GPO = "gpo";
    private static String MODELN = "modeln";
    private static String HOSPITAL = "hospital";
    private static String ORACLE = "oracle";

    public static boolean isManufacturer(Party party){
        return party.getName().getOrganisationUnit().equals(MANUFACTURER);
    }

    public static boolean isWholesaler(Party party){
        return party.getName().getOrganisationUnit().equals(WHOLESALER);
    }

    public static boolean isGPO(Party party){
        return party.getName().getOrganisationUnit().equals(GPO);
    }

    public static boolean isModelN(Party party){
        return party.getName().getOrganisationUnit().equals(MODELN);
    }

    public static boolean isOracle(Party party){
        return party.getName().getOrganisationUnit().equals(ORACLE);
    }

    public static boolean isHospital(Party party){
        return party.getName().getOrganisationUnit().equals(HOSPITAL);
    }
}
