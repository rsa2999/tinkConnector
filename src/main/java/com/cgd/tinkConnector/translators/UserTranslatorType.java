package com.cgd.tinkConnector.translators;

public enum UserTranslatorType {


    PCE_SINGLE_USER(1), LOCAL_SUBSCRIPTION(2), PCE_SUSCRIPTIONS_SYNC(3), TEST_USERS_ONLY(4);

    private final long code;

    UserTranslatorType(long codei) {

        code = codei;
    }

    public long getCode() {
        return code;
    }

    public static UserTranslatorType fromValue(short value) {
        if (value == 0) {
            return null;
        }
        for (UserTranslatorType type : UserTranslatorType.values()) {
            if (type.getCode() == value) {
                return type;
            }
        }
        return null;
    }

}
