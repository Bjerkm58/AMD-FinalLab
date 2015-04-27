package com.example.vellaj48.myapplication;

import android.provider.BaseColumns;

/**
 * Created by vellaj48 on 3/30/2015.
 */
public final class NumberContract {
    //prevents someone from instantiating this class
    public NumberContract(){}

    /* By implementing the BaseColumns interface, your inner class can inherit a primary key field
    called _ID that some Android classes such as cursor adaptors will expect it to have. It's not
    required, but this can help your database work harmoniously with the Android framework.
     This defines contents of 1 table. Makes it easy to change tables/columns
     without having to find where it is everywhere in your code.
     */
    public static abstract class NumberEntry implements BaseColumns{
        public static final String USER_TABLE_NAME = "Users";
        public static final String COLUMN_NAME_ID = "Username";
        public static final String COLUMN_NAME_VALUE = "Password";

    }

    public static abstract class VariableEntry implements BaseColumns{
        public static final String VARIABLE_TABLE_NAME = "variable_table";
        public static final String VARIABLE_NAME = "variableNames";
        public static final String VARIABLE_EQUATION = "variableEquations";
        public static final String VARIABLE_CURRENT_USER = "variable_current_user";

    }

    public static abstract class EquationEntry implements BaseColumns{
        public static final String EQUATION_TABLE_NAME = "equations";
        public static final String EQUATION_CURRENT_USER = "equation_current_user";
        public static final String SAVED_EQUATION_ONE = "equation1";
        public static final String SAVED_EQUATION_ONE_NAME = "equation1_name";
        public static final String SAVED_EQUATION_TWO = "equation2";
        public static final String SAVED_EQUATION_TWO_NAME  = "equation2_name";
        public static final String SAVED_EQUATION_THREE = "equation3";
        public static final String SAVED_EQUATION_THREE_NAME  = "equation3_name";
        // SELECT equations, equation_current_user, equation1, equation1_name, equation2, equation2_name, equation3, equation3_name FROM equations
    }
}
