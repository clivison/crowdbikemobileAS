package br.ufpe.cin.util.bikecidadao;

import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.Ocorrencia;


public interface OnGetOccurrencesCompletedCallback {

    void onGetOccurrencesCompleted(List<Ocorrencia> occurrences);
}
