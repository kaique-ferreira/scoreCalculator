package com.aruana.scorecalculator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aruana.scorecalculator.networking.ScoreService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

const val SCORE_RESULT_BUNDLE = "SCORE_RESULT_BUNDLE"

class MainActivity : AppCompatActivity() {

    private val scoreCalculator = ScoreCalculator()
    private var disposable: Disposable? = null
    private var result: ScoreResult? = null
    private val retrofit: ScoreService = Retrofit.Builder()
            .baseUrl(ScoreService.API_ENDPOINT)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(ScoreService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fetchJsonData()
        } else {
            result = savedInstanceState.getParcelable(SCORE_RESULT_BUNDLE)
            showResults()
        }
    }

    private fun fetchJsonData() {
        progressBar.visibility = View.VISIBLE
        disposable = retrofit.get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    progressBar.visibility = View.GONE
                    result = scoreCalculator.calculateScore(it)
                    showResults()
                }, {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                })
    }

    private fun showResults() {
        if (result == null) {
            return
        }

        resultTextView.text = result.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SCORE_RESULT_BUNDLE, result)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
