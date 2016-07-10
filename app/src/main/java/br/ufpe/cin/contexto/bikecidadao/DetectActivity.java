package br.ufpe.cin.contexto.bikecidadao;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import br.ufpe.cin.db.bikecidadao.LocalRepositoryController;


import java.util.List;

public class DetectActivity extends IntentService  implements LocationListener, GoogleApiClient.ConnectionCallbacks {

    public DetectActivity() {
        super("DetectActivity");
    }
    private LocalRepositoryController localRepositoryController;
    private static final String TAG = "DetectActivity";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        localRepositoryController = new LocalRepositoryController(this);
        setOnBike(false);
        callGoogleApiConnection();
    }

    private void callGoogleApiConnection() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(ActivityRecognition.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Intent intent = new Intent(this, DetectActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 3000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //sambora: Handle do tipo de evento
    @Override
    public void onHandleIntent(Intent intent) {

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            //handleDetectedActivities( result.getProbableActivities() );
            handleDetectedActivities( result.getMostProbableActivity() );            
            //Log.v(TAG,result.toString());
        }
    }

    private void handleDetectedActivities(DetectedActivity mostProbableActivity) {
        Log.v(TAG,"mostProbableActivity: " + mostProbableActivity.toString());
        setActivity(mostProbableActivity.toString());
        // usando ON_FOOT apenas para facilitar os testes, alterar para ON_BICYCLE na versão final
        if ( mostProbableActivity.getType() == DetectedActivity.ON_FOOT ){
            setOnBike(true);
        }else{
            setOnBike(false);
        }
    }


    //sambora: Verifica o tipo para iniciar o parar o tracking
//    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
//        //Log.v(TAG,probableActivities.toString());
//        for (DetectedActivity activity: probableActivities) {
//
//            switch (activity.getType()) {
//                case DetectedActivity.ON_FOOT : {
//                    if (activity.getConfidence() > 75) {
//                        setOnBike(true);
//                    }
//                    break;
//                }
//                default: {
//
//                    setOnBike(false);
//                    break;
//                }
//            }
//        }
//    }
    private void setOnBike(boolean onBike){
        localRepositoryController.setOnBike(onBike);
        //Log.v(TAG,"setOnBike: " + onBike);
    }

    private void setActivity(String activity){
        localRepositoryController.setActivity(activity);
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
