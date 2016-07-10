package br.ufpe.cin.util.bikecidadao;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by jal3 on 05/11/2015.
 */
public interface OnGetRoutesCompletedCallback {

    void onGetRoutesCompleted(List<LatLng> routes);
}
