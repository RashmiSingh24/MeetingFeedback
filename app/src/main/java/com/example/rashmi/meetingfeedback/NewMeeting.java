package com.example.rashmi.meetingfeedback;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;


public class NewMeeting extends AppCompatActivity {
    public final static String CLIENT_ID = "2705e6fa-b98d-4425-b2e5-436eb19d203e"; //This is your client ID
    public final static String REDIRECT_URI = "http://localhost"; //This is your redirect URI
    private static final String TAG = "MainActivity";
    private String[] scopes = new String[]{"https://outlook.office.com/Mail.Read"};
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    public final static String AUTHORITY_URL = "https://login.microsoftonline.com/common";  //COMMON OR YOUR TENANT ID

    private final static String AUTH_TAG = "auth"; // Search "auth" in your Android Monitor to see errors

    private AuthenticationContext mAuthContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meeting);

    }
    public void signIn (final View v)
    {
        mAuthContext = new AuthenticationContext(NewMeeting.this, AUTHORITY_URL, true);

        mAuthContext.acquireToken(
                NewMeeting.this,
                CLIENT_ID,
                CLIENT_ID,
                REDIRECT_URI,
                PromptBehavior.Auto,
                new AuthenticationCallback<AuthenticationResult>()
                {

                    @Override
                    public void onError(Exception e)
                    {
                        Log.e(AUTH_TAG, "Error getting token: " + e.toString());
                    }

                    @Override
                    public void onSuccess(AuthenticationResult result)
                    {
                        Log.v(AUTH_TAG, "Successfully obtained token, still need to validate");
                        if (result != null && !result.getAccessToken().isEmpty())
                        {
                            try
                            {
                                String firstName = result.getUserInfo().getGivenName();
                                String lastName = result.getUserInfo().getFamilyName();
                                Intent intent=new Intent(getApplicationContext(),UserDetails.class);
                                intent.putExtra("FirstName",firstName);
                                intent.putExtra("LastName",lastName);

                                startActivity(intent);

                                updateLoggedInUI(firstName, lastName);
                            }
                            catch (Exception e)
                            {
                                Log.e(AUTH_TAG, "Exception Generated, Unable to hit the backend: " + e.toString());
                            }
                        }
                        else
                        {
                            Log.e(AUTH_TAG, "Error: token came back empty");
                        }
                    }
                });
    }

   private void updateLoggedInUI(String firstName, String lastName)
    {
    /* Hide the sign in button */
        findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);

    /* Show the welcome message */
      /*  TextView signedIn = (TextView) findViewById(R.id.welcomeSignedIn);
        signedIn.setVisibility(View.VISIBLE);
        signedIn.setText("Welcome " + firstName + " " + lastName);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }


}