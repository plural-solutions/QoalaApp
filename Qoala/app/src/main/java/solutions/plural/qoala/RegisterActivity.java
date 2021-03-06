package solutions.plural.qoala;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import solutions.plural.qoala.utils.HttpMethod;
import solutions.plural.qoala.utils.HttpStatusCode;
import solutions.plural.qoala.utils.JSONAPI;
import solutions.plural.qoala.utils.JsonTask;
import solutions.plural.qoala.utils.Util;

public class RegisterActivity extends Activity {

    private EditText edtEmail = null;
    private EditText edtPassword = null;
    private EditText edtUsername = null;

    public Context getContext() {
        return RegisterActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtUsername = (EditText) findViewById(R.id.edtUserName);


        Bundle bundle = getIntent().getExtras();
        String email = null;
        String pwd = null;

        if (bundle != null) {
            bundle.getString("email");

            if (bundle.containsKey("email")) {
                email = bundle.getString("email");
            }
            if (bundle.containsKey("pwd")) {
                pwd = bundle.getString("pwd");
            }
        }
        edtEmail.setText(email);
        edtPassword.setText(pwd);

    }

    public void registerClick(View v) {

        if (edtUsername.getText().length() == 0) {
            Snackbar.make(edtUsername, R.string.error_field_required, Snackbar.LENGTH_LONG).show();
            if (edtUsername.isFocusable() && !edtUsername.isFocused())
                edtUsername.requestFocus();
            return;
        }
        if (!Util.isValidEmail(edtEmail.getText().toString())) {
            Snackbar.make(edtEmail, R.string.error_invalid_email, Snackbar.LENGTH_LONG).show();
            if (edtEmail.isFocusable())
                edtEmail.requestFocus();
            return;
        }
        if (edtPassword.getText().length() == 0) {
            Snackbar.make(edtPassword, R.string.error_field_required, Snackbar.LENGTH_LONG).show();
            if (edtPassword.isFocusable())
                edtPassword.requestFocus();
            return;
        }
        try {
            JSONStringer json = new JSONStringer()
                    .object()
                    .key("name").value(edtUsername.getText().toString())
                    .key("email").value(edtEmail.getText().toString())
                    .key("password").value(edtPassword.getText().toString())
                    .endObject();
            new RegisterTask().execute(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Task para registro de usuario
     */
    private class RegisterTask extends JsonTask {

        @Override
        protected void setConfig() {
            this.context = getContext();
            this.action = "Accounts/Register";
            this.httpMethod = HttpMethod.POST;
        }

        @Override
        protected boolean onPostExecuted(int responseCode, String responseMessage, JSONObject jsonObject) {
            switch (responseCode) {
                case HttpStatusCode.Created:
                    //todo: receber ok do login e registrar token
                    if (jsonObject.has(JSONAPI.json_token)) {
                        String token = jsonObject.optString(JSONAPI.json_token);
                        Intent i = getIntent();
                        i.putExtra(JSONAPI.json_token, token);
                        setResult(RESULT_OK, i);
                        finishActivity(101);
                        finish();
                    }
                    return true;
            }
            return false;
        }
    }
}
