package com.cssd.ehealthcompanionapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cssd.ehealthcompanionapp.MainApp;
import com.cssd.ehealthcompanionapp.R;
import com.cssd.ehealthcompanionapp.data.services.PatientsService;
import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

import static com.cssd.ehealthcompanionapp.parameters.RequestCodes.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity implements SignInButton.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;

    private FirebaseAuth fAuth;
    private Button guestButton;
    final public static String TAG = LoginActivity.class.getSimpleName();
    private FirebaseUser user;

    private DatabaseReference databaseReference;

    PatientsService patientsService = PatientsService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = fAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            signInButton.setVisibility(View.VISIBLE);
        } else {


            signInButton.setVisibility(View.GONE);

            MainApp.setFirebaseUser(currentUser);

            patientsService.find(currentUser.getUid(), r-> {
                if (r.isSuccess()) {
                    if (r.get().getFirstName() != null) {
                        Intent intent = new Intent(this, ConnectGoogleFitActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        startAccountSetup();
                    }
                }
                else {
                    startAccountSetup();
                }

            });
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sign_in_button) {
            gSignIn();
        }
    }

    private void gSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    void addAccountToDatabase(FirebaseUser currentUser) {
        GenericAccount genericAccount = new GenericAccount(currentUser);
        patientsService.add(genericAccount, r -> updateUI(currentUser));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (account != null) {
                AuthCredential credentials = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                fAuth.signInWithCredential(credentials).addOnSuccessListener(l -> {
                    patientsService.find(Objects.requireNonNull(l.getUser()).getUid(), r -> {
                        if (r.isSuccess()) {
                            if (r.get().getFirstName() != null) {
                                Intent intent = new Intent(this, ConnectGoogleFitActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                        else {
                            addAccountToDatabase(l.getUser());
                        }
                    });
                });
            } else {
                Log.e(TAG, "signInResult:failed code=", completedTask.getException());
                updateUI(null);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void startAccountSetup() {
        Intent intent = new Intent(this, AccountSetupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
