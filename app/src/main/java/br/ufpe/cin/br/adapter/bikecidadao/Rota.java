package br.ufpe.cin.br.adapter.bikecidadao;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Rota {

	private Long idRota;
	private Integer routeCode;
	private ArrayList<LatLng> pontos = new ArrayList<>();

	public void addPonto(LatLng umPonto) {
		pontos.add(umPonto);
	}

	public void setPontos(ArrayList<LatLng> pontos) {
		this.pontos = pontos;
	}

	public ArrayList<LatLng> getPontos() {

		return pontos;
	}

	public Long getIdRota() {
		return idRota;
	}

	public void setIdRota(Long idRota) {
		this.idRota = idRota;
	}


	public void setRouteCode(Integer routeCode) {
		this.routeCode = routeCode;
	}

	public Integer getRouteCode() {
		return routeCode;
	}
}
