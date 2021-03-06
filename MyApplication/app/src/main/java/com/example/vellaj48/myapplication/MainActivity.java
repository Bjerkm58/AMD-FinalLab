package com.example.vellaj48.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.lang.*;

public class MainActivity extends ActionBarActivity {

    public View.OnClickListener getOnClickDoSomething(final Button button)  {
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
    public View.OnClickListener goBack(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                variableUsed = false;
                showButtons(v);
            }
        };
    }
    public boolean usingGuestButtons = false;
    public boolean decimalUsed = false;
    public boolean numbersRequired = false;  // used only for after decimal used
    public boolean operatorRequired = false;  // used only for after right parentheses ")"
    public boolean operatorUsed = true;
    public boolean variableUsed = false;
    public boolean creatingVariables = false; // used to diferentiate between where buttons are directed.
    public boolean firstEquationSelected = false;
    public boolean secondEquationSelected = false;
    public boolean thirdEquationSelected = false;
    public boolean mainEquation = false;
    public int leftParenthesesUsed = 0;
    public int decimalWasUsed = 0;

    public String savedEquation = "";
    public String currentUserId = "";
    public String highlightedSavedEquation = "";
    public final static String MY_MSG = "com.example.erik.myfirstapp.MESSAGE";
    public String showVariablesUser ="VARIABLES";
    public String answer = "";

    public boolean[] saveBooleanStates = new boolean[5]; // saves the states of boolean values when we move to create variables. //Initializer stuff

    public HashMap<String, String> userDB = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager()
            .beginTransaction()
            .add(R.id.button_Frame, new loginFragment())
            .add(R.id.equation_Frame, new EquationFragment())
            .add(R.id.favorite_equation_frame, new favoriteEquations())
        .commit();
        pulluserDB();
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
    public void addNumbers(View v) {
        if(expression().getText().length() == 0) {
            expression().append(button(v));
            operatorUsed = false;
            validityCheck();
        }
        else if(!lastCharIs(')')){
            expression().append(button(v));
            operatorUsed = false;
            validityCheck();
        }
        else {

        }

    }
    public void addDecimal(View v) {
        if (!decimalUsed) {
            // gets the editText containing the equation in the fragment fragment_equation.xml
            expression().append(button(v));
            decimalUsed = true;
            operatorUsed = false;
        }
    }
    public void addParentheses(View v) {
        if(nAn()) {
            if ((button(v).equals("(")))
            // we are adding a left parentheses after an operation  ex: "9 + "
            // OR we are adding a parentheses to an empty equation ex: ""
            {
                expression().append(button(v) + " ");
                // updates misc variables preventing user error
                leftParenthesesUsed++;

            } else if (leftParenthesesUsed > 0) // (parentheses == ')')    aka right parentheses is used
            {
                // updates the expression
                expression().append(" " + button(v));
                // updates misc variables preventing user error
                leftParenthesesUsed--;
                validityCheck();
                operatorUsed = true;
            }
        }
    }
    public void addOperators(View v) {
        if (!nAn()) {
                // gets the editText containing the equation in the fragment fragment_equation.xml
                expression().append(" " + button(v) + " ");
                variableUsed = false;
                operatorUsed = true;
                decimalUsed = false;
            }
        }
    public void addNewVariable(View v) {
        ContentValues cv = new ContentValues();
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        Cursor s = varCursor();
        s.moveToFirst();
        boolean foundVarName = false, foundEquation = false;
        while (!s.isAfterLast()) {

            if(currentUser(s).equalsIgnoreCase(currentUserId))
            {
                if (dbVarName(s).equalsIgnoreCase(varName())) // does this variable name exist?
                {
                    foundVarName = true;
                    break;
                }
                if (dbEquation(s).equalsIgnoreCase(equation())) // does equation already exist?
                {
                    foundEquation = true;
                    break;
                }
            }
            //else do nothing move to the new users.
            s.moveToNext();
        }
        if (foundVarName) {
            Toast.makeText(getApplication(), "Variable Name already exists", Toast.LENGTH_SHORT).show();
        }
        if (foundEquation) {
            Toast.makeText(getApplication(), "Equation already exists", Toast.LENGTH_SHORT).show();
        }
        if (!foundVarName && !foundEquation) {
            // re-enables greyed out buttons
            enableButtons();
            // restores all boolean values as we are going back to the calculator

            decimalUsed = saveBooleanStates[0];
            numbersRequired =saveBooleanStates[1];
            operatorRequired = saveBooleanStates[2];
            operatorUsed = saveBooleanStates[3];
            variableUsed =  saveBooleanStates[4];

            String defaultSavedEquations = "SavedEquationSlot";
            //creating the database
            cv.put(NumberContract.VariableEntry.VARIABLE_NAME, varName());
            // could also be written cv.put("username", id);
            cv.put(NumberContract.VariableEntry.VARIABLE_EQUATION, equation());
            cv.put(NumberContract.VariableEntry.VARIABLE_CURRENT_USER, currentUserId);
            //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
            sdb.insert(NumberContract.VariableEntry.VARIABLE_TABLE_NAME, "null", cv);
            sdb.close();
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.equation_Frame)).commit();
            getFragmentManager().beginTransaction().add(R.id.equation_Frame, new EquationFragment()).commit();
            creatingVariables = false;
            // waits for the commits to execute BEFORE trying to do stuff to the newly loaded fragments text.
            getFragmentManager().executePendingTransactions();
            expression().setText(savedEquation);
        }
    }
    public void backSpace(View v) {
            String expression = expression().getText().toString();

            if (expression.length() > 0) // if we actually have an expression
            {
                if (lastCharIs('.')) // removes decimals
                {
                    // sets the new string to the equation editText in fragment_equation.xml
                    expression().setText(expression.substring(0, expression.length() - 1));

                    // updates misc variables preventing user error
                    decimalUsed = false;
                    operatorUsed = false;
                    numbersRequired = false;
                    decimalWasUsed--;
                } else if (lastCharIs(' ')) // if we have a blank space we are reading a parentheses or an operator
                {
                    if (lastCharIs('(') && expression.length() > 2)  // all expressions NOT starting with "(" ex "8 + ( 9 )"
                    {
                        // reduces the total number counter of left parentheses used
                        leftParenthesesUsed--;
                        validityCheck();
                        // sets the new equation to the editText in fragment_equation.xml
                        expression().setText(expression.substring(0, expression.length()-2));

                    } else if (lastCharIs('(')  && expression.length() == 2) // if our first character is a "(" ex "("
                    {
                        leftParenthesesUsed--;
                        validityCheck();
                        expression().setText(expression.substring(0, expression.length() - 2));

                    } else // reading an operator ex "% * + - /"
                    {
                        expression().setText(expression.substring(0, expression.length() - 3));
                        operatorUsed = false;
                        validityCheck();
                    }
                } else if (lastCharIs(')')) // if we are reading a right parentheses
                {
                    // adds to the number of left parentheses used because we now have 1 less right parentheses
                    leftParenthesesUsed++;
                    operatorRequired = false;

                    // makes the equal button clickable
                    validityCheck();
                    expression().setText(expression.substring(0, expression.length() - 2));
                } else // otherwise we are reading a number
                {
                    if (lastCharIs(' '))// if there is an empty space we are done with numbers
                    {
                        operatorUsed = true;
                        expression().setText(expression.substring(0, expression.length() - 1));
                    } else {
                        expression().setText(expression.substring(0, expression.length() - 1));
                        operatorUsed = false;
                    }
                }
            }
        }
    public void makeNegative(View v) {
        // gets the editText containing the equation in the fragment fragment_equation.xml
       String expression = expression().getText().toString();
        if (expression.length() > 0) {
            String[] elements = expression.split("\\s+"); // delimit by whitespace throw into an array

            // if what we have is a digit
            if (Character.isDigit(elements[elements.length - 1].charAt(0))
                    || elements[elements.length - 1].charAt(0) == '.') {
                elements[elements.length - 1] = "-" + elements[elements.length - 1];
                expression = elements[0];
                for (int i = 1; i < elements.length; i++) {
                    expression = expression + " " + elements[i];
                }
            }
            // if its a negative sign,
            else if (elements[elements.length - 1].charAt(0) == '-') {
                if (elements[elements.length - 1].length() > 1) {
                    elements[elements.length - 1] = elements[elements.length - 1].substring(1);
                    expression = elements[0];
                    for (int i = 1; i < elements.length; i++) {
                        expression = expression + " " + elements[i];
                    }
                }

            }
            expression().setText(expression);
        }


    }
    public void clearExpression(View v) {

        // gets the editText containing the equation in the fragment fragment_equation.xml
        expression().setText("");
        operatorUsed = true;
        decimalUsed = false;
        leftParenthesesUsed = 0;
        numbersRequired = false;  // used only for after decimal used
        operatorRequired = false;  // used only for after right parentheses ")"
        variableUsed = false;


    }
    public void evaluate(View v) {
        // gets the editText containing the equation in the fragment fragment_equation.xml

        if(!numbersRequired && leftParenthesesUsed == 0 && !operatorUsed)
        {
            EquationFragment expression = (EquationFragment) getFragmentManager().findFragmentById(R.id.equation_Frame);
            String mathExpression = expression.getText();

            String[] getEquation = mathExpression.split("\\s+");// tokenize mathExpression to delimit variables
            mathExpression = ""; // reset mathExpression to be re-created during for loop later
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
                value = evalPostfix(value, mathExpression) + "";
                decimalUsed = true;
                operatorUsed = false;
                leftParenthesesUsed = 0;
                showVariablesUser ="VARIABLES";
                //sets the new string to the equation editText in fragment_equation.xml
                expression.setEquation(value);
            }
        }
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
    public void createVariablesFragment(View v){
        // greys out apropriate buttons
        disableButtons();

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
        showVariableCreatorFragment(v);



    }
    public void addVariables(View v){
        if(variableUsed == false && operatorUsed == true) {
            // pull the variables out of the databse & dbVarName them on the page as buttons enclosed in parentheses
            // example: the equation 5/(7+2) has a variable name foo,
            // button looks like:  [ foo = (5/(7+2)) ] where the [] brackets are the button itself.
            // this way you can simpl
            // when a variable is selected it is put into main equation up top
            // then you are taken back to the main buttons page so that you can use the other buttons
            // will be graphically pleasing
            showVariables showMyButtons = new showVariables();

            TextView text =  (TextView) findViewById(R.id.textView12);
            String showVariablesUser = text.getText().toString();
            //Toast.makeText(getApplication(),showVariablesUser, Toast.LENGTH_SHORT).show();
            showVariablesUser = text.getText().toString();
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
    public int  evalPostfixInt(String postfix, String original) {
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
    public void highlight(View v){

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
    public void saveEquations(View v){
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
    public void clearVariables(View v){
        NumberDBHelper n = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase sdb = n.getWritableDatabase();
        sdb.delete(NumberContract.VariableEntry.VARIABLE_TABLE_NAME,null,null);
    }
    public void saveData(View v){
        String id = ((EditText)loginFrame().findViewById(R.id.editText5)).getText().toString(); // password
        String pas = ((EditText)loginFrame().findViewById(R.id.editText6)).getText().toString(); // username
        String confirmPass = ((EditText)loginFrame().findViewById(R.id.secondPass)).getText().toString();  // backup password
        // Toast.makeText(getApplication(), "pas " + pas + " id " + id + " confirmpas " + confirmPass, Toast.LENGTH_SHORT).show();
        if(pas.equalsIgnoreCase(confirmPass)) // check to see if passwords match
        {
            if(!userDB.containsKey(id)) // if the user does not exist. add them to the database
            {
                //INSERT INTO TABLE_NAME VALUES (whatever the values in each row are);
                pushuserintoDb(v);

                Toast.makeText(getApplication(), "SUCCESS! Your account was created", Toast.LENGTH_SHORT).show();
                //createDefaultSavedEquations(totalUserCount);
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
    public void createDefaultSavedEquations(int i){
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
    public void login(View v)throws UnsupportedEncodingException{
        String password = ((EditText)loginFrame().findViewById(R.id.editText4)).getText().toString();
        String username = ((EditText) loginFrame().findViewById(R.id.editText)).getText().toString(); // username
        if(userDB.containsKey(username)){
            if(userDB.get(username).equals(password)){
                showButtons(v);
                // wait for the new fragment to load.
                getFragmentManager().executePendingTransactions();

                loadFavoriteEquations();
            }
            else{
                Toast.makeText(getApplication(), "Invalid Password", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplication(), "Invalid Username", Toast.LENGTH_SHORT).show();
        }
    }
    public void logout(View v){
        loginFragment showMyButtons = new loginFragment();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();
    }
    public void guestLogout(View v){
        loginFragment showMyButtons = new loginFragment();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();

        // also re-load the favorite equation frame with default N/A states
        // may change later to only load when a user logs in.
        favoriteEquations showFavorites = new favoriteEquations();
        getFragmentManager().beginTransaction().add(R.id.favorite_equation_frame, showFavorites).commit();
        usingGuestButtons = false;
    }
    public void loadFavoriteEquations(){
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
    public void guestLogin(View v)    {
        showGuestButtons(v);
        usingGuestButtons = true;
    }

    // ALL UNFINISHED NEW METHODS TO FINISH THE FINAL LAB
    public void getFriendsEquation(View v)    {
        // when you click on one of a friends favorite equations,
        // you can import it as a VARIABLE to your variables table in the DB
    }
    public void findNewFriends(View v) {
        // this will corelate to a "search" button in the friends fragment
        // which will allow users to search the DB for friends with that username
        // if they find that friend the button to "add friend" will become un-hidden
    }
    public void addFriend(View v){
        // this button is only clickable after the findNewFriends button has been clicked AND
        // the findNewFriends button found a user with that username in the DB
        // when this button is clicked it adds the found user to the current users friends list
    }
    public void viewOPendingRequests(View v){
        /*
        as per krohns request, this button will display all of your
        pending invitations both incomming and outgoing
         */
    }
    public void inviteReceived(){
        // this method will display a notification
        // on the event that you have receieved a friend request
    }
    public void inviteAccepted(){
        // this method will display a notification
        // on the event an invitation of yours has been accepted.
    }
    ////Object references/////////////////////////////////////////////////////////

    public EditText expression() {
        return (creatingVariables) ? (EditText)(getFragmentManager().findFragmentById(R.id.equation_Frame)).getView().findViewById(R.id.editText8) : (EditText)(getFragmentManager().findFragmentById(R.id.equation_Frame)).getView().findViewById(R.id.equation);
    }
    public View loginFrame(){
       return ((getFragmentManager().findFragmentById(R.id.button_Frame)).getView());
    }
    public String button(View v){
        return ((Button) v.findViewById(v.getId())).getText().toString();
    }
    public String dbVarName(Cursor s){
        return s.getString(
                s.getColumnIndexOrThrow(
                        NumberContract.VariableEntry.VARIABLE_NAME));
    }
    public String currentUser(Cursor s){
        return s.getString(
                s.getColumnIndexOrThrow(
                        NumberContract.VariableEntry.VARIABLE_CURRENT_USER));
    }
    public String dbEquation(Cursor s){
        return s.getString(
                s.getColumnIndexOrThrow(
                        NumberContract.VariableEntry.VARIABLE_EQUATION));
    }
    public String varName(){
        return ((EditText)(getFragmentManager().findFragmentById(R.id.equation_Frame)).getView().findViewById(R.id.editText7)).getText().toString();
    }
    public String equation(){
        return ((EditText)(getFragmentManager().findFragmentById(R.id.equation_Frame)).getView().findViewById(R.id.editText8)).getText().toString();
    }
    public Cursor varCursor(){

        NumberDBHelper ndbh = new NumberDBHelper(getApplicationContext());
        SQLiteDatabase db = ndbh.getReadableDatabase();
        String[] projection = {
                NumberContract.VariableEntry.VARIABLE_NAME,
                NumberContract.VariableEntry.VARIABLE_EQUATION,
                NumberContract.VariableEntry.VARIABLE_CURRENT_USER,
        };

        //SELECT * FROM numbers
        return db.query(
                NumberContract.VariableEntry.VARIABLE_TABLE_NAME,
                projection,
                null,  //String
                null,  //String[]
                null,
                null,
                null,
                null
        );
    }

    ////Scripts/////////////////////////////////////////////////////////

    public void enableButtons(){
        if(usingGuestButtons == false) {
            findViewById(R.id.button29).setEnabled(true);
            findViewById(R.id.button7).setEnabled(true);
            findViewById(R.id.useSaved).setEnabled(true);
            findViewById(R.id.button28).setEnabled(true);
        }
        else // usingGuestButtons == true
        {
            findViewById(R.id.guestEquals).setEnabled(false);
        }
    }
    public void disableButtons(){
        if(usingGuestButtons == false) {
            findViewById(R.id.button29).setEnabled(false);
            findViewById(R.id.button7).setEnabled(false);
            findViewById(R.id.useSaved).setEnabled(false);
            findViewById(R.id.button28).setEnabled(false);
        }
        else // usingGuestButtons == true
        {
            findViewById(R.id.guestEquals).setEnabled(false);
        }

    }
    public void validityCheck(){
        if (usingGuestButtons == false) {
            findViewById(R.id.button7).setEnabled(!nAn());
        }
        else // usingGuestButtons == true
        {
            findViewById(R.id.guestEquals).setEnabled(false);

        }
    }


    //////Helper Methods/////////////////////////////////////////////////////////

    public boolean isNumeric(String str){
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
    public boolean nAn(char c){
        try{
            Double d = ((double) c);
            return false;
        }
        catch(NumberFormatException nfe)
        {
            return true;
        }
    }
    public boolean nAn(){
        return nAn(expression().getText().toString().charAt(expression().length() -1));
    }
    public boolean lastCharIs(char c){
        return expression().getText().toString().charAt(expression().length() - 1) == c;
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
    public void showFriendsSearch(View v) {
        find_New_Friends showMyButtons = new find_New_Friends();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.equation_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.equation_Frame, showMyButtons).commit();
    }
    public void showFriends(View v){
        friends_fragment showMyButtons = new friends_fragment();

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();
    }
    public void loadFriendsFragments(View v){
        showFriendsSearch(v);
        showFriends(v);
    }
    public void showGuestButtons(View v)   {
        guest_buttons showMyButtons = new guest_buttons();

        // remove the saved equations fragment as it is a registered users option
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.favorite_equation_frame)).commit();

        //do the rest of the stuff normalls
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.button_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.button_Frame, showMyButtons).commit();
    }
    public void showEquationSteps(View v) {  // used for showing equation steps button
        equationSteps expression = (equationSteps) getFragmentManager().findFragmentById(R.id.button_Frame);
        expression.setEquationSteps(answer);
    }
    public void showVariableCreatorFragment(View v) {
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.equation_Frame)).commit();
        getFragmentManager().beginTransaction().add(R.id.equation_Frame, new createVariables()).commit();
    }

    /////ExtDBCode////////////////////////////////////////////////////////////////
    private class InsertUserIntoDB extends AsyncTask<Long, Integer, Void> {
        @Override
        protected Void doInBackground(Long... v) {
            try {
                String username = ((EditText)findViewById(R.id.editText5)).getText().toString(); // password
               // byte[] shaPass  = DigestUtils.sha1();
                String password = ((EditText)findViewById(R.id.editText6)).getText().toString();
                userDB.put(username, password);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://softeng.cs.uwosh.edu/students/bjerkm58/insert.php");
                // Add your data via a POST command. insert.php accesses variable by using $_POST['variable']
                // See attached php page to see the whole example.
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                // There should probably be some kind of check to make sure the data was inserted
                // correctly. The webpage could echo out a "success" or some kind of failure code
                // if something went wrong. However, to keep this example short, I am just ignoring
                // the output and assuming inserting was a success.
            } catch (ClientProtocolException e) {
                Log.i("main", "bad thing happened");
            } catch (IOException e) {
                Log.i("main", "bad thing happened");
            }
            //If your method returns nothing, you can state the return type is Void.
            //However, Void is an Object, it is different from void. Therefore, you
            //must return something. This is why I return null.
            return null;
        }

        // This is called when doInBackground() is finished
        @Override
        protected void onPostExecute(Void result) {

        }
    }
    private class GetUserDB extends AsyncTask<Long, Integer, JSONArray> {
        @Override
        protected JSONArray doInBackground(Long... v) {
            //what gets returned at the end
            JSONArray jsonArray = null;

            try {
                //Code to connect to a web server and retrieve data from the database.
                //The actual select query is in the php file. You can pass data to the
                //select.php page using GET or POST commands and then use that data
                //to modify your query. For instance, instead of doing SELECT * FROM temp,
                //if a value were passed in, you could modify your query to be
                //SELECT * FROM temp WHERE var='a'
                //or something like that. See code above for how to pass data to the php page.
                //See attached php code to see query.
                HttpEntity entity = null;
                String url = "http://softeng.cs.uwosh.edu/students/bjerkm58/selectUsers.php";
                DefaultHttpClient dhc = new DefaultHttpClient();
                HttpGet hg = new HttpGet(url);
                HttpResponse hr = dhc.execute(hg);
                entity = hr.getEntity();
                String response = EntityUtils.toString(entity);
                response = response.substring(3);
                jsonArray = new JSONArray(response);
            } catch (ClientProtocolException e) {
                Log.i("main", "bad thing happened1");
            } catch (IOException e) {
                Log.i("main", "bad thing happened2");
            } catch (JSONException e){
                Log.i("main3", "bad thing 3");
            }

            return jsonArray;
        }

        // This is called when doInBackground() is finished
        @Override
        protected void onPostExecute(JSONArray result) {
            //not necessary to call a method in the activity but just
            //an example of how to do it.
            userDB = getUserDB(result);
        }
    }

    public void pushuserintoDb(View v){
        new InsertUserIntoDB().execute();
    }
    public void pulluserDB() {
        new GetUserDB().execute();
    }

    public HashMap<String, String> getUserDB(JSONArray arr){
        HashMap<String, String> result = new HashMap<>();
        if(arr.length() != 0) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject obj = arr.getJSONObject(i);
                    result.put(obj.getString("username"), obj.getString("password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }




}