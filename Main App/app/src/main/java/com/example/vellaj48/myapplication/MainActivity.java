package com.example.vellaj48.myapplication;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;
import java.util.StringTokenizer;
import java.lang.*;



public class MainActivity extends ActionBarActivity {
    public final static String MY_MSG = "com.example.erik.myfirstapp.MESSAGE";

    public boolean decimalUsed = false;
    public boolean numbersRequired = false;  // used only for after decimal used
    public boolean operatorRequired = false;  // used only for after right parentheses ")"
    public boolean operatorUsed = true;
    public boolean variableUsed = false;
    public boolean creatingVariables = false; // used to diferentiate between where buttons are directed.
    public int leftParenthesesUsed = 0;
    public int decimalWasUsed = 0;

    public String savedEquation = "";
    public String currentUserId = "";

    public boolean[] saveBooleanStates = new boolean[5]; // saves the states of boolean values when we move to create variables.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*  // to load up buttons fragment
        ButtonsFragment calculatorButtons = new ButtonsFragment();
        FragmentTransaction calc = getFragmentManager()
                .beginTransaction();
        //add frag Fragment to view
        calc.add(R.id.button_Frame, calculatorButtons);
        calc.commit();
        */

        loginFragment calculatorButtons = new loginFragment();
        FragmentTransaction calc = getFragmentManager()
                .beginTransaction();
        //add frag Fragment to view
        calc.add(R.id.button_Frame, calculatorButtons);
        calc.commit();


        EquationFragment equation = new EquationFragment();
        FragmentTransaction f = getFragmentManager()
                .beginTransaction();
        //add frag Fragment to view
        f.add(R.id.equation_Frame, equation);
        f.commit();

        favoriteEquations favEquation = new favoriteEquations();
        FragmentTransaction favEq = getFragmentManager()
                .beginTransaction();
        //add frag Fragment to view
        favEq.add(R.id.favorite_equation_frame, favEquation);
        favEq.commit();
    }

    public void addNumbers(View v) {

        if (operatorRequired == false && variableUsed==false) {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            String mathExpression = "";

            if(creatingVariables == false)
            {
                EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
                mathExpression = expression.getText();
                Button button = (Button) findViewById(v.getId());
                String buttonName = button.getText().toString();

                mathExpression = mathExpression + buttonName;
                operatorUsed = false;
                numbersRequired = false;
                if(leftParenthesesUsed ==0)
                {
                    // makes the equal button clickable
                    Button greySavedEquations = (Button) findViewById(R.id.button7);
                    greySavedEquations.setEnabled(true);
                }
                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(mathExpression);
            }
            else
            {
                createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);
                mathExpression = expression.getTextVariable();
                Button button = (Button) findViewById(v.getId());
                String buttonName = button.getText().toString();

                mathExpression = mathExpression + buttonName;
                operatorUsed = false;
                numbersRequired = false;

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquationVariable(mathExpression);
            }

        }
    }

    public void addDecimal(View v) {
        if (decimalUsed == false && variableUsed==false) {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            if (creatingVariables == false )
            {
                EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
                String mathExpression = expression.getText();

                Button button = (Button) findViewById(v.getId());
                String buttonName = button.getText().toString();
                mathExpression = mathExpression + buttonName;

                // makes the equal button clickable
                Button greySavedEquations = (Button)findViewById(R.id.button7);
                greySavedEquations.setEnabled(true);
                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(mathExpression);

                // updates misc variables preventing user error
                decimalUsed = true;
                numbersRequired = true;
                decimalWasUsed++;
            }
            else
            {
                createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);
                String mathExpression = expression.getTextVariable();

                Button button = (Button) findViewById(v.getId());
                String buttonName = button.getText().toString();
                mathExpression = mathExpression + buttonName;

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquationVariable(mathExpression);

                // updates misc variables preventing user error
                decimalUsed = true;
                numbersRequired = true;
                decimalWasUsed++;
            }
        }
    }

    public void addParentheses(View v) {
        if (creatingVariables == false)
        {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getText();

            // gets the character in the button pressed
            Button button = (Button) findViewById(v.getId());
            String buttonName = button.getText().toString();

            char parentheses = buttonName.charAt(0);
            if ((parentheses == '(' && operatorUsed == true) || (parentheses == '(' && mathExpression.length() == 0))
            // we are adding a left parentheses after an operation  ex: "9 + "
            // OR we are adding a parentheses to an empty equation ex: ""
            {
                // updates the expression
                mathExpression = mathExpression + buttonName + " ";

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(mathExpression);

                // updates misc variables preventing user error
                leftParenthesesUsed++;
                decimalUsed = false;
                operatorUsed = true;
            } else if (leftParenthesesUsed > 0 && operatorUsed == false) // (parentheses == ')')    aka right parentheses is used
            {
                // updates the expression
                mathExpression = mathExpression + " " + buttonName;

                // updates misc variables preventing user error
                decimalUsed = true;
                operatorRequired = true;
                leftParenthesesUsed--;


                if(leftParenthesesUsed ==0)
                {
                    // makes the equal button clickable
                    Button greySavedEquations = (Button) findViewById(R.id.button7);
                    greySavedEquations.setEnabled(true);
                }
                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(mathExpression);


            }
        }
        else // we are editing a different text field, use different stuff
        {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);  // changed
            String mathExpression = expression.getTextVariable(); // changed

            // gets the character in the button pressed
            Button button = (Button) findViewById(v.getId());
            String buttonName = button.getText().toString();

            char parentheses = buttonName.charAt(0);
            if ((parentheses == '(' && operatorUsed == true) || (parentheses == '(' && mathExpression.length() == 0))
            // we are adding a left parentheses after an operation  ex: "9 + "
            // OR we are adding a parentheses to an empty equation ex: ""
            {
                // updates the expression
                mathExpression = mathExpression + buttonName + " ";

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquationVariable(mathExpression); // changed

                // updates misc variables preventing user error
                leftParenthesesUsed++;
                decimalUsed = false;
                operatorUsed = true;
            } else if (leftParenthesesUsed > 0 && operatorUsed == false) // (parentheses == ')')    aka right parentheses is used
            {
                // updates the expression
                mathExpression = mathExpression + " " + buttonName;

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquationVariable(mathExpression); // changed

                // updates misc variables preventing user error
                leftParenthesesUsed--;
                decimalUsed = true;
                operatorRequired = true;
            }
        }
    }

    public void addOperators(View v) {
        if (operatorUsed == false) {
            if (creatingVariables == false)
            {
                // gets the editText containing the equation in the fragment fragment_equation.xml
                EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
                String mathExpression = expression.getText();

                // grabs the character in the button pressed
                Button button = (Button) findViewById(v.getId());
                String buttonName = button.getText().toString();

                // updates the equation string
                mathExpression = mathExpression + " " + buttonName + " ";

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(mathExpression);
                Button greySavedEquations = (Button)findViewById(R.id.button7);
                greySavedEquations.setEnabled(false);
                // updates misc variables preventing user error
                operatorUsed = true;
                decimalUsed = false;
                operatorRequired = false;
                variableUsed = false;
            }
            else
            {
                // gets the editText containing the equation in the fragment fragment_equation.xml
                createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);
                String mathExpression = expression.getTextVariable();

                // grabs the character in the button pressed
                Button button = (Button) findViewById(v.getId());
                String buttonName = button.getText().toString();

                // updates the equation string
                mathExpression = mathExpression + " " + buttonName + " ";

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquationVariable(mathExpression);

                // updates misc variables preventing user error
                operatorUsed = true;
                decimalUsed = false;
                operatorRequired = false;
                variableUsed = false;
            }
        }

    }

    public void addNewVariable(View v) {

        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase db = ndbh.getReadableDatabase();
        ContentValues cv = new ContentValues();

        String equation = ((EditText) findViewById(R.id.editText8)).getText().toString(); // equation
        String varName = ((EditText) findViewById(R.id.editText7)).getText().toString(); // variable name

        String[] projection = {
                NumberContract.VariableEntry.VARIABLE_NAME,
                NumberContract.VariableEntry.VARIABLE_EQUATION,
                NumberContract.VariableEntry.VARIABLE_CURRENT_USER,
        };

        //SELECT * FROM numbers
        Cursor s = db.query(
                NumberContract.VariableEntry.VARIABLE_TABLE_NAME,
                projection,
                null,  //String
                null,  //String[]
                null,
                null,
                null,
                null
        );

        s.moveToFirst();
        String display = "", DBequation = "",currentUser = "";
        boolean foundVarName = false, foundEquation = false;

        while (!s.isAfterLast()) {
            display = s.getString(
                    s.getColumnIndexOrThrow(
                            NumberContract.VariableEntry.VARIABLE_NAME));
            DBequation = s.getString(
                    s.getColumnIndexOrThrow(
                            NumberContract.VariableEntry.VARIABLE_EQUATION));
            currentUser = s.getString(
                    s.getColumnIndexOrThrow(
                            NumberContract.VariableEntry.VARIABLE_CURRENT_USER));
            if(currentUser.equalsIgnoreCase(currentUserId))
            {
                if (display.equalsIgnoreCase(varName)) // does this variable name exist?
                {
                    foundVarName = true;
                    break;
                }
                if (DBequation.equalsIgnoreCase(equation)) // does equation already exist?
                {
                    foundEquation = true;
                    break;
                }
            }
            //else do nothing move to the new users.
            s.moveToNext();
        }
        if (foundVarName == true) {
            Toast.makeText(getApplication(), "Variable Name already exists", Toast.LENGTH_SHORT).show();
        }
        if (foundEquation == true) {
            Toast.makeText(getApplication(), "Equation already exists", Toast.LENGTH_SHORT).show();
        }
        if (foundVarName == false && foundEquation == false) {
            // re-enables greyed out buttons
            Button greyCreateNewVariableButton = (Button)findViewById(R.id.button29);
            greyCreateNewVariableButton.setEnabled(true);
            Button greyEqualsButton = (Button)findViewById(R.id.button7);
            greyEqualsButton.setEnabled(true);
            Button greyUseVariablesButton = (Button)findViewById(R.id.useSaved);
            greyUseVariablesButton.setEnabled(true);
            Button greySavedEquations = (Button)findViewById(R.id.button28);
            greySavedEquations.setEnabled(true);
            // restores all boolean values as we are going back to the calculator

            decimalUsed = saveBooleanStates[0];
            numbersRequired =saveBooleanStates[1];
            operatorRequired = saveBooleanStates[2];
            operatorUsed = saveBooleanStates[3];
            variableUsed =  saveBooleanStates[4];

            String defaultSavedEquations = "SavedEquationSlot";
            //creating the database
            cv.put(NumberContract.VariableEntry.VARIABLE_NAME, varName);
            // could also be written cv.put("username", id);
            cv.put(NumberContract.VariableEntry.VARIABLE_EQUATION, equation);
            cv.put(NumberContract.VariableEntry.VARIABLE_CURRENT_USER, currentUserId);
            //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
            //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
            sdb.insert(NumberContract.VariableEntry.VARIABLE_TABLE_NAME, "null", cv);
            sdb.close();

            EquationFragment showMyButtons = new EquationFragment();

            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.equation_Frame)).commit();
            getFragmentManager().beginTransaction().add(R.id.equation_Frame, showMyButtons).commit();
            creatingVariables = false;

            // waits for the commits to execute BEFORE trying to do stuff to the newly loaded fragments text.
            getFragmentManager().executePendingTransactions();
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            expression.setEquation(savedEquation);
        }
        foundEquation = false;
        foundVarName = false;
        //((TextView)findViewById(R.id.dbValues)).setText(display);

    }

    public void backSpace(View v) {
        if (creatingVariables == false) {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getText();

            char leftParenthesesCheck = ' ';
            if (mathExpression.length() > 0) // if we actually have an expression
            {
                char getDecimalOrOperator = mathExpression.charAt(mathExpression.length() - 1);
                if (mathExpression.length() > 1) // if our string is longer than 2 characters, meaning there is not a single number ex "3"
                {
                    // grab the 2nd last digit to check if its a parentheses later in the code
                    leftParenthesesCheck = mathExpression.charAt(mathExpression.length() - 2);
                }
                if (getDecimalOrOperator == '.')  // removes decimals
                {
                    // updates the equation
                    String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);

                    // sets the new string to the equation editText in fragment_equation.xml
                    expression.setEquation(expressionMinusOne);

                    // updates misc variables preventing user error
                    decimalUsed = false;
                    operatorUsed = false;
                    numbersRequired = false;
                    decimalWasUsed--;
                } else if (getDecimalOrOperator == ' ') // if we have a blank space we are reading a parentheses or an operator
                {
                    if (leftParenthesesCheck == '(' && mathExpression.length() > 2)  // all expressions NOT starting with "(" ex "8 + ( 9 )"
                    {
                        // reduces the total number counter of left parentheses used
                        leftParenthesesUsed--;
                        if(leftParenthesesUsed ==0)
                        {
                            // makes the equal button clickable
                            Button greySavedEquations = (Button) findViewById(R.id.button7);
                            greySavedEquations.setEnabled(true);
                        }
                        // updates the equation
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 2);

                        // sets the new equation to the editText in fragment_equation.xml
                        expression.setEquation(expressionMinusOne);

                    } else if (leftParenthesesCheck == '(' && mathExpression.length() == 2) // if our first character is a "(" ex "("
                    {
                        leftParenthesesUsed--;
                        if(leftParenthesesUsed ==0)
                        {
                            // makes the equal button clickable
                            Button greySavedEquations = (Button) findViewById(R.id.button7);
                            greySavedEquations.setEnabled(true);
                        }
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 2);
                        expression.setEquation(expressionMinusOne);

                    } else // reading an operator ex "% * + - /"
                    {
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 3);
                        expression.setEquation(expressionMinusOne);
                        operatorUsed = false;
                        if(leftParenthesesUsed ==0)
                        {
                            // makes the equal button clickable
                            Button greySavedEquations = (Button) findViewById(R.id.button7);
                            greySavedEquations.setEnabled(true);
                        }
                    }
                } else if (getDecimalOrOperator == ')') // if we are reading a right parentheses
                {
                    // adds to the number of left parentheses used because we now have 1 less right parentheses
                    leftParenthesesUsed++;
                    operatorRequired = false;

                    // makes the equal button clickable
                    Button greySavedEquations = (Button) findViewById(R.id.button7);
                    greySavedEquations.setEnabled(false);

                    String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 2);
                    expression.setEquation(expressionMinusOne);
                } else // otherwise we are reading a number
                {
                    if (leftParenthesesCheck == ' ')// if there is an empty space we are done with numbers
                    {
                        operatorUsed = true;
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);
                        expression.setEquation(expressionMinusOne);
                    } else {
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);
                        expression.setEquation(expressionMinusOne);
                        operatorUsed = false;
                    }
                }
            }
        }
        else
        {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getTextVariable();

            char leftParenthesesCheck = ' ';
            if (mathExpression.length() > 0) // if we actually have an expression
            {
                char getDecimalOrOperator = mathExpression.charAt(mathExpression.length() - 1);
                if (mathExpression.length() > 1) // if our string is longer than 2 characters, meaning there is not a single number ex "3"
                {
                    // grab the 2nd last digit to check if its a parentheses later in the code
                    leftParenthesesCheck = mathExpression.charAt(mathExpression.length() - 2);
                }
                if (getDecimalOrOperator == '.')  // removes decimals
                {
                    // updates the equation
                    String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);

                    // sets the new string to the equation editText in fragment_equation.xml
                    expression.setEquationVariable(expressionMinusOne);

                    // updates misc variables preventing user error
                    decimalUsed = false;
                    operatorUsed = false;
                    numbersRequired = false;
                    decimalWasUsed--;
                } else if (getDecimalOrOperator == ' ') // if we have a blank space we are reading a parentheses or an operator
                {
                    if (leftParenthesesCheck == '(' && mathExpression.length() > 2)  // all expressions NOT starting with "(" ex "8 + ( 9 )"
                    {
                        // reduces the total number counter of left parentheses used
                        leftParenthesesUsed--;

                        // updates the equation
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 3);

                        // sets the new equation to the editText in fragment_equation.xml
                        expression.setEquationVariable(expressionMinusOne);

                    } else if (leftParenthesesCheck == '(' && mathExpression.length() == 2) // if our first character is a "(" ex "("
                    {
                        leftParenthesesUsed--;
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 2);
                        expression.setEquationVariable(expressionMinusOne);

                    } else // reading an operator ex "% * + - /"
                    {
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 3);
                        expression.setEquationVariable(expressionMinusOne);
                        operatorUsed = false;
                    }
                } else if (getDecimalOrOperator == ')') // if we are reading a right parentheses
                {
                    // adds to the number of left parentheses used because we now have 1 less right parentheses
                    leftParenthesesUsed++;
                    operatorRequired = false;

                    String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);
                    expression.setEquationVariable(expressionMinusOne);
                } else // otherwise we are reading a number
                {
                    if (leftParenthesesCheck == ' ')// if there is an empty space we are done with numbers
                    {
                        operatorUsed = true;
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);
                        expression.setEquationVariable(expressionMinusOne);
                    } else {
                        String expressionMinusOne = mathExpression.substring(0, mathExpression.length() - 1);
                        expression.setEquationVariable(expressionMinusOne);
                        operatorUsed = false;
                    }
                }
            }
        }
    }
    public void makeNegative(View v) {
        // gets the editText containing the equation in the fragment fragment_equation.xml
        if (creatingVariables == false) {
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getText();


            // if we actually have something to make negative
            if (mathExpression.length() > 0) {
                String[] elements = mathExpression.split("\\s+"); // delimit by whitespace throw into an array

                // if what we have is a digit
                if (Character.isDigit(elements[elements.length - 1].charAt(0))
                        || elements[elements.length - 1].charAt(0) == '.') {
                    elements[elements.length - 1] = "-" + elements[elements.length - 1];
                    mathExpression = elements[0];
                    for (int i = 1; i < elements.length; i++) {
                        mathExpression = mathExpression + " " + elements[i];
                    }
                }
                // if its a negative sign,
                else if (elements[elements.length - 1].charAt(0) == '-') {
                    if (elements[elements.length - 1].length() > 1) {
                        elements[elements.length - 1] = elements[elements.length - 1].substring(1);
                        mathExpression = elements[0];
                        for (int i = 1; i < elements.length; i++) {
                            mathExpression = mathExpression + " " + elements[i];
                        }
                    }

                }
                expression.setEquation(mathExpression);
            }
        }
        else {
            createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getTextVariable();

            // if we actually have something to make negative
            if (mathExpression.length() > 0) {
                String[] elements = mathExpression.split("\\s+"); // delimit by whitespace throw into an array

                // if what we have is a digit
                if (Character.isDigit(elements[elements.length - 1].charAt(0))
                        || elements[elements.length - 1].charAt(0) == '.') {
                    elements[elements.length - 1] = "-" + elements[elements.length - 1];
                    mathExpression = elements[0];
                    for (int i = 1; i < elements.length; i++) {
                        mathExpression = mathExpression + " " + elements[i];
                    }
                }
                // if its a negative sign,
                else if (elements[elements.length - 1].charAt(0) == '-') {
                    if (elements[elements.length - 1].length() > 1) {
                        elements[elements.length - 1] = elements[elements.length - 1].substring(1);
                        mathExpression = elements[0];
                        for (int i = 1; i < elements.length; i++) {
                            mathExpression = mathExpression + " " + elements[i];
                        }
                    }

                }
                expression.setEquationVariable(mathExpression);
            }
        }
    }

    public void clearExpression(View v) {
        if(creatingVariables == false) {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            expression.setEquation("");
            operatorUsed = true;
            decimalUsed = false;
            leftParenthesesUsed = 0;

            numbersRequired = false;  // used only for after decimal used
            operatorRequired = false;  // used only for after right parentheses ")"
            decimalWasUsed = 0;
            variableUsed = false;
        }
        else
        {
            createVariables expression = (createVariables) getFragmentManager().findFragmentById(R.id.equation_Frame);
            expression.setEquationVariable("");
            operatorUsed = true;
            decimalUsed = false;
            leftParenthesesUsed = 0;

            numbersRequired = false;  // used only for after decimal used
            operatorRequired = false;  // used only for after right parentheses ")"
            decimalWasUsed = 0;
            variableUsed = false;
        }
    }

    public void evaluate(View v) {
        // gets the editText containing the equation in the fragment fragment_equation.xml

        if(numbersRequired == false && leftParenthesesUsed == 0 && operatorUsed == false)
        {
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getText();

            String[] getEquation = mathExpression.split("\\s+");// tokenize mathExpression to delimit variables
            mathExpression = ""; // reset mathExpression to be re-created during for loop later
            //String newStuff = "";
            // set up database stuff
            NumberDBHelper n = new NumberDBHelper(getApplicationContext());
            SQLiteDatabase sdb = n.getWritableDatabase();
            NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
            SQLiteDatabase db = ndbh.getReadableDatabase();
            ContentValues cv = new ContentValues();

            for(int i=0; i<getEquation.length; i++)
            {
                if (!isNumeric(getEquation[i])) // if we find a variable
                {
                    String[] projection = {
                            NumberContract.VariableEntry.VARIABLE_NAME,
                            NumberContract.VariableEntry.VARIABLE_EQUATION,
                            NumberContract.VariableEntry.VARIABLE_CURRENT_USER,

                    };

                    //SELECT * FROM numbers
                    Cursor s = db.query(
                            NumberContract.VariableEntry.VARIABLE_TABLE_NAME,
                            projection,
                            null,  //String
                            null,  //String[]
                            null,
                            null,
                            null,
                            null
                    );

                    s.moveToFirst();
                    String display = "", DBequation = "";

                    while (!s.isAfterLast()) {
                        display = s.getString(
                                s.getColumnIndexOrThrow(
                                        NumberContract.VariableEntry.VARIABLE_NAME));
                        DBequation = s.getString(
                                s.getColumnIndexOrThrow(
                                        NumberContract.VariableEntry.VARIABLE_EQUATION));
                        if(getEquation[i].equalsIgnoreCase(display))
                        {
                            getEquation[i] = DBequation;
                        }
                        s.moveToNext();
                    }
                }
                mathExpression = mathExpression + " " + getEquation[i];
            }
            //Toast.makeText(getApplication(), newStuff + " || " + getEquation.length, Toast.LENGTH_SHORT).show();

            if (mathExpression.length() != 0) {
                String value = infixToPostfix(mathExpression);
                value = evalPostfix(value, mathExpression) + " ";
                decimalUsed = true;
                operatorUsed = false;
                leftParenthesesUsed = 0;
                showVariablesUser ="VARIABLES";
                //sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(value);
            }
        }
    }
    // returns true if a string requates to a numerical value
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public void intEval(View v) {
        // gets the editText containing the equation in the fragment fragment_equation.xml
        EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
        String mathExpression = expression.getText();

        if (decimalWasUsed == 0) {
            String value = infixToPostfix(mathExpression);
            value = evalPostfixInt(value, mathExpression) + "";
            decimalUsed = true;
            operatorUsed = false;
            leftParenthesesUsed = 0;

            // sets the new string to the equation editText in fragment_equation.xml


            expression.setEquation(mathExpression);
        }
    }

    public static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 5;
            case '*':
            case '/':
            case '%':
                return 10;
            case '(':
                return 15;
            case ')':
                return 20;
            default:
                throw new IllegalArgumentException("invalid operator");
        }
    }

    public static String infixToPostfix(String infix) {
        StringTokenizer tokenizer = new StringTokenizer(infix);
        String postfix = "";
        Stack opStack = new Stack();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            char c;
            if (token.length() > 1) {
                c = token.charAt(token.length() - 1);

                if (c == '.') // check if idiot used decimal inapropriatly EX: "9."
                {
                    c = token.charAt(0);
                }
                if (c == '-') // if the person was extra stupid EX: "-9."
                {
                    c = token.charAt(1);
                }
            } else {
                c = token.charAt(0);
            }
            if (Character.isDigit(c)) {
                postfix += (token + " ");
            } else {
                while (!opStack.empty()) {
                    char top = ((Character) opStack.peek()).charValue();
                    if (precedence(top) >= precedence(c)) {
                        if (precedence(top) == 20) {
                            while (precedence(top) > 15 && !opStack.empty()) {
                                if (!opStack.empty()) {
                                    opStack.pop();
                                    top = ((Character) opStack.pop()).charValue();
                                    postfix += (top + " ");
                                    opStack.pop();
                                }
                            }
                        } else if (precedence(top) == 15) {
                            break;
                        } else {
                            postfix += (top + " ");
                            opStack.pop();
                        }
                    } else {
                        break;
                    }
                }

                opStack.push(new Character(c));
            }
        }

        while (!opStack.empty()) {
            char top = ((Character) opStack.pop()).charValue();
            if (precedence(top) != 20 && precedence(top) != 15) {
                postfix += (top + " ");
            }
        }

        return postfix;
    }

    public String answer = "";

    public double evalPostfix(String postfix, String original) {
        StringTokenizer tokenizer = new StringTokenizer(postfix);
        Stack valStack = new Stack();
        // Intent i = new Intent(this, maths.class);
        String sendMsg = original + "\n";


        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            char c;
            if (token.length() > 1) {
                c = token.charAt(token.length() - 1);

                if (c == '.') // check if idiot used decimal inapropriatly EX: "9."
                {
                    c = token.charAt(0);
                }
                if (c == '-') // if the person was extra stupid EX: "-9."
                {
                    c = token.charAt(1);
                }
            } else {
                c = token.charAt(0);
            }

            if (Character.isDigit(c)) {
                valStack.push(new Double(token));
            } else {
                double rightVal = ((Double) valStack.pop()).doubleValue();
                double leftVal = ((Double) valStack.pop()).doubleValue();
                double rslt;

                switch (c) {
                    case '+':
                        rslt = leftVal + rightVal;
                        sendMsg = sendMsg + leftVal + " + " + rightVal + " = " + rslt + "\n";
                        break;
                    case '-':
                        rslt = leftVal - rightVal;
                        sendMsg = sendMsg + leftVal + " - " + rightVal + " = " + rslt + "\n";
                        break;
                    case '*':
                        rslt = leftVal * rightVal;
                        sendMsg = sendMsg + leftVal + " * " + rightVal + " = " + rslt + "\n";
                        break;
                    case '/':
                        rslt = leftVal / rightVal;
                        sendMsg = sendMsg + leftVal + " / " + rightVal + " = " + rslt + "\n";
                        break;
                    case '%':
                        rslt = leftVal % rightVal;
                        sendMsg = sendMsg + leftVal + " % " + rightVal + " = " + rslt + "\n";
                        break;
                    default:
                        throw new IllegalArgumentException("invalid postfix expression");
                }

                valStack.push(new Double(rslt));
            }
        }

        double rslt = ((Double) valStack.pop()).doubleValue();
        if (!valStack.empty()) {
            throw new IllegalArgumentException("invalid postfix expression");
        }

        sendMsg = sendMsg.toString() + " final result = " + rslt;

        showEquationSteps();

        //equationSteps showSteps = (equationSteps)getFragmentManager().findFragmentById(R.id.button_Frame);
        //showSteps.setEquationSteps(sendMsg);
        answer = sendMsg;


        return rslt;
    }

    public void showEquationSteps() {
        equationSteps steps = new equationSteps();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, steps).commit();
    }

    public void showButtons(View v) {
        ButtonsFragment showMyButtons = new ButtonsFragment();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();
    }

    public void showEquationSteps(View v) {  // used for showing equation steps button
        equationSteps expression = (equationSteps) getFragmentManager().findFragmentById(R.id.button_Frame);
        expression.setEquationSteps(answer);
    }

    public void showVariableCreatorFragment(View v)
    {
        createVariables showMyButtons = new createVariables();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.equation_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.equation_Frame, showMyButtons).commit();
    }


    public void createVariablesFragment(View v)
    {
        // greys out apropriate buttons
        Button greyCreateNewVariableButton = (Button)findViewById(R.id.button29);
        greyCreateNewVariableButton.setEnabled(false);
        Button greyEqualsButton = (Button)findViewById(R.id.button7);
        greyEqualsButton.setEnabled(false);
        Button greyUseVariablesButton = (Button)findViewById(R.id.useSaved);
        greyUseVariablesButton.setEnabled(false);
        Button greySavedEquations = (Button)findViewById(R.id.button28);
        greySavedEquations.setEnabled(false);

        // creats a Equationfragment
        EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
        // saves all boolean states
        saveBooleanStates[0] = decimalUsed;
        saveBooleanStates[1] = numbersRequired;
        saveBooleanStates[2] = operatorRequired;
        saveBooleanStates[3] = operatorUsed;
        saveBooleanStates[4] = variableUsed;

        // resets all boolean values for when we use the calculator when we create variables
        decimalUsed = false;
        numbersRequired = false;  // used only for after decimal used
        operatorRequired = false;  // used only for after right parentheses ")"
        operatorUsed = true;
        variableUsed = false;
        creatingVariables = true;
        savedEquation = expression.getText();

        // instantiates said Equationfragment created earlier
        showVariableCreatorFragment(v);



    }

    public void addVariables(View v)
    {
        if(variableUsed == false && operatorUsed == true) {
            // pull the variables out of the databse & display them on the page as buttons enclosed in parentheses
            // example: the equation 5/(7+2) has a variable name foo,
            // button looks like:  [ foo = (5/(7+2)) ] where the [] brackets are the button itself.
            // this way you can simpl
            // when a variable is selected it is put into main equation up top
            // then you are taken back to the main buttons page so that you can use the other buttons
            // will be graphically pleasing
            showVariables showMyButtons = new showVariables();

            TextView text =  (TextView) findViewById(R.id.textView12);
            String showVariablesUser =text.getText().toString();
            //Toast.makeText(getApplication(),showVariablesUser, Toast.LENGTH_SHORT).show();
            showVariablesUser =text.getText().toString();
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
            getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();
            // waits for the commits to execute BEFORE trying to do stuff to the newly loaded fragments text.
            getFragmentManager().executePendingTransactions();


            NumberDBHelper n = new NumberDBHelper(getApplicationContext());
            SQLiteDatabase sdb = n.getWritableDatabase();
            NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
            SQLiteDatabase db = ndbh.getReadableDatabase();
            ContentValues cv = new ContentValues();


            String[] projection = {
                    NumberContract.VariableEntry.VARIABLE_NAME,
                    NumberContract.VariableEntry.VARIABLE_EQUATION,
                    NumberContract.VariableEntry.VARIABLE_CURRENT_USER,
            };

            //SELECT * FROM numbers
            Cursor s = db.query(
                    NumberContract.VariableEntry.VARIABLE_TABLE_NAME,
                    projection,
                    null,  //String
                    null,  //String[]
                    null,
                    null,
                    null,
                    null
            );

            s.moveToFirst();
            String varName = "", equation = "", userId = "";

            while (!s.isAfterLast()) {
                varName = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.VariableEntry.VARIABLE_NAME));
                equation = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.VariableEntry.VARIABLE_EQUATION));
                userId = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.VariableEntry.VARIABLE_CURRENT_USER));

                if(userId.equalsIgnoreCase(currentUserId) )  // if the variable is one of the current users variables show it
                {
                    // Toast.makeText(getApplication(), userId + " : " + currentUserId, Toast.LENGTH_SHORT).show();

                    Button b = new Button(getApplicationContext());
                    b.setText(varName + " = " + equation );

                    b.setOnClickListener(getOnClickDoSomething(b));  // look at method below
                    ((LinearLayout) findViewById(R.id.showMyVariables)).addView(b);
                }


                //else do nothing move to the new users.
                s.moveToNext();
            }
            variableUsed = true;

            Button b = new Button(getApplicationContext());
            b.setText("Back to Calculator");

            b.setOnClickListener(goBack(b));
            ((LinearLayout) findViewById(R.id.showMyVariables)).addView(b);

            // ((TextView)findViewById(R.id.textView12)).setText(varName);
            //  ((TextView)findViewById(R.id.textView13)).setText(equation);

        }
    }
    String showVariablesUser ="VARIABLES";
    //Overrides the onclickListener for the buttons being created above.
    View.OnClickListener getOnClickDoSomething(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
                String mathExpression = expression.getText();
                String buttonText = button.getText().toString();
                String[] getEquation = button.getText().toString().split("\\s+");
                String buttonName = "( " + getEquation[0] + " )";


                mathExpression = mathExpression + buttonName;
                operatorUsed = false;
                numbersRequired = false;

                // sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(mathExpression);
                // load the buttons fragment


                showButtons(v);


                getFragmentManager().executePendingTransactions();

                TextView text =  (TextView) findViewById(R.id.textView12);
                //String showVariablesUser =text.getText().toString();
                Toast.makeText(getApplication(),buttonText, Toast.LENGTH_SHORT).show();
                showVariablesUser = showVariablesUser + "\n" + buttonText;
                text.setText(showVariablesUser);
            }
        };
    }

    //Overrides the onclickListener for the buttons being created above.
    View.OnClickListener goBack(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                variableUsed = false;
                showButtons(v);
            }
        };
    }

    public int evalPostfixInt(String postfix, String original) {
        StringTokenizer tokenizer = new StringTokenizer(postfix);
        Stack valStack = new Stack();
        Intent i = new Intent(this, maths.class);
        String sendMsg = original + "\n";



        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            char c;
            if(token.length() >1)
            {
                c = token.charAt(token.length()-1);

                if(c == '.') // check if idiot used decimal inapropriatly EX: "9."
                {
                    c = token.charAt(0);
                }
                if(c=='-') // if the person was extra stupid EX: "-9."
                {
                    c = token.charAt(1);
                }
            }
            else
            {
                c = token.charAt(0);
            }

            if (Character.isDigit(c)) {
                valStack.push(new Integer(token));
            }
            else {
                int rightVal = ((Integer)valStack.pop()).intValue();
                int leftVal = ((Integer)valStack.pop()).intValue();
                int rslt;

                switch (c) {
                    case '+': rslt = leftVal + rightVal;   sendMsg = sendMsg + leftVal + " + " + rightVal + " = " + rslt + "\n"; break;
                    case '-': rslt = leftVal - rightVal;  sendMsg = sendMsg + leftVal + " - " + rightVal + " = " + rslt + "\n"; break;
                    case '*': rslt = leftVal * rightVal;  sendMsg = sendMsg + leftVal + " * " + rightVal + " = " + rslt + "\n"; break;
                    case '/': rslt = leftVal / rightVal; sendMsg = sendMsg + leftVal + " / " + rightVal + " = " + rslt + "\n"; break;
                    case '%': rslt = leftVal % rightVal; sendMsg = sendMsg + leftVal + " % " + rightVal + " = " + rslt + "\n"; break;
                    default:
                        throw new IllegalArgumentException("invalid postfix expression");
                }

                valStack.push(new Integer(rslt));
            }
        }

        int rslt = ((Integer)valStack.pop()).intValue();
        if (!valStack.empty()) {
            throw new IllegalArgumentException("invalid postfix expression");
        }

        sendMsg = sendMsg.toString() + " final result = " + rslt;
        showEquationSteps();
        answer = sendMsg;
        return rslt;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // checks to see which equation has been selected
    public boolean firstEquationSelected = false;
    public boolean secondEquationSelected = false;
    public boolean thirdEquationSelected = false;
    public boolean mainEquation = false;

    // strings containing saved data from highlighted sections
    public String highlightedSavedEquation = "";
    public void highlight(View v)
    {

        if(firstEquationSelected == false && secondEquationSelected == false && thirdEquationSelected==false)
        {
            EditText textBackground = (EditText) findViewById(v.getId());
            String idAsString = v.getResources().getResourceName(v.getId());
            idAsString = idAsString.substring(idAsString.length()-9, idAsString.length());
            textBackground.setBackgroundColor(Color.argb(100, 200, 100, 100));
            if(idAsString.equalsIgnoreCase( "editText1"))
            {
                if(mainEquation == true)
                {
                    EquationFragment expression = (EquationFragment)getFragmentManager().findFragmentById(R.id.equation_Frame);
                    String mathExpression = expression.getText();

                    EditText editText = (EditText)v.findViewById(R.id.editText1);

                    editText.setText(mathExpression);
                }
                else
                {
                    EditText editText = (EditText)v.findViewById(R.id.editText1);
                    highlightedSavedEquation = editText.getText().toString();

                }
                firstEquationSelected = true;
            }
            else if(idAsString.equalsIgnoreCase( "editText2"))
            {
                if(mainEquation == true)
                {
                    EquationFragment expression = (EquationFragment)getFragmentManager().findFragmentById(R.id.equation_Frame);
                    String mathExpression = expression.getText();

                    EditText editText = (EditText)v.findViewById(R.id.editText2);
                    editText.setText(mathExpression);
                    // textBackground.setBackgroundColor(Color.argb(0,0,0,0));
                    // secondEquationSelected = false;
                }
                else
                {
                    EditText editText = (EditText)v.findViewById(R.id.editText2);
                    highlightedSavedEquation = editText.getText().toString();
                }
                secondEquationSelected = true;
            }
            else if (idAsString.equalsIgnoreCase( "editText3"))
            {
                if(mainEquation == true)
                {
                    EquationFragment expression = (EquationFragment)getFragmentManager().findFragmentById(R.id.equation_Frame);
                    String mathExpression = expression.getText();

                    EditText editText = (EditText)v.findViewById(R.id.editText3);
                    editText.setText(mathExpression);
                    //textBackground.setBackgroundColor(Color.argb(0,0,0,0));
                    //thirdEquationSelected = false;
                }
                else
                {
                    EditText editText = (EditText)v.findViewById(R.id.editText3);
                    highlightedSavedEquation = editText.getText().toString();

                }
                thirdEquationSelected = true;
            }
            else // if we are using the main equation
            {
                if(mainEquation==false)
                {
                    textBackground.setBackgroundColor(Color.argb(100, 200, 100, 100));
                    mainEquation = true;
                }
                else  // one of our saved equations is highlighted, save it apropriately
                {
                    textBackground.setBackgroundColor(Color.argb(0,0,0,0));
                    mainEquation = false;
                }
            }
        }
        else // we have something selected
        {
            EditText textBackground = (EditText) findViewById(v.getId());
            String idAsString = v.getResources().getResourceName(v.getId());
            idAsString = idAsString.substring(idAsString.length()-9, idAsString.length());
            textBackground.setBackgroundColor(Color.argb(0,0,0,0));
            if(idAsString.equalsIgnoreCase( "editText1"))
            {
                firstEquationSelected = false;
            }
            else if(idAsString.equalsIgnoreCase( "editText2"))
            {
                secondEquationSelected = false;
            }
            else if (idAsString.equalsIgnoreCase( "editText3"))
            {
                thirdEquationSelected = false;
            }
            else // if we are using the main equation
            {
                // expression.setEquation(idAsString);
                if(mainEquation==false)
                {
                    EquationFragment expression = (EquationFragment)getFragmentManager().findFragmentById(R.id.equation_Frame);
                    expression.setEquation(highlightedSavedEquation);

                    textBackground.setBackgroundColor(Color.argb(100, 200, 100, 100));
                    mainEquation = true;
                }
                else // one of our saved equations is highlighted, save it apropriately
                {
                    mainEquation = false;
                }
            }
        }
    }
    public void saveEquations(View v)
    {
        //Toast.makeText(getApplication(), "Hit", Toast.LENGTH_SHORT).show();
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase db = ndbh.getReadableDatabase();
        ContentValues cv = new ContentValues();

        // grabs all the current information to add to the database
        String equationOneName = ((EditText) findViewById(R.id.editText9)).getText().toString();
        String equationOne = ((EditText) findViewById(R.id.editText1)).getText().toString();
        String equationTwoName = ((EditText) findViewById(R.id.editText10)).getText().toString();
        String equationTwo = ((EditText) findViewById(R.id.editText2)).getText().toString();
        String equationThreeName = ((EditText) findViewById(R.id.editText11)).getText().toString();
        String equationThree = ((EditText) findViewById(R.id.editText3)).getText().toString();

        String[] projection = {
                NumberContract.EquationEntry.EQUATION_CURRENT_USER,
                NumberContract.EquationEntry.SAVED_EQUATION_ONE,
                NumberContract.EquationEntry.SAVED_EQUATION_ONE_NAME,
                NumberContract.EquationEntry.SAVED_EQUATION_TWO,
                NumberContract.EquationEntry.SAVED_EQUATION_TWO_NAME,
                NumberContract.EquationEntry.SAVED_EQUATION_THREE,
                NumberContract.EquationEntry.SAVED_EQUATION_THREE_NAME,
        };

        //SELECT * FROM equation
        Cursor s = db.query(
                NumberContract.EquationEntry.EQUATION_TABLE_NAME,
                projection,
                null,  //String
                null,  //String[]
                null,
                null,
                null,
                null
        );

        s.moveToFirst();
        String currentUser = "";

        while (!s.isAfterLast()) {
            currentUser = s.getString(
                    s.getColumnIndexOrThrow(
                            NumberContract.EquationEntry.EQUATION_CURRENT_USER));

            if(currentUser.equalsIgnoreCase(currentUserId))
            {
               // if we have found the current users saved equations row,
                // first delete it,
/*
                String table = "NumberContract.EquationEntry.EQUATION_TABLE_NAME";
                String where = "_ID = ?";
                String[] args = { currentUserid };
                db.delete(table, whereClause, whereArgs);
               */

                String table = NumberContract.EquationEntry.EQUATION_TABLE_NAME;
                String where =  NumberContract.EquationEntry.EQUATION_CURRENT_USER + " = ?";
                String[] args = { currentUserId };
                db.delete(table, where, args);

                // then add the new data

                //creating the database
                cv.put(NumberContract.EquationEntry.EQUATION_CURRENT_USER, currentUserId);
                // could also be written cv.put("username", id);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_ONE, equationOne);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_ONE_NAME, equationOneName);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_TWO, equationTwo);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_TWO_NAME, equationTwoName);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_THREE, equationThree);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_THREE_NAME, equationThreeName);

                //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
                sdb.insert(NumberContract.EquationEntry.EQUATION_TABLE_NAME, "null", cv);
                sdb.close();
                Toast.makeText(getApplication(), "SUCCESS! Your Favorite equations were updated was created", Toast.LENGTH_SHORT).show();
            }


            //else do nothing move to the other users.
            //Toast.makeText(getApplication(), "rwar" + currentUserId + " c " + currentUser, Toast.LENGTH_SHORT).show();
            s.moveToNext();
        }
      //  Toast.makeText(getApplication(), "While Loop terminated", Toast.LENGTH_SHORT).show();
        //displayNewStuff(v);
    }


    public void clearVariables(View v)
    {
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        sdb.delete(NumberContract.VariableEntry.VARIABLE_TABLE_NAME,null,null);
    }
    public void saveData(View v){
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase s = n.getWritableDatabase();

        ContentValues cv = new ContentValues();
        String id = ((EditText)findViewById(R.id.editText5)).getText().toString(); // password
        String pas = ((EditText)findViewById(R.id.editText6)).getText().toString(); // username
        String confirmPass = ((EditText)findViewById(R.id.secondPass)).getText().toString();  // backup password
        // Toast.makeText(getApplication(), "pas " + pas + " id " + id + " confirmpas " + confirmPass, Toast.LENGTH_SHORT).show();
        if(pas.equalsIgnoreCase(confirmPass)) // check to see if passwords match
        {
            // the following checks to see if a user is already in the database,
            // if they are it sets a boolean value to true and submits an error toast message
            // if the user does not exist, create the user
            boolean userExists = false;

            NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
            SQLiteDatabase db = ndbh.getReadableDatabase();

            String[] projection = {
                    NumberContract.NumberEntry._ID,
                    NumberContract.NumberEntry.COLUMN_NAME_ID,
                    NumberContract.NumberEntry.COLUMN_NAME_VALUE,
            };

            //SELECT * FROM numbers
            Cursor c = db.query(
                    NumberContract.NumberEntry.USER_TABLE_NAME,
                    projection,
                    null,  //String
                    null,  //String[]
                    null,
                    null,
                    null,
                    null
            );

            int totalUserCount = 1;
            c.moveToFirst();
            String display = "";
            while (!c.isAfterLast()) {
                display = c.getString(
                        c.getColumnIndexOrThrow(
                                NumberContract.NumberEntry.COLUMN_NAME_ID)); // gets user id's
                if(display.equalsIgnoreCase(id))
                {
                    userExists = true;
                }
                totalUserCount++;
                c.moveToNext();
            }
            if(!userExists) // if the user does not exist. add them to the database
            {
                String defaultSavedEquations = "SavedEquationSlot";
                //creating the database
                cv.put(NumberContract.NumberEntry.COLUMN_NAME_ID, id);
                // could also be written cv.put("username", id);
                cv.put(NumberContract.NumberEntry.COLUMN_NAME_VALUE, pas);

                //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
                s.insert(NumberContract.NumberEntry.USER_TABLE_NAME, "null", cv);
                s.close();
                Toast.makeText(getApplication(), "SUCCESS! Your account was created", Toast.LENGTH_SHORT).show();
                createDefaultSavedEquations(totalUserCount);
            }
            else
            {
                Toast.makeText(getApplication(), "User already exists", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplication(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }
    public void createDefaultSavedEquations(int i)
    {
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase db = ndbh.getReadableDatabase();
        ContentValues cv = new ContentValues();

        // grabs all the current information to add to the database
        String equationOneName = "Equation One Name";
        String equationOne = "Double tap to Highlight";
        String equationTwoName = "Equation Two Name";
        String equationTwo = "While highlighted, double tap calculator equation to import a favorite equation";
        String equationThreeName = "Equation Three Name";
        String equationThree = "Reverse Order to save a favorite Equation";
        //currentUserId = NumberContract.NumberEntry._ID;
                //creating the database
        String temp = ""+i;
                cv.put(NumberContract.EquationEntry.EQUATION_CURRENT_USER, temp);
                // could also be written cv.put("username", id);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_ONE, equationOne);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_ONE_NAME, equationOneName);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_TWO, equationTwo);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_TWO_NAME, equationTwoName);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_THREE, equationThree);
                cv.put(NumberContract.EquationEntry.SAVED_EQUATION_THREE_NAME, equationThreeName);

                //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
                sdb.insert(NumberContract.EquationEntry.EQUATION_TABLE_NAME, "null", cv);
                sdb.close();
            }






    public void login(View v)
    {
        NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase db = ndbh.getReadableDatabase();

        String pas = ((EditText)findViewById(R.id.editText4)).getText().toString(); // password
        String id = ((EditText)findViewById(R.id.editText)).getText().toString(); // username

        String[] projection = {
                NumberContract.NumberEntry._ID,
                NumberContract.NumberEntry.COLUMN_NAME_ID,
                NumberContract.NumberEntry.COLUMN_NAME_VALUE,
        };

        //SELECT * FROM numbers
        Cursor s = db.query(
                NumberContract.NumberEntry.USER_TABLE_NAME,
                projection,
                null,  //String
                null,  //String[]
                null,
                null,
                null,
                null
        );

        s.moveToFirst();
        String display = "";
        boolean foundUser = false;
        while(!s.isAfterLast()){
            display = s.getString(
                    s.getColumnIndexOrThrow(
                            NumberContract.NumberEntry.COLUMN_NAME_ID));
            if(display.equalsIgnoreCase(id)) // check to see if usernames are in database
            {
                foundUser = true;
                display = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.NumberEntry.COLUMN_NAME_VALUE));
                if(display.equalsIgnoreCase(pas)) // if they are, does their password match?
                {
                    currentUserId = s.getString(
                            s.getColumnIndexOrThrow(
                                    NumberContract.NumberEntry._ID));
                    // Toast.makeText(getApplication(), currentUserId, Toast.LENGTH_SHORT).show();
                    showButtons(v);

                    // wait for the new fragment to load.
                    getFragmentManager().executePendingTransactions();

                    loadFavoriteEquations();
                }
                else
                {
                    Toast.makeText(getApplication(), "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            }
            //else do nothing move to the new users.
            s.moveToNext();
        }
        if(foundUser == false)
        {
            Toast.makeText(getApplication(), "Invalid Username", Toast.LENGTH_SHORT).show();
        }
        //((TextView)findViewById(R.id.dbValues)).setText(display);
    }

    public void logout(View v)
    {
        loginFragment showMyButtons = new loginFragment();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();
    }

    public void loadFavoriteEquations()
    {
        //Toast.makeText(getApplication(), "Hit", Toast.LENGTH_SHORT).show();
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase db = ndbh.getReadableDatabase();
        ContentValues cv = new ContentValues();

        // grabs all the current information to add to the database
        EditText equationOneName = ((EditText) findViewById(R.id.editText9));
        EditText equationOne = ((EditText) findViewById(R.id.editText1));
        EditText equationTwoName = ((EditText) findViewById(R.id.editText10));
        EditText equationTwo = ((EditText) findViewById(R.id.editText2));
        EditText equationThreeName = ((EditText) findViewById(R.id.editText11));
        EditText equationThree = ((EditText) findViewById(R.id.editText3));

        String[] projection = {
                NumberContract.EquationEntry.EQUATION_CURRENT_USER,
                NumberContract.EquationEntry.SAVED_EQUATION_ONE,
                NumberContract.EquationEntry.SAVED_EQUATION_ONE_NAME,
                NumberContract.EquationEntry.SAVED_EQUATION_TWO,
                NumberContract.EquationEntry.SAVED_EQUATION_TWO_NAME,
                NumberContract.EquationEntry.SAVED_EQUATION_THREE,
                NumberContract.EquationEntry.SAVED_EQUATION_THREE_NAME,
        };

        //SELECT * FROM equation
        Cursor s = db.query(
                NumberContract.EquationEntry.EQUATION_TABLE_NAME,
                projection,
                null,  //String
                null,  //String[]
                null,
                null,
                null,
                null
        );

        s.moveToFirst();
        String currentUser = "";
        String firstE, firstEn, secondE, secondEn, thirdE, thirdEn;
        while (!s.isAfterLast()) {
            currentUser = s.getString(
                    s.getColumnIndexOrThrow(
                            NumberContract.EquationEntry.EQUATION_CURRENT_USER));

            if(currentUser.equalsIgnoreCase(currentUserId))
            {
                // When we find the current user, load their favorite equations
                firstE = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.EquationEntry.SAVED_EQUATION_ONE));
                firstEn = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.EquationEntry.SAVED_EQUATION_ONE_NAME));
                secondE = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.EquationEntry.SAVED_EQUATION_TWO));
                secondEn = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.EquationEntry.SAVED_EQUATION_TWO_NAME));
                thirdE = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.EquationEntry.SAVED_EQUATION_THREE));
                thirdEn = s.getString(
                        s.getColumnIndexOrThrow(
                                NumberContract.EquationEntry.SAVED_EQUATION_THREE_NAME));

                 equationOneName.setText(firstEn);
                 equationOne.setText(firstE);
                 equationTwoName.setText(secondEn);
                 equationTwo.setText(secondE);
                 equationThreeName.setText(thirdEn);
                 equationThree.setText(thirdE);
            }


            //else do nothing move to the other users.
            //Toast.makeText(getApplication(), "rwar" + currentUserId + " c " + currentUser, Toast.LENGTH_SHORT).show();
            s.moveToNext();
        }
    }


}